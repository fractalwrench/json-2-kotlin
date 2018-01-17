package com.fractalwrench.json2kotlin

import java.io.InputStream
import java.io.OutputStream

interface JsonConverter {
    fun convert(input: InputStream): OutputStream
}