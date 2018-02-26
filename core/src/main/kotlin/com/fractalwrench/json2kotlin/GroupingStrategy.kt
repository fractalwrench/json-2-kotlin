package com.fractalwrench.json2kotlin


internal typealias GroupingStrategy = (lhs: TypedJsonElement, rhs: TypedJsonElement) -> Boolean

/**
 * Determines whether two JSON Objects on the same level of a JSON tree share the same class type.
 *
 * The grouping strategy used here is fairly simple. If either of the JSON objects contain 1/5 of the same
 * keys as one of the others, then each object is of the same type.
 *
 * The only exception to this rule is the case of an empty object, which is considered to have 1 common key
 * with the other class, as this allows grouping with smaller objects, but discourages it with larger ones.
 */
internal fun defaultGroupingStrategy(lhs: TypedJsonElement, rhs: TypedJsonElement): Boolean {
    val lhsKeys = lhs.asJsonObject.keySet()
    val rhsKeys = rhs.asJsonObject.keySet()
    val lhsSize = lhsKeys.size
    val rhsSize = rhsKeys.size
    val emptyClasses = (lhsKeys.isEmpty() || rhsKeys.isEmpty())

    val maxKeySize = if (lhsSize > rhsSize) lhsSize else rhsSize
    val commonKeyCount = if (emptyClasses) 1 else lhsKeys.intersect(rhsKeys).size

    return (commonKeyCount * 5) >= maxKeySize // at least a fifth of keys must match
}

