package com.fractalwrench.json2kotlin

import java.io.OutputStream

interface JsonConverter {

    @Throws(IllegalArgumentException::class, IllegalStateException::class)
    fun convert(input: String, output: OutputStream, rootClassName: String)
}