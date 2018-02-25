package com.fractalwrench.json2kotlin

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.squareup.kotlinpoet.*
import java.util.HashSet

internal class JsonTypeDetector {
    
    internal fun typeForJsonField(jsonElement: JsonElement, key: String, jsonElementMap: Map<JsonElement, TypeSpec>): TypeName {
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

    internal fun typeForJsonPrimitive(primitive: JsonPrimitive): TypeName {
        return when {
            primitive.isBoolean -> Boolean::class
            primitive.isNumber -> Number::class
            primitive.isString -> String::class
            else -> throw IllegalStateException("No type found for JSON primitive " + primitive)
        }.asTypeName()
    }


    // FIXME feels really messy from here on out


    internal fun typeForJsonObject(jsonObject: JsonObject, key: String, jsonElementMap: Map<JsonElement, TypeSpec>): TypeName {
        val existingTypeName = jsonElementMap[jsonObject]
        if (existingTypeName != null) {
            return ClassName.bestGuess(existingTypeName.name!!)
        }

        val identifier = key.toKotlinIdentifier().capitalize() // FIXME check symbol pool!
        return ClassName.bestGuess(identifier)
    }

    internal fun typeForJsonArray(jsonArray: JsonArray, key: String, jsonElementMap: Map<JsonElement, TypeSpec>): TypeName {
        val arrayTypes = HashSet<TypeName>()
        var nullable = false

        jsonArray.withIndex().forEach {
            val sanitisedName = key.toKotlinIdentifier() // FIXME check symbol pool!
            with(it.value) {
                when {
                    isJsonPrimitive -> arrayTypes.add(typeForJsonPrimitive(asJsonPrimitive))
                    isJsonArray -> arrayTypes.add(typeForJsonArray(asJsonArray, nameForArrayField(it.index, sanitisedName), jsonElementMap))
                    isJsonObject -> arrayTypes.add(typeForJsonObject(asJsonObject, nameForArrayField(it.index, sanitisedName), jsonElementMap))
                    isJsonNull -> nullable = true
                    else -> throw IllegalStateException("Unexpected state in array")
                }
            }
        }
        val arrayType = deduceArrayType(arrayTypes, nullable)
        return ParameterizedTypeName.get(Array<Any>::class.asClassName(), arrayType)
    }

    internal fun deduceArrayType(arrayTypes: HashSet<TypeName>, nullable: Boolean): TypeName {
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