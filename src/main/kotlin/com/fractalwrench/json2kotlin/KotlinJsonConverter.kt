package com.fractalwrench.json2kotlin

import com.google.gson.Gson
import com.google.gson.JsonParser
import java.io.OutputStream

class KotlinJsonConverter(val gson: Gson) : JsonConverter {

    override fun convert(input: String, output: OutputStream, rootClassName: String) {
        val jsonElement = JsonParser().parse(input)
    }

}