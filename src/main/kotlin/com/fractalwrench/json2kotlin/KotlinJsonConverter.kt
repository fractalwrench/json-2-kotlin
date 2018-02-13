package com.fractalwrench.json2kotlin

import com.google.gson.*
import java.io.OutputStream

class KotlinJsonConverter {

    private val sourceFileWriter = SourceFileWriter()
    private val stackBuilder = ClassTypeStackBuilder()
    private val traverser = ReverseJsonTreeTraverser(stackBuilder)
    private val jsonReader = JsonReader(JsonParser())

    private lateinit var args: ConversionArgs

    fun convert(input: String, output: OutputStream, args: ConversionArgs) {
        this.args = args
        try {
            if (input.isEmpty()) {
                throw IllegalArgumentException("Json input empty")
            }

            val jsonRoot = jsonReader.readJsonTree(input, args)
            traverser.traverse(jsonRoot, args.rootClassName)
            sourceFileWriter.writeSourceFile(stackBuilder.stack, args, output)
        } catch (e: JsonSyntaxException) {
            throw IllegalArgumentException("Invalid JSON supplied", e)
        }
    }



}