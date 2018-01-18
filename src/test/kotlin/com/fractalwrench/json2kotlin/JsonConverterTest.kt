package com.fractalwrench.json2kotlin

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.ByteArrayOutputStream

@RunWith(Parameterized::class)
class JsonConverterTest(val expectedFilename: String, val jsonFilename: String) {

    private val fileReader = ResourceFileReader()
    private val jsonConverter = DataClassJsonConverter()

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun filenamePairs(): Collection<Array<String>> {
            return listOf(
                    arrayOf("HelloWorld.kt", "hello_world.json")
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
        jsonConverter.convert(json, outputStream)

        val generatedSource = String(outputStream.toByteArray())
        val expectedContents = fileReader.readContents(expectedFilename)

        val msg = "Generated file doesn't match expected file \'$expectedFilename\'"
        Assert.assertEquals(msg, expectedContents, generatedSource)
    }
}