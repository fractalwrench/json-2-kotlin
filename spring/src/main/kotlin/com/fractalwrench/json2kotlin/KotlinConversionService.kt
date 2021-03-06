package com.fractalwrench.json2kotlin

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@Service
class KotlinConversionService {

    companion object {
        val maxPayloadSize = 10000
    }

    fun convert(json: String): Pair<ByteArrayOutputStream, String> {
        val os = ByteArrayOutputStream()

        if (json.length > maxPayloadSize) {
            throw IllegalArgumentException("JSON input cannot be larger than $maxPayloadSize")
        }

        val inputStream = ByteArrayInputStream(json.toByteArray())
        val conversionInfo = Kotlin2JsonConverter().convert(inputStream, os, ConversionArgs())
        return Pair(os, conversionInfo.filename)
    }
}