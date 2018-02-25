package com.fractalwrench.json2kotlin

import org.junit.Assert
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.File

class ConverterArgsTest {

    private val fileReader = ResourceFileReader()
    private val jsonConverter = Kotlin2JsonConverter()

    /**
     * Takes a JSON file and converts it into the equivalent Kotlin class, then compares to expected output.
     */
    @Test
    fun testPackageName() {
        val jsonFilename = "args/Package.json"
        val expectedFilename = "args/PackageExample.kt"
        val json = fileReader.readContents(jsonFilename)

        val outputStream = ByteArrayOutputStream()
        jsonConverter.convert(json, outputStream, ConversionArgs("PackageExample", "com.fractalwrench.foo"))

        val generatedSource = String(outputStream.toByteArray()).standardiseNewline()
        val expectedContents = fileReader.readContents(expectedFilename).standardiseNewline()
        val msg = "Generated file doesn't match expected file \'$expectedFilename\'"
        Assert.assertEquals(msg, expectedContents, generatedSource)
    }

}