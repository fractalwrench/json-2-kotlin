package com.fractalwrench.json2kotlin

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.squareup.kotlinpoet.*
import java.util.HashMap
import java.util.HashSet


internal class JsonProcessor { // TODO crappy name

    internal val jsonElementMap = HashMap<JsonElement, TypeSpec>() // FIXME feels wrong having this exposed

    // FIXME should take TypedJsonElement rather than String as a param!
    fun findDistinctTypesForFields(fields: Collection<String>,
                                   commonElements: List<TypedJsonElement>): Map<String, TypeName> {
        val fieldMap = HashMap<String, TypeName>()

        fields.forEach {
            val distinctTypes = findDistinctTypesForField(commonElements, it)
            fieldMap.put(it, reduceToSingleType(distinctTypes))
        }
        return fieldMap
    }

    private fun findDistinctTypesForField(commonElements: List<TypedJsonElement>, key: String): List<TypeName?> {
        return commonElements.map {
            val fieldValue = it.asJsonObject.get(key)
            if (fieldValue != null) typeForJsonField(fieldValue, key) else null
        }.distinct()
    }



    // TODO: refactor all the (simple) type deduction methods. They can take an extra Map parameter. This will greatly simplify any unit
    // testing and pass Single-Responsibility test


    private fun typeForJsonField(jsonElement: JsonElement, key: String): TypeName {
        with(jsonElement) {
            return when {
                isJsonPrimitive -> typeForJsonPrimitive(asJsonPrimitive)
                isJsonArray -> typeForJsonArray(asJsonArray, key)
                isJsonObject -> typeForJsonObject(asJsonObject, key)
                isJsonNull -> Any::class.asTypeName().asNullable()
                else -> throw IllegalStateException("Expected a JSON value")
            }
        }
    }

    private fun typeForJsonPrimitive(primitive: JsonPrimitive): TypeName {
        return when {
            primitive.isBoolean -> Boolean::class
            primitive.isNumber -> Number::class
            primitive.isString -> String::class
            else -> throw IllegalStateException("No type found for JSON primitive " + primitive)
        }.asTypeName()
    }


    // FIXME feels really messy from here on out


    private fun typeForJsonObject(jsonObject: JsonObject, key: String): TypeName {
        val existingTypeName = jsonElementMap[jsonObject]
        if (existingTypeName != null) {
            return ClassName.bestGuess(existingTypeName.name!!)
        }

        val identifier = key.toKotlinIdentifier().capitalize() // FIXME check symbol pool!
        return ClassName.bestGuess(identifier)
    }

    private fun typeForJsonArray(jsonArray: JsonArray, key: String): TypeName {
        val arrayTypes = HashSet<TypeName>()
        var nullable = false

        jsonArray.withIndex().forEach {
            val sanitisedName = key.toKotlinIdentifier() // FIXME check symbol pool!
            with(it.value) {
                when {
                    isJsonPrimitive -> arrayTypes.add(typeForJsonPrimitive(asJsonPrimitive))
                    isJsonArray -> arrayTypes.add(typeForJsonArray(asJsonArray, nameForArrayField(it.index, sanitisedName)))
                    isJsonObject -> arrayTypes.add(typeForJsonObject(asJsonObject, nameForArrayField(it.index, sanitisedName)))
                    isJsonNull -> nullable = true
                    else -> throw IllegalStateException("Unexpected state in array")
                }
            }
        }
        val arrayType = deduceArrayType(arrayTypes, nullable)
        return ParameterizedTypeName.get(Array<Any>::class.asClassName(), arrayType)
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