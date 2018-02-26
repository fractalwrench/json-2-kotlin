package com.fractalwrench.json2kotlin

import com.google.gson.JsonElement
import com.squareup.kotlinpoet.*
import java.util.*

/**
 * Finds distinct types for a collection of fields.
 */
internal class TypeReducer(private val typeDetector: JsonTypeDetector) {

    fun findDistinctTypes(fields: Collection<String>,
                          commonElements: List<TypedJsonElement>,
                          jsonElementMap: HashMap<JsonElement, TypeSpec>): Map<String, TypeName> {
        val fieldMap = HashMap<String, TypeName>()

        fields.forEach {
            val distinctTypes = findDistinctTypesForField(commonElements, it, jsonElementMap)
            fieldMap.put(it, reduceToSingleType(distinctTypes))
        }
        return fieldMap
    }

    private fun findDistinctTypesForField(commonElements: List<TypedJsonElement>,
                                          key: String,
                                          jsonElementMap: HashMap<JsonElement, TypeSpec>): List<TypeName?> {
        return commonElements.map {
            val fieldValue = it.asJsonObject.get(key)
            if (fieldValue != null) {
                typeDetector.typeForJsonElement(fieldValue, key, jsonElementMap)
            } else {
                null
            }
        }.distinct()
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
            nonNullTypes.size == 2 -> reduceMultipleTypes(nonNullTypes, anyClz)
            else -> anyClz
        }
        return if (nullable) typeName.asNullable() else typeName
    }

    private fun reduceMultipleTypes(nonNullTypes: List<TypeName>, anyClz: ClassName): TypeName {
        val parameterizedTypeName
                = nonNullTypes.filterIsInstance(ParameterizedTypeName::class.java).firstOrNull()

        return when {
            parameterizedTypeName != null -> { // handle type params (recursive)
                val rawType = parameterizedTypeName.rawType
                parameterizedTypeName.typeArguments
                ParameterizedTypeName.get(rawType, reduceToSingleType(parameterizedTypeName.typeArguments))
            }
            nonNullTypes.contains(anyClz) -> // Any will be an empty/missing object
                nonNullTypes.filterNot { it == anyClz }.first()
            else -> anyClz
        }
    }

}