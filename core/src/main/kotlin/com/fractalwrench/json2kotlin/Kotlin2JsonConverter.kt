package com.fractalwrench.json2kotlin

import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import java.io.InputStream
import java.io.OutputStream

/**
 * Converts JSON to Kotlin
 */
class Kotlin2JsonConverter(private val buildDelegate: SourceBuildDelegate = GsonBuildDelegate()) {

    private val jsonReader = JsonReader(JsonParser())
    private val sourceFileWriter = SourceFileWriter()
    private val treeTraverser = ReverseJsonTreeTraverser()

    /**
     * Converts an InputStream of JSON to Kotlin source code, writing the result to the OutputStream.
     */
    fun convert(input: InputStream, output: OutputStream, args: ConversionArgs) {
        try {
            val jsonRoot = jsonReader.readJsonTree(input, args)
            val jsonStack = treeTraverser.traverse(jsonRoot, args.rootClassName)
            val generator = TypeSpecGenerator(buildDelegate, ::defaultGroupingStrategy)
            val typeSpecs = generator.generateTypeSpecs(jsonStack)
            sourceFileWriter.writeSourceFile(typeSpecs, args, output)
        } catch (e: JsonSyntaxException) {
            throw IllegalArgumentException("Invalid JSON supplied", e)
        }
    }

}