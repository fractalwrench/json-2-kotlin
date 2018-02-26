package com.fractalwrench.json2kotlin

import com.google.gson.JsonElement
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asTypeName
import java.util.*

// TODO docs
internal class JsonProcessor(private val typeDetector: JsonTypeDetector) { // TODO crappy name

    internal val jsonElementMap = HashMap<JsonElement, TypeSpec>() // FIXME feels wrong having this exposed, return instead?

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
            if (fieldValue != null) typeDetector.typeForJsonElement(fieldValue, key, jsonElementMap) else null
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