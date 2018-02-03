package com.fractalwrench.json2kotlin

import com.google.gson.JsonParser
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.ByteArrayOutputStream

@RunWith(Parameterized::class)
class JsonConverterTest(val expectedFilename: String, val jsonFilename: String) {

    private val fileReader = ResourceFileReader()
    private val jsonConverter = KotlinJsonConverter(JsonParser())

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "File {0}")
        fun filenamePairs(): Collection<Array<String>> {
            return listOf(
                    // name-value pair primitives
                    arrayOf("nvpair/BoolExample.kt", "nvpair/boolean.json"),
                    arrayOf("nvpair/DoubleExample.kt", "nvpair/double.json"),
                    arrayOf("nvpair/IntExample.kt", "nvpair/int.json"),
                    arrayOf("nvpair/NullExample.kt", "nvpair/null.json"),
                    arrayOf("nvpair/StrExample.kt", "nvpair/string.json")
            )
        }
    }

    /**
     * Takes a JSON file and converts it into the equivalent Kotlin class, then compares to expected output.
     */
    @Test
    fun testJsonToKotlinConversion() {
        val json = fileReader.readContents(jsonFilename)
        val outputStream = ByteArrayOutputStream()
        jsonConverter.convert(json, outputStream, jsonFilename.replace(".kt", ""))

        val generatedSource = String(outputStream.toByteArray())
        val expectedContents = fileReader.readContents(expectedFilename)

        val msg = "Generated file doesn't match expected file \'$expectedFilename\'"
        Assert.assertEquals(msg, expectedContents, generatedSource)
    }
}