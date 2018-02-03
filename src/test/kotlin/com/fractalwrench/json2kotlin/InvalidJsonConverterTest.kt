package com.fractalwrench.json2kotlin

import com.google.gson.JsonParser
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.ByteArrayOutputStream

@RunWith(Parameterized::class)
class InvalidJsonConverterTest(val jsonFilename: String) {

    private val fileReader = ResourceFileReader()
    private val jsonConverter = KotlinJsonConverter(JsonParser())

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "File {0}")
        fun filenamePairs(): Collection<String> {
            return listOf(
                    "invalid/empty.json",
                    "invalid/invalid.json",
                    "invalid/dupe.json",

                    // this is valid JSON, but only contains primitives so doesn't need class generation
                    "invalid/primitive_array.json"
            )
        }
    }

    /**
     * Takes a JSON file and converts it into the equivalent Kotlin class, then compares to expected output.
     */
    @Test(expected = IllegalArgumentException::class)
    fun testJsonToKotlinConversion() {
        val json = fileReader.readContents(jsonFilename)
        val outputStream = ByteArrayOutputStream()
        jsonConverter.convert(json, outputStream, jsonFilename.replace(".kt", ""))
    }
}