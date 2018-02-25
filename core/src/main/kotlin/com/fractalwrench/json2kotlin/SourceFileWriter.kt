package com.fractalwrench.json2kotlin

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.BufferedWriter
import java.io.OutputStream
import java.io.OutputStreamWriter
import java.util.*

/**
 * Writes a collection of types to a source file OutputStream.
 */
internal class SourceFileWriter {

    /**
     * Writes a collection of types to a source file OutputStream.
     */
    fun writeSourceFile(stack: Stack<TypeSpec>, args: ConversionArgs, output: OutputStream) {
        val packageName = args.packageName ?: ""
        val sourceFile = FileSpec.builder(packageName, args.rootClassName)

        while (stack.isNotEmpty()) {
            sourceFile.addType(stack.pop())
        }

        val bufferedWriter = BufferedWriter(OutputStreamWriter(output))
        sourceFile.build().writeTo(bufferedWriter)
        bufferedWriter.flush()
    }

}