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
    val emptyClasses = (lhsKeys.isEmpty() || rhsKeys.isEmpty())

    val keySize = if (lhsKeys.size > rhsKeys.size) lhsKeys.size else rhsKeys.size
    val commonKeyCount = if (emptyClasses) 1 else lhsKeys.intersect(rhsKeys).size
    return (commonKeyCount * 5) >= keySize // at least a fifth of keys must match
} // FIXME should also consider relative size of objects (if lhs has 99 keys, and rhs has 1 key, then they shouldn't match)

