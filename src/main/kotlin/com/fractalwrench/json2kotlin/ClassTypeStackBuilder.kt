package com.fractalwrench.json2kotlin

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.squareup.kotlinpoet.*
import java.util.*

// TODO tidy/split

internal class ClassTypeStackBuilder: TraversalDelegate {

    internal val stack = Stack<TypeSpec>()
    private val jsonElementMap = HashMap<JsonElement, TypeSpec>()

    /**
     * Processes a single level in the tree
     */
    override fun processTreeLevel(levelQueue: LinkedList<TypedJsonElement>) {
        val fieldValues = levelQueue.filter { it.isJsonObject }.toMutableList()
        fieldValues.forEach { println(it) }

        groupCommonFieldValues(fieldValues)
                .flatMap { convertToTypeSpec(it) }
                .sortedByDescending { it.name }
                .forEach { stack += it }
        levelQueue.clear()
    }

    private fun groupCommonFieldValues(allObjects: MutableList<TypedJsonElement>): List<List<TypedJsonElement>> {
        val allTypes: MutableList<MutableList<TypedJsonElement>> = mutableListOf()

        while (allObjects.isNotEmpty()) {
            val typeList = mutableListOf<TypedJsonElement>()
            allTypes.add(typeList)

            val first = allObjects.first()
            allObjects.remove(first)
            typeList.add(first)
            findCommonTypesForElement(first, allObjects, typeList)
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
        val sameTypes = allObjects.filter { hasSameClassType(element, it) }
        commonTypeList.addAll(sameTypes)
        allObjects.removeAll(sameTypes)

        sameTypes.forEach {
            findCommonTypesForElement(it, allObjects, commonTypeList)
        }
    }

    private fun convertToTypeSpec(commonElements: List<TypedJsonElement>): List<TypeSpec> {
        val fields = HashSet<String>()

        commonElements.forEach {
            fields.addAll(it.asJsonObject.keySet())
        }

        val identifier = commonElements.last().name
        val buildClass = buildClass(identifier, fields.sortedBy { it.toKotlinIdentifier().toLowerCase() }, commonElements)
        val classType = buildClass.build()

        // add to map for lookup on next level
        return commonElements.filterNot {
            val containsValue = jsonElementMap.containsValue(classType)
            jsonElementMap.put(it.jsonElement, classType)
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

        val fieldTypeMap = processFieldType(fields, commonElements)

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

    private fun processFieldType(fields: Collection<String>, commonElements: List<TypedJsonElement>): Map<String, TypeName> {
        val fieldMap = HashMap<String, TypeName>()

        for (field in fields) {
            val distinctTypes = commonElements.map {
                val value = it.asJsonObject.get(field)
                if (value != null) processJsonField(value, field) else null
            }.distinct()

            val typeName = reduceToSingleType(distinctTypes)
            fieldMap.put(field, typeName)
        }
        return fieldMap
    }



    private fun processJsonObject(jsonObject: JsonObject, key: String): TypeName {
        val get = jsonElementMap[jsonObject]
        if (get != null) {
            return ClassName.bestGuess(get.name!!)
        }

        val identifier = key.toKotlinIdentifier().capitalize()
        val classBuilder = TypeSpec.classBuilder(identifier)

        if (jsonObject.size() > 0) {
            val constructor = FunSpec.constructorBuilder()
            classBuilder.addModifiers(KModifier.DATA) // non-empty classes allow data modifier
            processJsonObjectFields(jsonObject, constructor, classBuilder)
            classBuilder.primaryConstructor(constructor.build())
        }
        return ClassName.bestGuess(identifier)
    }

    private fun processJsonObjectFields(jsonObject: JsonObject,
                                        constructor: FunSpec.Builder,
                                        classBuilder: TypeSpec.Builder) {
        jsonObject.entrySet().forEach {
            val fieldType = processJsonField(it.value, it.key)
            val identifier = it.key.toKotlinIdentifier()

            val initializer = PropertySpec.builder(identifier, fieldType).initializer(identifier)
            classBuilder.addProperty(initializer.build())
            constructor.addParameter(identifier, fieldType)
        }
    }

    private fun processJsonField(jsonElement: JsonElement, key: String): TypeName {
        with(jsonElement) {
            return when {
                isJsonPrimitive -> processJsonPrimitive(asJsonPrimitive)
                isJsonArray -> processJsonArray(asJsonArray, key)
                isJsonObject -> processJsonObject(asJsonObject, key)
                isJsonNull -> Any::class.asTypeName().asNullable()
                else -> throw IllegalStateException("Expected a JSON value")
            }
        }
    }

    private fun processJsonArray(jsonArray: JsonArray, key: String): TypeName {
        val arrayTypes = HashSet<TypeName>()
        var nullable = false

        jsonArray.withIndex().forEach {
            val sanitisedName = key.toKotlinIdentifier()
            with(it.value) {
                when {
                    isJsonPrimitive -> arrayTypes.add(processJsonField(asJsonPrimitive, sanitisedName))
                    isJsonArray -> arrayTypes.add(processJsonArray(asJsonArray, nameForArrayField(sanitisedName)))
                    isJsonObject -> arrayTypes.add(processJsonObject(asJsonObject, nameForObjectInArray(it, sanitisedName)))
                    isJsonNull -> nullable = true
                    else -> throw IllegalStateException("Unexpected state in array")
                }
            }
        }
        val arrayType = deduceArrayType(arrayTypes, nullable)
        return ParameterizedTypeName.get(Array<Any>::class.asClassName(), arrayType)
    }

}