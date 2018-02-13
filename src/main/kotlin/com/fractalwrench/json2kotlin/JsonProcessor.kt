package com.fractalwrench.json2kotlin

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.squareup.kotlinpoet.*
import java.util.HashMap
import java.util.HashSet


// TODO tidy/split

internal class JsonProcessor {

    internal val jsonElementMap = HashMap<JsonElement, TypeSpec>() // FIXME encapsulate me!

    fun processFieldType(fields: Collection<String>, commonElements: List<TypedJsonElement>): Map<String, TypeName> {
        val fieldMap = HashMap<String, TypeName>()

        for (field in fields) {
            val distinctTypes = commonElements.map {
                val value = it.asJsonObject.get(field)
                if (value != null) processJsonField(value, field) else null
            }.distinct()

            val typeName = reduceToSingleType(distinctTypes)
            fieldMap.put(field, typeName)
        }
        return fieldMap
    }

    private fun processJsonObject(jsonObject: JsonObject, key: String): TypeName {
        val get = jsonElementMap[jsonObject]
        if (get != null) {
            return ClassName.bestGuess(get.name!!)
        }

        val identifier = key.toKotlinIdentifier().capitalize()
        val classBuilder = TypeSpec.classBuilder(identifier)

        if (jsonObject.size() > 0) {
            val constructor = FunSpec.constructorBuilder()
            classBuilder.addModifiers(KModifier.DATA) // non-empty classes allow data modifier
            processJsonObjectFields(jsonObject, constructor, classBuilder)
            classBuilder.primaryConstructor(constructor.build())
        }
        return ClassName.bestGuess(identifier)
    }

    private fun processJsonObjectFields(jsonObject: JsonObject,
                                        constructor: FunSpec.Builder,
                                        classBuilder: TypeSpec.Builder) {
        jsonObject.entrySet().forEach {
            val fieldType = processJsonField(it.value, it.key)
            val identifier = it.key.toKotlinIdentifier()

            val initializer = PropertySpec.builder(identifier, fieldType).initializer(identifier)
            classBuilder.addProperty(initializer.build())
            constructor.addParameter(identifier, fieldType)
        }
    }

    private fun processJsonField(jsonElement: JsonElement, key: String): TypeName {
        with(jsonElement) {
            return when {
                isJsonPrimitive -> processJsonPrimitive(asJsonPrimitive)
                isJsonArray -> processJsonArray(asJsonArray, key)
                isJsonObject -> processJsonObject(asJsonObject, key)
                isJsonNull -> Any::class.asTypeName().asNullable()
                else -> throw IllegalStateException("Expected a JSON value")
            }
        }
    }

    private fun processJsonArray(jsonArray: JsonArray, key: String): TypeName {
        val arrayTypes = HashSet<TypeName>()
        var nullable = false

        jsonArray.withIndex().forEach {
            val sanitisedName = key.toKotlinIdentifier()
            with(it.value) {
                when {
                    isJsonPrimitive -> arrayTypes.add(processJsonField(asJsonPrimitive, sanitisedName))
                    isJsonArray -> arrayTypes.add(processJsonArray(asJsonArray, nameForArrayField(sanitisedName)))
                    isJsonObject -> arrayTypes.add(processJsonObject(asJsonObject, nameForObjectInArray(it, sanitisedName)))
                    isJsonNull -> nullable = true
                    else -> throw IllegalStateException("Unexpected state in array")
                }
            }
        }
        val arrayType = deduceArrayType(arrayTypes, nullable)
        return ParameterizedTypeName.get(Array<Any>::class.asClassName(), arrayType)
    }

    private fun processJsonPrimitive(primitive: JsonPrimitive): TypeName {
        return when {
            primitive.isBoolean -> Boolean::class
            primitive.isNumber -> Number::class
            primitive.isString -> String::class
            else -> throw IllegalStateException("No type found for JSON primitive " + primitive)
        }.asTypeName()
    }

    private fun deduceArrayType(arrayTypes: HashSet<TypeName>, nullable: Boolean): TypeName {
        val hasMultipleType = arrayTypes.size > 1 || arrayTypes.isEmpty()
        val arrayTypeName = when {
            hasMultipleType -> Any::class.asTypeName()
            else -> arrayTypes.asIterable().first()
        }
        return when {
            nullable -> arrayTypeName.asNullable()
            else -> arrayTypeName
        }
    }

    /**
     * Determines a single type which fits multiple types.
     */
    private fun reduceToSingleType(types: List<TypeName?>): TypeName {
        val anyClz = Any::class.asTypeName()
        val nullableClassName = anyClz.asNullable()
        val nullable = types.contains(nullableClassName) || types.contains(null)
        val nonNullTypes = types.filterNotNull().filter { it != nullableClassName }

        val typeName: TypeName = when {
            nonNullTypes.size == 1 -> nonNullTypes[0]
            nonNullTypes.size == 2 -> {

                val parameterizedTypeName = nonNullTypes.filterIsInstance(ParameterizedTypeName::class.java).firstOrNull()

                if (parameterizedTypeName != null) { // handle type params (recursive)
                    val rawType = parameterizedTypeName.rawType
                    parameterizedTypeName.typeArguments
                    return ParameterizedTypeName.get(rawType, reduceToSingleType(parameterizedTypeName.typeArguments))
                } else {
                    if (nonNullTypes.contains(anyClz)) { // Any will be an empty/missing object
                        nonNullTypes.filterNot { it == anyClz }.first()
                    } else {
                        anyClz
                    }
                }
            }
            else -> anyClz
        }
        return if (nullable) typeName.asNullable() else typeName
    }

}