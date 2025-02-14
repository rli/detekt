package io.gitlab.arturbosch.detekt.rules.naming

import io.gitlab.arturbosch.detekt.test.lint
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TopLevelPropertyNamingSpec {

    val subject = TopLevelPropertyNaming()

    @Nested
    inner class `constants on top level` {

        @Test
        fun `should not detect any constants not complying to the naming rules`() {
            val code = """
                const val MY_NAME_8 = "Artur"
                const val MYNAME = "Artur"
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should detect five constants not complying to the naming rules`() {
            val code = """
                const val MyNAME = "Artur"
                const val name = "Artur"
                const val nAme = "Artur"
                private const val _nAme = "Artur"
                const val serialVersionUID = 42L
            """
            assertThat(subject.lint(code)).hasSize(5)
        }
    }

    @Nested
    inner class `variables on top level` {

        @Test
        fun `should not report any`() {
            val code = """
                val name = "Artur"
                val nAme8 = "Artur"
                private val _name = "Artur"
                val serialVersionUID = 42L
                val MY_NAME = "Artur"
                val MYNAME = "Artur"
                val MyNAME = "Artur"
                private val NAME = "Artur"
                val s_d_d_1 = listOf("")
                private val INTERNAL_VERSION = "1.0.0"
            """
            assertThat(subject.lint(code)).isEmpty()
        }

        @Test
        fun `should report non private top level property using underscore`() {
            val code = """
                val _nAme = "Artur"
            """
            assertThat(subject.lint(code)).hasSize(1)
        }

        @Test
        fun `should report private top level property using two underscores`() {
            val code = """
                private val __NAME = "Artur"
            """
            io.gitlab.arturbosch.detekt.test.assertThat(subject.lint(code)).hasSize(1)
        }
    }
}
