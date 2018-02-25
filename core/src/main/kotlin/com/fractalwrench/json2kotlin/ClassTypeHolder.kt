package com.fractalwrench.json2kotlin

import com.squareup.kotlinpoet.*
import java.util.*


internal class ClassTypeHolder(val delegate: SourceBuildDelegate, groupingStrategy: GroupingStrategy) { // TODO rename, bad ontology

    internal val stack = Stack<TypeSpec>()
    private val jsonProcessor = JsonProcessor(JsonTypeDetector())
    private val jsonFieldGrouper = JsonFieldGrouper(groupingStrategy)


    /**
     * Processes JSON nodes in a reverse level order traversal,
     * by building class types for each level of the tree.
     */
    fun processQueue(bfsStack: Stack<TypedJsonElement>) { // TODO split into two separate classes, as separate responsibilities.
        var level = -1
        val levelQueue = LinkedList<TypedJsonElement>()

        while (bfsStack.isNotEmpty()) {
            val pop = bfsStack.pop()

            if (level != -1 && pop.level != level) {
                handleLevel(levelQueue)
            }
            levelQueue.add(pop)
            level = pop.level
        }
        handleLevel(levelQueue)
    }
    private fun handleLevel(levelQueue: LinkedList<TypedJsonElement>) {
        processTreeLevel(levelQueue)
    }


    /**
     * Processes a single level in the tree
     */
    private fun processTreeLevel(levelQueue: LinkedList<TypedJsonElement>) {
        val fieldValues = levelQueue.filter { it.isJsonObject }.toMutableList()

        jsonFieldGrouper.groupCommonFieldValues(fieldValues)
                .flatMap { convertFieldsToTypes(it) }
                .sortedByDescending { it.name }
                .forEach { stack += it }
        levelQueue.clear()
    }

    /**
     * Converts a List of JSON elements which share common fields into a Kotlin type
     */
    private fun convertFieldsToTypes(commonElements: List<TypedJsonElement>): List<TypeSpec> {
        val fields = HashSet<String>()

        commonElements.forEach {
            fields.addAll(it.asJsonObject.keySet())
        }

        val classType = buildClass(commonElements, fields.sortedBy {
            it.toKotlinIdentifier().toLowerCase()
        }).build()

        return commonElements.filterNot { // reuse any types which already exist in the map
            val containsValue = jsonProcessor.jsonElementMap.containsValue(classType)
            jsonProcessor.jsonElementMap.put(it.jsonElement, classType) // FIXME feels weird
            containsValue
        }.map { classType }
    }

    private fun buildClass(commonElements: List<TypedJsonElement>, fields: Collection<String>): TypeSpec.Builder {
        val identifier = commonElements.last().kotlinIdentifier
        val classBuilder = TypeSpec.classBuilder(identifier.capitalize())
        val constructor = FunSpec.constructorBuilder()

        if (fields.isNotEmpty()) {
            val fieldTypeMap = jsonProcessor.findDistinctTypesForFields(fields, commonElements)
            fields.forEach { addProperty(it, fieldTypeMap, classBuilder, constructor) }
            classBuilder.addModifiers(KModifier.DATA) // non-empty classes allow data modifier
            classBuilder.primaryConstructor(constructor.build())
        }

        delegate.prepareClass(classBuilder, identifier, commonElements.last())
        return classBuilder
    }

    private fun addProperty(field: String, fieldTypeMap: Map<String, TypeName>, classBuilder: TypeSpec.Builder, constructor: FunSpec.Builder) {
        val sanitisedName = field.toKotlinIdentifier() // FIXME should be done before this
        val typeName = fieldTypeMap[field]
        val initializer = PropertySpec.builder(sanitisedName, typeName!!).initializer(sanitisedName)
        delegate.prepareClassProperty(initializer, sanitisedName, field)
        classBuilder.addProperty(initializer.build())
        constructor.addParameter(sanitisedName, typeName)
    }

}
