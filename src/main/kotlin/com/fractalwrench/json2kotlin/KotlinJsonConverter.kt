package com.fractalwrench.json2kotlin

import com.google.gson.*
import java.io.OutputStream

class KotlinJsonConverter {

    private val sourceFileWriter = SourceFileWriter()
    private val typeHolder = ClassTypeHolder()
    private val traverser = ReverseJsonTreeTraverser(typeHolder)
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
            sourceFileWriter.writeSourceFile(typeHolder.stack, args, output)
        } catch (e: JsonSyntaxException) {
            throw IllegalArgumentException("Invalid JSON supplied", e)
        }
    }

}