package com.fractalwrench.json2kotlin

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import java.io.OutputStream
import java.util.*

/**
 * Writes a collection of types to a source file OutputStream.
 */
internal class SourceFileWriter { // TODO rename once functionality has changed

    /**
     * Writes a collection of types to a source file OutputStream.
     */
    fun writeSourceFile(stack: Stack<TypeSpec>, args: ConversionArgs, output: OutputStream) {
        val packageName = args.packageName ?: ""
        val sourceFile = FileSpec.builder(packageName, args.rootClassName)

        while (stack.isNotEmpty()) {
            sourceFile.addType(stack.pop())
        }

        with(StringBuilder()) { // FIXME inefficient use of resources
            sourceFile.build().writeTo(this)
            output.write(toString().toByteArray())
        }
    }

}