package com.fractalwrench.json2kotlin

import com.google.gson.*
import com.squareup.kotlinpoet.*
import java.io.OutputStream
import java.util.*

class KotlinJsonConverter(val jsonParser: JsonParser) {

    var sourceFile: FileSpec.Builder = FileSpec.builder("", "")
    val stack = Stack<TypeSpec.Builder>()

    fun convert(input: String, output: OutputStream, args: ConversionArgs) {
        try {
            if (input.isEmpty()) {
                throw IllegalArgumentException("Json input empty")
            }
            val jsonRoot = jsonParser.parse(input)
            sourceFile = FileSpec.builder("", args.rootClassName)

            // TODO build up a Set of all the objects as a type representation

            when {
                jsonRoot.isJsonObject -> processJsonObject(jsonRoot.asJsonObject, args.rootClassName)
                jsonRoot.isJsonArray -> handleRootJsonArray(jsonRoot.asJsonArray, args.rootClassName)
                else -> throw IllegalStateException("Expected a JSON array or object")
            }

            while (!stack.isEmpty()) {
                sourceFile.addType(stack.pop().build())
            }

            val stringBuilder = StringBuilder()
            sourceFile.build().writeTo(stringBuilder)
            output.write(stringBuilder.toString().toByteArray())

        } catch (e: JsonSyntaxException) {
            throw IllegalArgumentException("Invalid JSON supplied", e)
        }
    }

    /**
     * Adds a wrapper object around the array
     */
    private fun handleRootJsonArray(jsonArray: JsonArray, className: String) {
        val fieldName = "${className}Field".decapitalize()
        val containerClassName = "${className}Container"
        val jsonElement = JsonObject().apply { add(fieldName, jsonArray) }
        processJsonObject(jsonElement, containerClassName)
    }

    private fun processJsonObject(jsonObject: JsonObject, className: String) {
        val classBuilder = TypeSpec.classBuilder(className)

        if (jsonObject.size() > 0) {
            val constructor = FunSpec.constructorBuilder()
            classBuilder.addModifiers(KModifier.DATA) // non-empty classes allow data modifier

            // find the type for each value then add the field to the class
            for (entry in jsonObject.entrySet()) {
                val valueType = findJsonValueType(entry.value)
                addDataClassProperty(entry.key, valueType, constructor, classBuilder)
            }
            classBuilder.primaryConstructor(constructor.build())
        }
        stack.add(classBuilder)
    }

    private fun addDataClassProperty(key: String, type: TypeName,
                                     constructor: FunSpec.Builder, classBuilder: TypeSpec.Builder) {
        val initializer = PropertySpec.builder(key, type)
                .initializer(key)
        classBuilder.addProperty(initializer.build()) // ensures val present by adding both
        constructor.addParameter(key, type)
    }


    private fun findJsonValueType(nvp: JsonElement): TypeName {
        return when {
            nvp.isJsonPrimitive -> findJsonPrimitiveType(nvp.asJsonPrimitive)
            nvp.isJsonArray -> findJsonArrayType(nvp.asJsonArray)
            nvp.isJsonObject -> findJsonObjectType(nvp.asJsonObject)
            nvp.isJsonNull -> Any::class.asTypeName().asNullable()
            else -> throw IllegalStateException("Expected a JSON value")
        }
    }

    private fun findJsonPrimitiveType(primitive: JsonPrimitive): TypeName {
        return when {
            primitive.isBoolean -> Boolean::class
            primitive.isNumber -> Number::class
            primitive.isString -> String::class
            else -> throw IllegalStateException("No type found for JSON primitive " + primitive)
        }.asTypeName()
    }

    private fun findJsonArrayType(jsonArray: JsonArray): TypeName {
        val arrayTypes = HashSet<TypeName>()
        var nullable = false

        for (jsonElement in jsonArray) { // TODO optimise by checking arrayTypes each iteration
            when {
                jsonElement.isJsonPrimitive -> arrayTypes.add(findJsonValueType(jsonElement.asJsonPrimitive))
                jsonElement.isJsonArray -> arrayTypes.add(findJsonArrayType(jsonElement.asJsonArray))
                jsonElement.isJsonObject -> arrayTypes.add(findJsonObjectType(jsonElement.asJsonObject))
                jsonElement.isJsonNull -> nullable = true
                else -> throw IllegalStateException("Unexpected state in array")
            }
        }

        val rawType = deduceArrayType(arrayTypes, nullable)
        return ParameterizedTypeName.get(Array<Any>::class.asClassName(), rawType)
    }

    private fun deduceArrayType(arrayTypes: HashSet<TypeName>, nullable: Boolean): TypeName {
        val typeName = if (arrayTypes.size > 1 || arrayTypes.isEmpty()) {
            Any::class.asTypeName()
        } else {
            arrayTypes.asIterable().first()
        }
        return if (nullable) typeName.asNullable() else typeName
    }

    private fun findJsonObjectType(jsonObject: JsonObject): TypeName {

        // FIXME need to replace "foo" with a classname (generated or otherwise
        processJsonObject(jsonObject, "Foo") // TODO need to de-dupe things!


        return ClassName.bestGuess("Foo").asNonNullable()

//        return Any::class.asTypeName()
        TODO("Need to handle finding object type properly, now that everything else is pseudo-working")
    }
}