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
                    arrayOf("primitives/obj/BoolExample.kt", "primitives/obj/boolean.json"),
                    arrayOf("primitives/obj/NumberExample.kt", "primitives/obj/number.json"),
                    arrayOf("primitives/obj/NullExample.kt", "primitives/obj/null.json"),
                    arrayOf("primitives/obj/StrExample.kt", "primitives/obj/string.json"),
                    arrayOf("primitives/obj/MultiPrimitiveExample.kt", "primitives/obj/multi_primitive.json"),
                    arrayOf("primitives/obj/EmptyExample.kt", "primitives/obj/empty.json"),

                    // arrays
                    arrayOf("primitives/array/BoolExample.kt", "primitives/array/boolean.json"),
                    arrayOf("primitives/array/NullExample.kt", "primitives/array/null.json"),
                    arrayOf("primitives/array/NumberExample.kt", "primitives/array/number.json"),
                    arrayOf("primitives/array/StrExample.kt", "primitives/array/string.json"),
                    arrayOf("primitives/array/EmptyExample.kt", "primitives/array/empty.json"),
                    arrayOf("primitives/array/RootExample.kt", "primitives/array/root.json"),
                    arrayOf("primitives/array/NullableStrExample.kt", "primitives/array/nullable_string.json"),
                    arrayOf("primitives/array/AnyExample.kt", "primitives/array/any.json")
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