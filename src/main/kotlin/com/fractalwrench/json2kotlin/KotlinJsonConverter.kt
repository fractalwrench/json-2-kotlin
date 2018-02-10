package com.fractalwrench.json2kotlin

import com.google.gson.*
import com.squareup.kotlinpoet.*
import java.io.OutputStream
import java.util.*

class KotlinJsonConverter(private val jsonParser: JsonParser) {

    private var sourceFile: FileSpec.Builder = FileSpec.builder("", "")
    private val stack = Stack<TypeSpec.Builder>()

    private val bfsQueue = LinkedList<JsonElement>()

    fun convert(input: String, output: OutputStream, args: ConversionArgs) {
        try {
            if (input.isEmpty()) {
                throw IllegalArgumentException("Json input empty")
            }

            val json = readJsonTree(input, args)
            processJsonObject(json, args.rootClassName)
            generateSourceFile(args, output)
        } catch (e: JsonSyntaxException) {
            throw IllegalArgumentException("Invalid JSON supplied", e)
        }
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