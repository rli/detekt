package io.gitlab.arturbosch.detekt.core.settings

import io.gitlab.arturbosch.detekt.core.createNullLoggingSpec
import io.gitlab.arturbosch.detekt.core.createProcessingSettings
import org.assertj.core.api.Assertions.assertThat
import org.jetbrains.kotlin.konan.file.File
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class EnvironmentFacadeSpec {

    @Nested
    inner class `classpath entries should be separated by platform-specific separator` {

        val classpath = when (File.pathSeparator) {
            ":" -> "/path/to/file1:/path/to/file2:/path/to/file3"
            ";" -> """C:\path\to\file1;C:\path\to\file2;C:\path\to\file3"""
            else -> ""
        }

        @Test
        fun `supports ${File_pathSeparator}`() {
            testSettings(classpath).use {
                assertThat(it.classpath).hasSize(3)
            }
        }
    }
}

private fun testSettings(classpath: String) = createProcessingSettings(
    spec = createNullLoggingSpec {
        compiler {
            this.classpath = classpath
        }
    }
)
