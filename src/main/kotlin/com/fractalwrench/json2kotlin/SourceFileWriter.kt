package com.fractalwrench.json2kotlin

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.OutputStream
import java.util.*

/**
 * Writes a collection of types to a source file OutputStream.
 */
internal class SourceFileWriter {

    /**
     * Writes a collection of types to a source file OutputStream.
     */
    fun writeSourceFile(stack: Stack<TypeSpec>, args: ConversionArgs, output: OutputStream) {
        val sourceFile = FileSpec.builder("", args.rootClassName)

        while (stack.isNotEmpty()) {
            sourceFile.addType(stack.pop())
        }

        with(StringBuilder()) {
            sourceFile.build().writeTo(this)
            output.write(toString().toByteArray())
        }
    }

}