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

        val classType = buildClass(commonElements, fields.sortedBy {
            it.toKotlinIdentifier().toLowerCase()
        }).build()

        return commonElements.filterNot { // reuse any types which already exist in the map
            val containsValue = jsonProcessor.jsonElementMap.containsValue(classType)
            jsonProcessor.jsonElementMap.put(it.jsonElement, classType) // FIXME weird
            containsValue
        }.map { classType }
    }

    private fun buildClass(commonElements: List<TypedJsonElement>, fields: Collection<String>): TypeSpec.Builder {
        val identifier = commonElements.last().name
        val classBuilder = TypeSpec.classBuilder(identifier.toKotlinIdentifier().capitalize())
        val constructor = FunSpec.constructorBuilder()

        if (fields.isEmpty()) {
            return classBuilder
        }

        val fieldTypeMap = jsonProcessor.findDistinctTypesForFields(fields, commonElements)
        fields.forEach { addProperty(it, fieldTypeMap, classBuilder, constructor) }

        classBuilder.addModifiers(KModifier.DATA) // non-empty classes allow data modifier
        classBuilder.primaryConstructor(constructor.build())
        return classBuilder
    }

    private fun addProperty(field: String, fieldTypeMap: Map<String, TypeName>, classBuilder: TypeSpec.Builder, constructor: FunSpec.Builder) {
        val sanitisedName = field.toKotlinIdentifier()
        val typeName = fieldTypeMap[field]
        val initializer = PropertySpec.builder(sanitisedName, typeName!!).initializer(sanitisedName)
        classBuilder.addProperty(initializer.build())
        constructor.addParameter(sanitisedName, typeName)
    }

}
