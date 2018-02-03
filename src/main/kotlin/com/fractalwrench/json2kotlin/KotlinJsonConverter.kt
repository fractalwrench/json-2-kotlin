package com.fractalwrench.json2kotlin

import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.squareup.kotlinpoet.*
import java.io.OutputStream

class KotlinJsonConverter(val jsonParser: JsonParser) : JsonConverter {

    override fun convert(input: String, output: OutputStream, rootClassName: String) {
        try {
            if (input.isEmpty()) {
                throw IllegalArgumentException("Json input empty")
            }
            val jsonElement = jsonParser.parse(input)


            // create kotlin class
            val jsonObject = jsonElement.asJsonObject


            val classBuilder = TypeSpec.classBuilder(rootClassName)
                    .addModifiers(KModifier.DATA)
            val primaryConstructor = FunSpec.constructorBuilder()

            for (key in jsonObject.keySet()) {
                val get = jsonObject.get(key)

                if (get.isJsonPrimitive) {
                    val primitive = get.asJsonPrimitive
                    primaryConstructor.addParameter(key, String::class)
                    val initializer = PropertySpec.builder(key, String::class)
                            .initializer(key)
                    classBuilder.addProperty(initializer.build())
                }
            }

            val sourceFile = FileSpec.builder("", rootClassName)
                    .addType(classBuilder
                            .primaryConstructor(primaryConstructor.build()).build())
                    .build()

            val stringBuilder = StringBuilder()
            sourceFile.writeTo(stringBuilder)
            output.write(stringBuilder.toString().toByteArray())

        } catch (e: JsonSyntaxException) {
            throw IllegalArgumentException("Invalid JSON supplied", e)
        }
    }

}