package com.fractalwrench.json2kotlin

import com.google.gson.JsonParser
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.ByteArrayOutputStream

fun String.standardiseNewline(): String {
    return this.replace("\r\n", "\n")
}

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
                    arrayOf("BoolExample.kt", "boolean.json"),
                    arrayOf("NumberExample.kt", "number.json"),
                    arrayOf("NullExample.kt", "null.json"),
                    arrayOf("StrExample.kt", "string.json")
            )
        }
    }

    /**
     * Takes a JSON file and converts it into the equivalent Kotlin class, then compares to expected output.
     */
    @Test
    fun testJsonToKotlinConversion() {
        val json = fileReader.readContents("valid/$jsonFilename")
        val outputStream = ByteArrayOutputStream()
        jsonConverter.convert(json, outputStream, expectedFilename.replace(".kt", ""))

        val generatedSource = String(outputStream.toByteArray()).standardiseNewline()
        val expectedContents = fileReader.readContents("valid/$expectedFilename").standardiseNewline()

        val msg = "Generated file doesn't match expected file \'$expectedFilename\'"

        Assert.assertEquals(msg, expectedContents, generatedSource)
    }
}