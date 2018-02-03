package com.fractalwrench.json2kotlin

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSyntaxException
import com.squareup.kotlinpoet.*
import java.io.OutputStream

class KotlinJsonConverter(val jsonParser: JsonParser) : JsonConverter {

    override fun convert(input: String, output: OutputStream, rootClassName: String) {
        try {
            if (input.isEmpty()) {
                throw IllegalArgumentException("Json input empty")
            }
            val root = jsonParser.parse(input)


            // create kotlin class

            when {
                root.isJsonObject -> {
                    val obj = root.asJsonObject

                    val classBuilder = TypeSpec.classBuilder(rootClassName)
                    val rootClass = buildClass(classBuilder, obj)

                    val sourceFile = FileSpec.builder("", rootClassName)
                            .addType(rootClass)
                            .build()

                    val stringBuilder = StringBuilder()
                    sourceFile.writeTo(stringBuilder)
                    output.write(stringBuilder.toString().toByteArray())
                }
                root.isJsonArray -> {
                    val ary = root.asJsonArray

                    // TODO wrap in another class, then recurse
                }
                else -> throw IllegalStateException("Expected a JSON array or object")
            }

        } catch (e: JsonSyntaxException) {
            throw IllegalArgumentException("Invalid JSON supplied", e)
        }
    }

    private fun buildClass(classBuilder: TypeSpec.Builder, jsonObject: JsonObject): TypeSpec {
        if (jsonObject.size() <= 0) {
            return classBuilder.build()
        }

        val constructor = FunSpec.constructorBuilder()
        classBuilder.addModifiers(KModifier.DATA)

        for (key in jsonObject.keySet()) {
            val nvp = jsonObject.get(key)

            when {
                nvp.isJsonNull -> {
                    val type = Any::class.asTypeName().asNullable()
                    addDataClassProperty(key, type, constructor, classBuilder)
                }
                nvp.isJsonPrimitive -> {
                    val primitive = nvp.asJsonPrimitive
                    val type = typenameForPrimitive(primitive)
                    addDataClassProperty(key, type, constructor, classBuilder)
                }
                nvp.isJsonArray -> TODO("Handle array")
                nvp.isJsonObject -> TODO("Handle object")
            }
        }
        return classBuilder.primaryConstructor(constructor.build()).build()
    }

    private fun typenameForPrimitive(primitive: JsonPrimitive): ClassName {
        return when {
            primitive.isBoolean -> Boolean::class
            primitive.isNumber -> Number::class
            primitive.isString -> String::class
            else -> throw IllegalStateException("No type found for JSON primitive " + primitive)
        }.asTypeName()
    }

    private fun addDataClassProperty(key: String, type: TypeName,
                                     constructor: FunSpec.Builder, classBuilder: TypeSpec.Builder) {
        constructor.addParameter(key, type)
        val initializer = PropertySpec.builder(key, type)
                .initializer(key)
        classBuilder.addProperty(initializer.build())
    }

}