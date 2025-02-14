package io.gitlab.arturbosch.detekt.rules.exceptions

import io.gitlab.arturbosch.detekt.test.compileAndLint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RethrowCaughtExceptionSpec {
    val subject = RethrowCaughtException()

    @Nested
    inner class `RethrowCaughtException rule` {

        @Test
        fun `reports when the same exception is rethrown`() {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalStateException) {
                        throw e
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `does not report when the other exception is rethrown with same name`() {
            val code = """
                class A {
                    private lateinit var e: Exception
                    fun f() {
                        try {
                        } catch (e: IllegalStateException) {
                            throw this.e
                        }
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `reports when the same exception succeeded by dead code is rethrown`() {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalStateException) {
                        throw e
                        print("log")
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports when the same nested exception is rethrown`() {
            val code = """
                fun f() {
                    try {
                    } catch (outer: IllegalStateException) {
                        try {
                        } catch (inner: IllegalStateException) {
                            throw inner
                        }
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `does not report a wrapped exception`() {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalStateException) {
                        throw IllegalArgumentException(e)
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report wrapped exceptions`() {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalStateException) {
                        throw IllegalArgumentException(e)
                    } catch (f: Exception) {
                        throw IllegalArgumentException("msg", f)
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report logged exceptions`() {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalStateException) {
                        print(e)
                    } catch (f: Exception) {
                        print(f)
                        throw IllegalArgumentException("msg", f)
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report when taking specific actions before throwing the exception`() {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalStateException) {
                        print("log") // taking specific action before throwing the exception
                        throw e
                    }
                    try {
                    } catch (e: IllegalStateException) {
                        print(e.message) // taking specific action before throwing the exception
                        throw e
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report when exception rethrown only in first catch`() {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalStateException) {
                        throw e
                    } catch (e: Exception) {
                        print(e)
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report when some work is done in last catch`() {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalStateException) {
                        throw e
                    } catch (e: Exception) {
                        print(e)
                        throw e
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `does not report when there is no catch clauses`() {
            val code = """
                fun f() {
                    try {
                    } finally {
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).isEmpty()
        }

        @Test
        fun `reports when exception rethrown in last catch`() {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalStateException) {
                        print(e)
                    } catch (e: Exception) {
                        throw e
                    }
                }
            """
            assertThat(subject.compileAndLint(code)).hasSize(1)
        }

        @Test
        fun `reports 2 violations for each catch`() {
            val code = """
                fun f() {
                    try {
                    } catch (e: IllegalStateException) {
                        throw e
                    } catch (e: Exception) {
                        // some comment
                        throw e
                    }
                }
            """
            val result = subject.compileAndLint(code)
            assertThat(result).hasSize(2)
            // ensure correct violation order
            assertThat(result[0].startPosition.line == 4).isTrue
            assertThat(result[1].startPosition.line == 7).isTrue
        }
    }
}
