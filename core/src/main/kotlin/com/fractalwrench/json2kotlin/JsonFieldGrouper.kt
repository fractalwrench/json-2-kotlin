package com.fractalwrench.json2kotlin

/**
 * Determines whether multiple JsonObjects share any commonality (i.e. whether they should be
 * represented by the same type).
 *
 * The strategy used to determine commonality can be supplied as a constructor parameter.
 */
internal class JsonFieldGrouper(private val groupingStrategy: GroupingStrategy = ::defaultGroupingStrategy) {

    /**
     * Recursively groups a List of JSONElementstogether by any commonality (i.e. whether they should be
     * represented by the same type)
     */
    fun groupJsonObjects(jsonElements: MutableList<TypedJsonElement>): List<List<TypedJsonElement>> {
        val allTypes: MutableList<MutableList<TypedJsonElement>> = mutableListOf()

        while (jsonElements.isNotEmpty()) {
            val typeList = mutableListOf<TypedJsonElement>()
            allTypes.add(typeList)
            findCommonTypesForElement(jsonElements.first(), jsonElements, typeList)
        }
        return allTypes
    }

    /**
     * Recursively finds any commonality between types in a collection of JSON objects.
     *
     * Recursion is necessary to detect transitive relationships. For example, an object that only contains a
     * key of "foo" may be the same type as an object that only contains a key of "bar", if another object exists
     * which contains both "foo" and "bar" keys.
     */
    private fun findCommonTypesForElement(element: TypedJsonElement,
                                          allObjects: MutableList<TypedJsonElement>,
                                          commonTypeList: MutableList<TypedJsonElement>) {
        val sameTypes = allObjects
                .filter { groupingStrategy(element, it) }

        commonTypeList.addAll(sameTypes)
        allObjects.removeAll(sameTypes)

        sameTypes.forEach {
            findCommonTypesForElement(it, allObjects, commonTypeList)
        }
    }

}