package com.fractalwrench.json2kotlin

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.squareup.kotlinpoet.*
import java.util.HashSet

/**
 * Deduces the TypeName for a JSON field value. For a primitive such as a String, this is a simple operation.
 * For complex values such as Arrays, Objects, and Nulls, this requires various considerations,
 * some of which are enumerated below:
 *
 * - Whether the type already exists
 * - Nullability of other elements
 * - Generic Parameters
 * - Object nesting
 */
internal class JsonTypeDetector {

    /**
     * Determines an returns the TypeName for a JSONElement
     */
    internal fun typeForJsonElement(jsonElement: JsonElement,
                                    key: String,
                                    jsonElementMap: Map<JsonElement, TypeSpec>): TypeName {
        with(jsonElement) {
            return when {
                isJsonPrimitive -> typeForJsonPrimitive(asJsonPrimitive)
                isJsonArray -> typeForJsonArray(asJsonArray, key, jsonElementMap)
                isJsonObject -> typeForJsonObject(asJsonObject, key, jsonElementMap)
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

    private fun typeForJsonObject(jsonObject: JsonObject,
                                   key: String,
                                   jsonElementMap: Map<JsonElement, TypeSpec>): TypeName {
        val existingTypeName = jsonElementMap[jsonObject]
        val identifier = existingTypeName?.name ?: key.toKotlinIdentifier().capitalize()
        return ClassName.bestGuess(identifier)
    }

    private fun typeForJsonArray(jsonArray: JsonArray,
                                  key: String,
                                  jsonElementMap: Map<JsonElement, TypeSpec>): TypeName {
        val pair = findAllArrayTypes(jsonArray, key, jsonElementMap)
        val arrayTypes = pair.first
        val nullable = pair.second
        val arrayType = deduceArrayType(arrayTypes, nullable)
        return ParameterizedTypeName.get(Array<Any>::class.asClassName(), arrayType)
    }

    private fun findAllArrayTypes(jsonArray: JsonArray,
                                  key: String,
                                  jsonElementMap: Map<JsonElement, TypeSpec>): Pair<HashSet<TypeName>, Boolean> {
        val arrayTypes = HashSet<TypeName>()
        var nullable = false

        jsonArray.withIndex().forEach {
            val sanitisedName = key.toKotlinIdentifier()
            val fieldKey = nameForArrayField(it.index, sanitisedName)

            with(it.value) {
                when {
                    isJsonPrimitive -> arrayTypes.add(typeForJsonPrimitive(asJsonPrimitive))
                    isJsonArray -> arrayTypes.add(typeForJsonArray(asJsonArray, fieldKey, jsonElementMap))
                    isJsonObject -> arrayTypes.add(typeForJsonObject(asJsonObject, fieldKey, jsonElementMap))
                    isJsonNull -> nullable = true
                    else -> throw IllegalStateException("Unexpected state in array")
                }
            }
        }
        return Pair(arrayTypes, nullable)
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
}