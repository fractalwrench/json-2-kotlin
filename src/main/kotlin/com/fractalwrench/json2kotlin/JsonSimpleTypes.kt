package com.fractalwrench.json2kotlin

import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import java.util.HashSet

/**
 * Determines whether two JSON Objects on the same level of a JSON tree share the same class type.
 *
 * The grouping strategy used here is very simple. If either of the JSON objects contain the same key as one of
 * the others, then each object is of the same type. The only exception to this rule is the case of an empty object.
 */
internal fun hasSameClassType(lhs: TypedJsonElement, rhs: TypedJsonElement): Boolean {
    val lhsKeys = lhs.asJsonObject.keySet()
    val rhsKeys = rhs.asJsonObject.keySet()
    val emptyClasses = (lhsKeys.isEmpty() || rhsKeys.isEmpty())// && Math.abs(lhsKeys.size - rhsKeys.size) == 1
    val hasCommonKeys = lhsKeys.intersect(rhsKeys).isNotEmpty()
    return hasCommonKeys || emptyClasses
}

internal fun processJsonPrimitive(primitive: JsonPrimitive): TypeName {
    return when {
        primitive.isBoolean -> Boolean::class
        primitive.isNumber -> Number::class
        primitive.isString -> String::class
        else -> throw IllegalStateException("No type found for JSON primitive " + primitive)
    }.asTypeName()
}

internal fun nameForArrayField(sanitisedName: String) = "${sanitisedName}Array"

internal fun nameForObjectInArray(it: IndexedValue<JsonElement>, sanitisedName: String): String {
    return if (it.index > 0) "$sanitisedName${it.index + 1}" else sanitisedName
}


// TODO tidy from here

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

/**
 * Determines a single type which fits multiple types.
 */
internal fun reduceToSingleType(types: List<TypeName?>): TypeName {
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