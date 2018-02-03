package com.fractalwrench.json2kotlin

import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import java.io.OutputStream

class KotlinJsonConverter(val jsonParser: JsonParser) : JsonConverter {

    override fun convert(input: String, output: OutputStream, rootClassName: String) {
        try {
            if (input.isEmpty()) {
                throw IllegalArgumentException("Json input empty")
            }
            val jsonElement = jsonParser.parse(input)


        } catch (e: JsonSyntaxException) {
            throw IllegalArgumentException("Invalid JSON supplied", e)
        }
    }

}