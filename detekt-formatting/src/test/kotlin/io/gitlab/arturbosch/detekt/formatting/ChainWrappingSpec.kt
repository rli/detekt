package io.gitlab.arturbosch.detekt.formatting

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.formatting.wrappers.ChainWrapping
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class ChainWrappingSpec {

    @Nested
    inner class `tests integration of formatting` {

        @Test
        fun `should work like KtLint`() {
            val subject = loadFile("configTests/chain-wrapping-before.kt")
            val expected = loadFileContent("configTests/chain-wrapping-after.kt")

            val findings = ChainWrapping(Config.empty).lint(subject.text)

            assertThat(findings).isNotEmpty
            assertThat(subject.text).isEqualTo(expected)
        }
    }
}
