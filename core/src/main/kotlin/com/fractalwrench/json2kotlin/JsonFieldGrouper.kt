package com.fractalwrench.json2kotlin

internal class JsonFieldGrouper(val groupingStrategy: GroupingStrategy) {

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
                .filter { groupingStrategy(element, it) }

        commonTypeList.addAll(sameTypes)
        allObjects.removeAll(sameTypes)

        sameTypes.forEach {
            findCommonTypesForElement(it, allObjects, commonTypeList)
        }
    }

}