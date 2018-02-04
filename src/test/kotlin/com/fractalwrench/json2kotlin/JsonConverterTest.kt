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
            return listOf( // TODO simplify detecting params
                    // primitives
                    arrayOf("primitives/obj/BoolExample.kt", "primitives/obj/boolean.json"),
                    arrayOf("primitives/obj/NumberExample.kt", "primitives/obj/number.json"),
                    arrayOf("primitives/obj/NullExample.kt", "primitives/obj/null.json"),
                    arrayOf("primitives/obj/StrExample.kt", "primitives/obj/string.json"),
                    arrayOf("primitives/obj/MultiPrimitiveExample.kt", "primitives/obj/multi_primitive.json"),
                    arrayOf("primitives/obj/EmptyExample.kt", "primitives/obj/empty.json"),
                    arrayOf("primitives/obj/AryExample.kt", "primitives/obj/ary.json"),
                    arrayOf("primitives/obj/ObjExample.kt", "primitives/obj/obj.json"),

                    // arrays
                    arrayOf("primitives/array/enclosed/BoolExample.kt", "primitives/array/enclosed/boolean.json"),
                    arrayOf("primitives/array/enclosed/NullExample.kt", "primitives/array/enclosed/null.json"),
                    arrayOf("primitives/array/enclosed/NumberExample.kt", "primitives/array/enclosed/number.json"),
                    arrayOf("primitives/array/enclosed/StrExample.kt", "primitives/array/enclosed/string.json"),
                    arrayOf("primitives/array/enclosed/EmptyExample.kt", "primitives/array/enclosed/empty.json"),
                    arrayOf("primitives/array/dangle/RootExample.kt", "primitives/array/dangle/root.json"),
                    arrayOf("primitives/array/enclosed/NullableStrExample.kt", "primitives/array/enclosed/nullable_string.json"),
                    arrayOf("primitives/array/enclosed/AnyExample.kt", "primitives/array/enclosed/any.json"),
                    arrayOf("primitives/array/enclosed/ObjExample.kt", "primitives/array/enclosed/obj.json"),

                    // dangling arrays
                    arrayOf("primitives/array/dangle/BoolDangleExample.kt", "primitives/array/dangle/boolean_dangle.json"),
                    arrayOf("primitives/array/dangle/NullDangleExample.kt", "primitives/array/dangle/null_dangle.json"),
                    arrayOf("primitives/array/dangle/NumberDangleExample.kt", "primitives/array/dangle/number_dangle.json"),
                    arrayOf("primitives/array/dangle/StrDangleExample.kt", "primitives/array/dangle/string_dangle.json"),
                    arrayOf("primitives/array/dangle/EmptyDangleExample.kt", "primitives/array/dangle/empty_dangle.json"),
                    arrayOf("primitives/array/dangle/NullableStrDangleExample.kt", "primitives/array/dangle/nullable_string_dangle.json"),
                    arrayOf("primitives/array/dangle/AnyDangleExample.kt", "primitives/array/dangle/any_dangle.json")

                    // TODO uncomment
//                    // commonality
//                    arrayOf("commonality/obj/SameStrExample.kt", "commonality/obj/same_str.json"),
//                    arrayOf("commonality/obj/NullableStrExample.kt", "commonality/obj/nullable_str.json"),
//                    arrayOf("commonality/obj/NullableAnyExample.kt", "commonality/obj/nullable_any.json"),
//                    arrayOf("commonality/obj/DiffSingleFieldExample.kt", "commonality/obj/diff_single_field.json"),
//                    arrayOf("commonality/obj/AnyExample.kt", "commonality/obj/any.json"),
//
//                    // commonality
//                    arrayOf("commonality/array/SameStrExample.kt", "commonality/array/same_str.json"),
//                    arrayOf("commonality/array/NullableStrExample.kt", "commonality/array/nullable_str.json"),
//                    arrayOf("commonality/array/NullableAnyExample.kt", "commonality/array/nullable_any.json"),
//                    arrayOf("commonality/array/DiffSingleFieldExample.kt", "commonality/array/diff_single_field.json"),
//                    arrayOf("commonality/array/AnyExample.kt", "commonality/array/any.json")
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