package com.fractalwrench.json2kotlin

import com.google.gson.*
import com.squareup.kotlinpoet.*
import java.io.OutputStream
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class KotlinJsonConverter(private val jsonParser: JsonParser) {

    private var sourceFile: FileSpec.Builder = FileSpec.builder("", "")
    private val stack = Stack<TypeSpec>()

    private val bfsStack = Stack<TypedJsonElement>()
    private val jsonElementMap = HashMap<JsonElement, TypeSpec>()
    private lateinit var args: ConversionArgs

    fun convert(input: String, output: OutputStream, args: ConversionArgs) {
        this.args = args
        try {
            if (input.isEmpty()) {
                throw IllegalArgumentException("Json input empty")
            }
            sourceFile = FileSpec.builder("", args.rootClassName)
            val jsonRoot = readJsonTree(input, args)
            buildQueue(jsonRoot, null)
            processQueue()
            generateSourceFile(args, output)
        } catch (e: JsonSyntaxException) {
            throw IllegalArgumentException("Invalid JSON supplied", e)
        }
    }

    /**
     * Adds the JSON nodes to a stack using BFS. This permits a reverse level order traversal, which is used to build
     * class types from the bottom up.
     */
    private fun buildQueue(element: JsonElement, key: String?, depth: Int = 0) {
        if (depth == 0) {
            bfsStack.add(TypedJsonElement(element, args.rootClassName, depth))
        }

        val newDepth = depth + 1

        when {
            element.isJsonObject -> {
                val entrySet = element.asJsonObject.entrySet()
                entrySet.mapTo(bfsStack) { TypedJsonElement(it.value, it.key, newDepth) }
                entrySet.forEach { buildQueue(it.value, it.key, newDepth) }
            }
            element.isJsonArray -> {
                val array = element.asJsonArray
                val identifier = key ?: throw IllegalStateException("Expected geberated identifier for array element")

                array.forEachIndexed { index, jsonElement ->
                    val genName = if (index == 0) identifier else "$identifier${index + 1}"
                    bfsStack.add(TypedJsonElement(jsonElement, genName, newDepth))
                }
                array.forEachIndexed { index, jsonElement ->
                    val genName = if (index == 0) identifier else "$identifier${index + 1}" // FIXME DRY
                    buildQueue(jsonElement, genName, newDepth)
                }
            }
        }
    }

    /**
     * Processes JSON nodes in a reverse level order traversal, by building class types for each level of the tree.
     */
    private fun processQueue() {
        var depth = -1
        val levelQueue = LinkedList<TypedJsonElement>()

        while (bfsStack.isNotEmpty()) {
            val pop = bfsStack.pop()

            if (depth != -1 && pop.depth != depth) {
                processTreeLevel(levelQueue, depth)
            }
            levelQueue.add(pop)
            depth = pop.depth
        }
        processTreeLevel(levelQueue, depth)
    }

    /**
     * Processes a single level in the tree
     */
    private fun processTreeLevel(levelQueue: LinkedList<TypedJsonElement>, depth: Int) {
        println("Processing level $depth")
        val objects = levelQueue.filter { it.isJsonObject }.toMutableList()
        objects.forEach { println(it) }

        val commonTypes = determineCommonTypes(objects)
        commonTypes.forEach(this::processCommonType)
        levelQueue.clear()
    }

    private fun determineCommonTypes(allObjects: MutableList<TypedJsonElement>): List<List<TypedJsonElement>> {
        val types: MutableList<MutableList<TypedJsonElement>> = mutableListOf()

        while (allObjects.isNotEmpty()) {
            val commonTypeList = mutableListOf<TypedJsonElement>()
            types.add(commonTypeList)

            val first = allObjects.first()
            allObjects.remove(first)
            commonTypeList.add(first)
            findCommonTypesForElement(first, allObjects, commonTypeList)
        }
        return types
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

    /**
     * Determines whether two JSON Objects on the same level of a JSON tree share the same class type.
     *
     * The grouping strategy used here is very simple. If either of the JSON objects contain the same key as one of
     * the others, then each object is of the same type. The only exception to this rule is the case of an empty object.
     */
    private fun hasSameClassType(lhs: TypedJsonElement, rhs: TypedJsonElement): Boolean {
        val lhsKeys = lhs.asJsonObject.keySet()
        val rhsKeys = rhs.asJsonObject.keySet()
        val emptyClasses = lhsKeys.isEmpty() && rhsKeys.isEmpty()
        val hasCommonKeys = lhsKeys.intersect(rhsKeys).isNotEmpty()
        return hasCommonKeys || emptyClasses
    }

    private fun processCommonType(commonElements: List<TypedJsonElement>) { // TODO assumes an object!
        val fields = HashSet<String>()

        commonElements.forEach {
            fields.addAll(it.asJsonObject.keySet())
        }

        val identifier = commonElements.last().name
        val buildClass = buildClass(identifier, fields.sortedBy { it.toKotlinIdentifier().toLowerCase() }, commonElements)
        val classType = buildClass.build()

        // add to map for lookup on next level
        commonElements.forEach {
            val containsValue = jsonElementMap.containsValue(classType)
            jsonElementMap.put(it.jsonElement, classType)

            if (!containsValue) { // e.g. an object in an array can be found multiple times, only want to define once
                stack.add(classType)
            }
        }
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

        for (field in fields) { // TODO need to handle differently if an array!
            val distinctTypes = commonElements.map {
                val value = it.asJsonObject.get(field)
                if (value != null) processJsonField(value, field) else null
            }.distinct()

            val typeName = reduceToSingleType(distinctTypes)
            fieldMap.put(field, typeName)
        }
        return fieldMap
    }

    /**
     * Determines a single type which fits multiple types.
     */
    private fun reduceToSingleType(types: List<TypeName?>): TypeName {
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

    private fun generateSourceFile(args: ConversionArgs, output: OutputStream) {
        while (stack.isNotEmpty()) {
            sourceFile.addType(stack.pop())
        }

        val stringBuilder = StringBuilder()
        sourceFile.build().writeTo(stringBuilder)
        output.write(stringBuilder.toString().toByteArray())
    }

    private fun processJsonPrimitive(primitive: JsonPrimitive): TypeName {
        return when {
            primitive.isBoolean -> Boolean::class
            primitive.isNumber -> Number::class
            primitive.isString -> String::class
            else -> throw IllegalStateException("No type found for JSON primitive " + primitive)
        }.asTypeName()
    }

    private fun readJsonTree(input: String, args: ConversionArgs): JsonObject {
        var rootElement = jsonParser.parse(input)

        if (rootElement.isJsonArray) {
            rootElement = processRootArrayWrapper(rootElement.asJsonArray, args.rootClassName)
        }
        return rootElement?.asJsonObject ?: throw IllegalStateException("Failed to read json object")
    }

    /**
     * Adds an object as root which wraps the array
     */
    private fun processRootArrayWrapper(jsonArray: JsonArray, className: String): JsonObject {
        val arrayName = nameForArrayField(className).decapitalize()
        return JsonObject().apply { add(arrayName, jsonArray) }
    }

    private fun nameForArrayField(sanitisedName: String) = "${sanitisedName}Array"

    private fun nameForObjectInArray(it: IndexedValue<JsonElement>, sanitisedName: String): String {
        return if (it.index > 0) "$sanitisedName${it.index + 1}" else sanitisedName
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
        return when {
            jsonElement.isJsonPrimitive -> processJsonPrimitive(jsonElement.asJsonPrimitive)
            jsonElement.isJsonArray -> processJsonArray(jsonElement.asJsonArray, key) // FIXME
            jsonElement.isJsonObject -> processJsonObject(jsonElement.asJsonObject, key) // FIXME
            jsonElement.isJsonNull -> Any::class.asTypeName().asNullable()
            else -> throw IllegalStateException("Expected a JSON value")
        }
    }

    private fun processJsonArray(jsonArray: JsonArray, key: String): TypeName {
        val arrayTypes = HashSet<TypeName>()
        var nullable = false

        jsonArray.withIndex().forEach {
            val sanitisedName = key.toKotlinIdentifier()
            val element = it.value

            when {
                element.isJsonPrimitive ->
                    arrayTypes.add(processJsonField(element.asJsonPrimitive, sanitisedName))
                element.isJsonArray ->
                    arrayTypes.add(processJsonArray(element.asJsonArray, nameForArrayField(sanitisedName)))
                element.isJsonObject ->
                    arrayTypes.add(processJsonObject(element.asJsonObject, nameForObjectInArray(it, sanitisedName)))
                element.isJsonNull -> nullable = true
                else -> throw IllegalStateException("Unexpected state in array")
            }
        }

        val arrayType = deduceArrayType(arrayTypes, nullable)
        return ParameterizedTypeName.get(Array<Any>::class.asClassName(), arrayType)
    }

    private fun deduceArrayType(arrayTypes: HashSet<TypeName>, nullable: Boolean): TypeName {
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

}