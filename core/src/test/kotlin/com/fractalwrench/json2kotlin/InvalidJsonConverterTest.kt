package com.fractalwrench.json2kotlin

import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.ByteArrayOutputStream

@RunWith(Parameterized::class)
class InvalidJsonConverterTest(val jsonFilename: String) {

    private val fileReader = ResourceFileReader()
    private val jsonConverter = Kotlin2JsonConverter()

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "File {0}")
        fun filenamePairs(): Collection<String> {
            return listOf(
                    "invalid/empty.json",
                    "invalid/invalid.json"
            )
        }
    }

    /**
     * Takes a JSON file and converts it into the equivalent Kotlin class, then compares to expected output.
     */
    @Test(expected = RuntimeException::class)
    fun testJsonToKotlinConversion() {
        val json = fileReader.inputStream(jsonFilename)
        val outputStream = ByteArrayOutputStream()
        val args = ConversionArgs(jsonFilename.replace(".kt", ""))
        jsonConverter.convert(json, outputStream, args)
    }
}