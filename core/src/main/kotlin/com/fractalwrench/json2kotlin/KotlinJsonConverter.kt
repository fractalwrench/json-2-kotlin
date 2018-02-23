package com.fractalwrench.json2kotlin

import com.google.gson.*
import java.io.OutputStream

/**
 * Converts JSON to Kotlin
 */
class KotlinJsonConverter {

    private val buildDelegate: SourceBuildDelegate = GsonBuildDelegate()
    private val jsonReader = JsonReader(JsonParser())
    private val sourceFileWriter = SourceFileWriter()
    private val typeHolder = ClassTypeHolder(buildDelegate)
    private val traverser = ReverseJsonTreeTraverser(typeHolder)

    /**
     * Converts a JSON string to Kotlin, writing it to the OutputStream.
     */
    fun convert(input: String, output: OutputStream, args: ConversionArgs) {
        try {
            if (input.isEmpty()) {
                throw IllegalArgumentException("Json input empty")
            }

            val jsonRoot = jsonReader.readJsonTree(input, args)
            traverser.traverse(jsonRoot, args.rootClassName)
            sourceFileWriter.writeSourceFile(typeHolder.stack, args, output)
        } catch (e: JsonSyntaxException) {
            throw IllegalArgumentException("Invalid JSON supplied", e)
        }
    }

}