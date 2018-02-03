package com.fractalwrench.json2kotlin

import java.io.OutputStream

interface JsonConverter {
    fun convert(input: String, output: OutputStream, rootClassName: String)
}