package io.gitlab.arturbosch.detekt.rules.style

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.rules.KotlinCoreEnvironmentTest
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.compileAndLintWithContext
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

@KotlinCoreEnvironmentTest
class UseEmptyCounterpartSpec(val env: KotlinCoreEnvironment) {
    val rule = UseEmptyCounterpart(Config.empty)

    @Nested
    inner class `UseEmptyCounterpart rule` {

        @Test
        fun `reports no-arg instantiation`() {
            val code = """
                val array = arrayOf<Any>()
                val list = listOf<Any>()
                val nonNullList = listOfNotNull<Any>()
                val map = mapOf<Any, Any>()
                val sequence = sequenceOf<Any>()
                val set = setOf<Any>()
            """
            assertThat(rule.compileAndLintWithContext(env, code)).hasSize(6)
        }

        @Test
        fun `reports no-arg instantiation with inferred type parameters`() {
            val code = """
                val array: Array<Any> = arrayOf()
                val list: List<Any> = listOf()
                val list: List<Any> = listOfNotNull()
                val map: Map<Any, Any> = mapOf()
                val sequence: Sequence<Any> = sequenceOf()
                val set: Set<Any> = setOf()
            """
            assertThat(rule.compileAndLintWithContext(env, code)).hasSize(6)
        }

        @Test
        fun `does not report empty instantiation`() {
            val code = """
                val array = emptyArray<Any>()
                val list = emptyList<Any>()
                val map = emptyMap<Any, Any>()
                val sequence = emptySequence<Any>()
                val set = emptySet<Any>()
            """
            assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report instantiation with arguments`() {
            val code = """
                val array = arrayOf(0)
                val list = listOf(0)
                val nonNullList = listOfNotNull(0)
                val map = mapOf(0 to 0)
                val sequence = sequenceOf(0)
                val set = setOf(0)
            """
            assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
        }

        @Test
        fun `does not report no-arg custom function with same name as function with empty counterpart`() {
            val code = """
                fun <T> arrayOf(): Array<T> = TODO()
                fun <T> listOf(): List<T> = TODO()
                fun <T> listOfNotNull(): List<T> = TODO()
                fun <K, V> mapOf(): Map<K, V> = TODO()
                fun <T> sequenceOf(): Sequence<T> = TODO()
                fun <T> setOf(): Set<T> = TODO() 

                val array = arrayOf<Any>()
                val list = listOf<Any>()
                val nonNullList = listOfNotNull<Any>()
                val map = mapOf<Any, Any>()
                val sequence = sequenceOf<Any>()
                val set = setOf<Any>()
            """
            assertThat(rule.compileAndLintWithContext(env, code)).isEmpty()
        }
    }
}
