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
            val jsonRoot = jsonParser.parse(input)
            val sourceFile = FileSpec.builder("", rootClassName)

            // TODO build up a Set of all the objects as a type representation

            when {
                jsonRoot.isJsonObject -> handleRootJsonObject(jsonRoot.asJsonObject, rootClassName, sourceFile)
                jsonRoot.isJsonArray -> handleRootJsonArray(jsonRoot.asJsonArray, rootClassName, sourceFile)
                else -> throw IllegalStateException("Expected a JSON array or object")
            }

            val stringBuilder = StringBuilder()
            sourceFile.build().writeTo(stringBuilder)
            output.write(stringBuilder.toString().toByteArray())

        } catch (e: JsonSyntaxException) {
            throw IllegalArgumentException("Invalid JSON supplied", e)
        }
    }

    private fun handleRootJsonObject(jsonObject: JsonObject, className: String, sourceFile: FileSpec.Builder) {
        val classBuilder = TypeSpec.classBuilder(className)
        val classRepresentation = buildClass(classBuilder, jsonObject)
        sourceFile.addType(classRepresentation)
    }

    private fun handleRootJsonArray(jsonArray: JsonArray, className: String, sourceFile: FileSpec.Builder) {
        val fieldName = "${className}Field".decapitalize() // FIXME not a common case
        val containerClassName = "${className}Container"
        val jsonElement = JsonObject()
        jsonElement.add(fieldName, jsonArray)
        handleRootJsonObject(jsonElement, containerClassName, sourceFile)
    }

    private fun buildClass(classBuilder: TypeSpec.Builder, jsonObject: JsonObject): TypeSpec {
        if (jsonObject.size() <= 0) {
            return classBuilder.build() // empty class def
        }

        val constructor = FunSpec.constructorBuilder()
        classBuilder.addModifiers(KModifier.DATA) // non-empty classes allow data modifier

        // find the type for each value then add the field to the class
        for (key in jsonObject.keySet()) {
            val nvp = jsonObject.get(key)
            val valueType = findValueType(nvp)
            addDataClassProperty(key, valueType, constructor, classBuilder)
        }
        return classBuilder.primaryConstructor(constructor.build()).build()
    }

    private fun findValueType(nvp: JsonElement): TypeName {
        return when {
            nvp.isJsonNull -> Any::class.asTypeName().asNullable()
            nvp.isJsonPrimitive -> findJsonPrimitiveType(nvp.asJsonPrimitive)
            nvp.isJsonArray -> findJsonArrayType(nvp.asJsonArray)
            nvp.isJsonObject -> findJsonObjectType(nvp.asJsonObject)
            else -> throw IllegalStateException("Expected a JSON value")
        }
    }

    private fun findJsonPrimitiveType(primitive: JsonPrimitive): ClassName {
        return when {
            primitive.isBoolean -> Boolean::class
            primitive.isNumber -> Number::class
            primitive.isString -> String::class
            else -> throw IllegalStateException("No type found for JSON primitive " + primitive)
        }.asTypeName()
    }

    private fun findJsonArrayType(jsonArray: JsonArray): TypeName { // TODO handle objects and arrays
        val arrayTypes = HashSet<KClass<*>>()
        var nullable = false

        for (jsonElement in jsonArray) {

            // TODO replace with findValueType (this would require returning KClass)


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

        val rawType = deduceArrayType(arrayTypes, nullable)
        return ParameterizedTypeName.get(Array<Any>::class.asClassName(), rawType)
    }

    private fun deduceArrayType(arrayTypes: HashSet<KClass<*>>, nullable: Boolean): ClassName {
        var rawType = if (arrayTypes.size > 1 || arrayTypes.isEmpty()) {
            Any::class
        } else {
            arrayTypes.asIterable().first()
        }.asTypeName()

        if (nullable) {
            rawType = rawType.asNullable()
        }
        return rawType
    }

    private fun findJsonObjectType(jsonObject: JsonObject): TypeName {
        TODO("Handle object")
    }

    private fun addDataClassProperty(key: String, type: TypeName,
                                     constructor: FunSpec.Builder, classBuilder: TypeSpec.Builder) {
        val initializer = PropertySpec.builder(key, type)
                .initializer(key)
        classBuilder.addProperty(initializer.build()) // ensures val present by adding both
        constructor.addParameter(key, type)
    }

}