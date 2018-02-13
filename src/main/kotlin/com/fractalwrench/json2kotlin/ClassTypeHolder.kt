package com.fractalwrench.json2kotlin

import com.squareup.kotlinpoet.*
import java.util.*


internal class ClassTypeHolder : TraversalDelegate {

    internal val stack = Stack<TypeSpec>()
    private val jsonProcessor = JsonProcessor()
    private val jsonFieldGrouper = JsonFieldGrouper()

    /**
     * Processes a single level in the tree
     */
    override fun processTreeLevel(levelQueue: LinkedList<TypedJsonElement>) {
        val fieldValues = levelQueue.filter { it.isJsonObject }.toMutableList()
        fieldValues.forEach { println(it) }

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

        val identifier = commonElements.last().name
        val buildClass = buildClass(identifier, fields.sortedBy { it.toKotlinIdentifier().toLowerCase() }, commonElements)
        val classType = buildClass.build()

        // add to map for lookup on next level
        return commonElements.filterNot {
            val containsValue = jsonProcessor.jsonElementMap.containsValue(classType)
            jsonProcessor.jsonElementMap.put(it.jsonElement, classType)
            containsValue
        }.map { classType }
    }

    private fun buildClass(identifier: String, fields: Collection<String>, commonElements: List<TypedJsonElement>): TypeSpec.Builder {
        val classBuilder = TypeSpec.classBuilder(identifier.toKotlinIdentifier().capitalize())
        val constructor = FunSpec.constructorBuilder()

        if (fields.isEmpty()) {
            return classBuilder
        }

        classBuilder.addModifiers(KModifier.DATA) // non-empty classes allow data modifier

        val fieldTypeMap = jsonProcessor.processFieldType(fields, commonElements)

        for (field in fields) {
            val sanitisedName = field.toKotlinIdentifier()
            val typeName = fieldTypeMap[field]
            val initializer = PropertySpec.builder(sanitisedName, typeName!!).initializer(sanitisedName)
            classBuilder.addProperty(initializer.build())
            constructor.addParameter(sanitisedName, typeName)
        }

        classBuilder.primaryConstructor(constructor.build())
        return classBuilder
    }


}

