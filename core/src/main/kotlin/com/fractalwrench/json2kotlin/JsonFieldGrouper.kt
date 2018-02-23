package com.fractalwrench.json2kotlin

internal class JsonFieldGrouper {

    fun groupCommonFieldValues(allObjects: MutableList<TypedJsonElement>): List<List<TypedJsonElement>> {
        val allTypes: MutableList<MutableList<TypedJsonElement>> = mutableListOf()

        while (allObjects.isNotEmpty()) {
            val typeList = mutableListOf<TypedJsonElement>()
            allTypes.add(typeList)
            findCommonTypesForElement(allObjects.first(), allObjects, typeList)
        }
        return allTypes
    }

    /**
     * Recursively finds any commonality between types in a collection of JSON objects. Commonality between
     * two objects is defined as them sharing one or more key value.
     *
     * Recursion is necessary to detect transitive relationships. For example, an object that only contains a
     * key of "foo" may be the same type as an object that only contains a key of "bar", if another object exists
     * which contains both "foo" and "bar" keys.
     */
    private fun findCommonTypesForElement(element: TypedJsonElement,
                                          allObjects: MutableList<TypedJsonElement>,
                                          commonTypeList: MutableList<TypedJsonElement>) {
        val sameTypes = allObjects
                .filter { hasSameClassType(element, it) }

        commonTypeList.addAll(sameTypes)
        allObjects.removeAll(sameTypes)

        sameTypes.forEach {
            findCommonTypesForElement(it, allObjects, commonTypeList)
        }
    }

    /**
     * Determines whether two JSON Objects on the same level of a JSON tree share the same class type.
     *
     * The grouping strategy used here is fairly simple. If either of the JSON objects contain 1/3 of the same
     * keys as one of the others, then each object is of the same type.
     *
     * The only exception to this rule is the case of an empty object, which is considered to have 1 common key
     * with the other class, as this allows grouping with smaller objects, but discourages it with larger ones.
     */
    private fun hasSameClassType(lhs: TypedJsonElement, rhs: TypedJsonElement): Boolean {
        val lhsKeys = lhs.asJsonObject.keySet()
        val rhsKeys = rhs.asJsonObject.keySet()
        val emptyClasses = (lhsKeys.isEmpty() || rhsKeys.isEmpty())

        val keySize = if (lhsKeys.size > rhsKeys.size) lhsKeys.size else rhsKeys.size
        val commonKeyCount = if (emptyClasses) 1 else lhsKeys.intersect(rhsKeys).size
        return (commonKeyCount * 3) >= keySize // at least a third of keys must match
    }
}