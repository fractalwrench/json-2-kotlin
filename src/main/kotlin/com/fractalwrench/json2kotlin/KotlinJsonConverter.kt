package com.fractalwrench.json2kotlin

import com.google.gson.*
import com.squareup.kotlinpoet.*
import java.io.OutputStream
import java.util.*

class KotlinJsonConverter(private val jsonParser: JsonParser) {

    private var sourceFile: FileSpec.Builder = FileSpec.builder("", "")
    private val stack = Stack<TypeSpec.Builder>()

    private val bfsStack = Stack<TypedJsonElement>()

    fun convert(input: String, output: OutputStream, args: ConversionArgs) {
        try {
            if (input.isEmpty()) {
                throw IllegalArgumentException("Json input empty")
            }

            val jsonRoot = readJsonTree(input, args)
            buildQueue(jsonRoot)
            processQueue()

            // TODO outdated
            processJsonObject(jsonRoot, args.rootClassName)
            generateSourceFile(args, output)
        } catch (e: JsonSyntaxException) {
            throw IllegalArgumentException("Invalid JSON supplied", e)
        }
    }

    /**
     * Adds the JSON nodes to a stack using BFS. This permits a reverse level order traversal, which is used to build
     * class types from the bottom up.
     */
    private fun buildQueue(element: JsonElement, depth: Int = 0) {
        if (depth == 0) {
            bfsStack.add(TypedJsonElement(element, depth))
        }

        val values = when {
            element.isJsonObject -> element.asJsonObject.entrySet().map { it.value }
            element.isJsonArray -> element.asJsonArray
            else -> Collections.emptyList()
        }

        values.forEach {
            bfsStack.add(TypedJsonElement(it, depth + 1))
        }
        values.forEach {
            buildQueue(it, depth + 1)
        }
    }

    /**
     * Processes JSON nodes in a reverse level order traversal, by building class types for each level of the tree.
     */
    private fun processQueue() {
        var depth = -1

        while (!bfsStack.isEmpty()) {
            val pop = bfsStack.pop()

            if (depth != -1 && pop.depth != depth) {
                println("Processing level ${pop.depth}")
            }
            depth = pop.depth

            when { // TODO won't know the name for the type immediately
                pop.isJsonArray -> {
                    println(pop) // TODO actually process!
                }
                pop.isJsonObject -> {
                    println(pop) // TODO actually process!
                }
                else -> {
                }
            }
        }
    }

    /**
     * Determines whether two JSON Objects on the same level of a JSON tree share the same class type.
     *
     * The grouping strategy used here is very simple. If either of the JSON objects contain the same key as one of
     * the others, then each object is of the same type. The only exception to this rule is the case of an empty object.
     */
    private fun hasSameClassType(lhs: JsonObject, rhs: JsonObject): Boolean {
        val lhsKeys = lhs.keySet()
        val rhsKeys = rhs.keySet()
        val emptyClasses = lhsKeys.isEmpty() && rhsKeys.isEmpty()
        val hasCommonKeys = !lhsKeys.intersect(rhsKeys).isEmpty()
        return hasCommonKeys || emptyClasses
    }


    private fun generateSourceFile(args: ConversionArgs, output: OutputStream) {
        sourceFile = FileSpec.builder("", args.rootClassName)

        while (!stack.isEmpty()) {
            sourceFile.addType(stack.pop().build())
        }

        val stringBuilder = StringBuilder()
        sourceFile.build().writeTo(stringBuilder)
        output.write(stringBuilder.toString().toByteArray())
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
        val arrayName = nameForArrayField(className).decapitalize() // TODO
        return JsonObject().apply { add(arrayName, jsonArray) }
    }


    /** Begin processing actual JSON **/


    private fun processJsonObject(jsonObject: JsonObject, key: String): TypeName {
        val identifier = key.toKotlinIdentifier().capitalize()
        val classBuilder = TypeSpec.classBuilder(identifier)

        if (jsonObject.size() > 0) {
            val constructor = FunSpec.constructorBuilder()
            classBuilder.addModifiers(KModifier.DATA) // non-empty classes allow data modifier
            processJsonObjectFields(jsonObject, constructor, classBuilder)
            classBuilder.primaryConstructor(constructor.build())
        }
        stack.add(classBuilder)
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
            jsonElement.isJsonArray -> processJsonArray(jsonElement.asJsonArray, key)
            jsonElement.isJsonObject -> processJsonObject(jsonElement.asJsonObject, key)
            jsonElement.isJsonNull -> Any::class.asTypeName().asNullable()
            else -> throw IllegalStateException("Expected a JSON value")
        }
    }

    private fun processJsonPrimitive(primitive: JsonPrimitive): TypeName {
        return when {
            primitive.isBoolean -> Boolean::class
            primitive.isNumber -> Number::class
            primitive.isString -> String::class
            else -> throw IllegalStateException("No type found for JSON primitive " + primitive)
        }.asTypeName()
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

    private fun nameForArrayField(sanitisedName: String) = "${sanitisedName}Array"

    private fun nameForObjectInArray(it: IndexedValue<JsonElement>, sanitisedName: String): String {
        return if (it.index > 0) "$sanitisedName${it.index + 1}" else sanitisedName
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