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
                    // primitives
                    arrayOf("primitives/BoolObjExample.kt", "primitives/boolean_obj.json"),
                    arrayOf("primitives/NumberObjExample.kt", "primitives/number_obj.json"),
                    arrayOf("primitives/NullObjExample.kt", "primitives/null_obj.json"),
                    arrayOf("primitives/StrObjExample.kt", "primitives/string_obj.json"),
                    arrayOf("primitives/MultiPrimitiveObjExample.kt", "primitives/multi_primitive_obj.json"),
                    arrayOf("primitives/EmptyObjExample.kt", "primitives/empty_obj.json"),

                    // arrays
                    arrayOf("primitives/array/BoolAryExample.kt", "primitives/array/boolean_ary.json"),
                    arrayOf("primitives/array/NullAryExample.kt", "primitives/array/null_ary.json"),
                    arrayOf("primitives/array/NumberAryExample.kt", "primitives/array/number_ary.json"),
                    arrayOf("primitives/array/StrAryExample.kt", "primitives/array/string_ary.json")
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
        val rootClassName = expectedFilename.replace(".kt", "").substringAfterLast('/')
        jsonConverter.convert(json, outputStream, rootClassName)

        val generatedSource = String(outputStream.toByteArray()).standardiseNewline()
        val expectedContents = fileReader.readContents(expectedFilename).standardiseNewline()

        val msg = "Generated file doesn't match expected file \'$expectedFilename\'"

        Assert.assertEquals(msg, expectedContents, generatedSource)
    }
}