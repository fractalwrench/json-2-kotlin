package com.fractalwrench.json2kotlin

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.OutputStream
import java.util.*

internal class SourceFileWriter {

    fun writeSourceFile(stack: Stack<TypeSpec>, args: ConversionArgs, output: OutputStream) {
        val sourceFile = FileSpec.builder("", args.rootClassName)

        while (stack.isNotEmpty()) {
            sourceFile.addType(stack.pop())
        }

        val stringBuilder = StringBuilder()
        sourceFile.build().writeTo(stringBuilder)
        output.write(stringBuilder.toString().toByteArray())
    }

}