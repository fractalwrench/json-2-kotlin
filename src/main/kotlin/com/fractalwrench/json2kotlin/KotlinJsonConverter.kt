package com.fractalwrench.json2kotlin

import com.google.gson.*
import com.squareup.kotlinpoet.*
import java.io.OutputStream
import kotlin.reflect.KClass

class KotlinJsonConverter(val jsonParser: JsonParser) : JsonConverter {

    override fun convert(input: String, output: OutputStream, rootClassName: String) {
        try {
            if (input.isEmpty()) {
                throw IllegalArgumentException("Json input empty")
            }
            val root = jsonParser.parse(input)
            val sourceFile = FileSpec.builder("", rootClassName)

            // create kotlin class

            when {
                root.isJsonObject -> { // TODO build up a Set of all the objects as a type representation
                    val obj = root.asJsonObject

                    val classBuilder = TypeSpec.classBuilder(rootClassName)
                    val rootClass = buildClass(classBuilder, obj)
                    sourceFile.addType(rootClass)
                }
                root.isJsonArray -> {
                    val ary = root.asJsonArray

                    TODO("wrap in another class, then recurse")
                }
                else -> throw IllegalStateException("Expected a JSON array or object")
            }

            val stringBuilder = StringBuilder()
            sourceFile.build().writeTo(stringBuilder)
            output.write(stringBuilder.toString().toByteArray())

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
                nvp.isJsonArray -> {
                    val array = nvp.asJsonArray
                    val paramType = Any::class

                    // TODO determine type
                    val arrayType = findArrayType(array)
                    addDataClassProperty(key, arrayType, constructor, classBuilder)
                    // TODO("Handle array")
                }
                nvp.isJsonObject -> TODO("Handle object")
            }
        }
        return classBuilder.primaryConstructor(constructor.build()).build()
    }

    private fun findArrayType(array: JsonArray): TypeName {
        val arrayTypes = HashSet<KClass<*>>()
        var nullable = false

        for (jsonElement in array) {
            when {
                jsonElement.isJsonPrimitive -> {
                    val primitive = jsonElement.asJsonPrimitive
                    when {
                        primitive.isBoolean -> arrayTypes.add(Boolean::class)
                        primitive.isNumber -> arrayTypes.add(Number::class)
                        primitive.isString -> arrayTypes.add(String::class)
                        else -> throw IllegalStateException("Unexpected state in array")
                    }
                }
                jsonElement.isJsonArray -> arrayTypes.add(Array<Any>::class) // FIXME handle this better!
                jsonElement.isJsonObject -> arrayTypes.add(Any::class) // FIXME handle this better!
                jsonElement.isJsonNull -> nullable = true
                else -> throw IllegalStateException("Unexpected state in array")
            }
        }

        var rawType = if (arrayTypes.size > 1 || arrayTypes.isEmpty()) {
            Any::class
        } else {
            arrayTypes.asIterable().first()
        }.asTypeName()

        if (nullable) {
            rawType = rawType.asNullable()
        }
        return ParameterizedTypeName.get(Array<Any>::class.asClassName(), rawType)
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