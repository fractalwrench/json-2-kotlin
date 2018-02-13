package com.fractalwrench.json2kotlin

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

class JsonReader(private val jsonParser: JsonParser) {

    internal fun readJsonTree(input: String, args: ConversionArgs): JsonObject {
        with (jsonParser.parse(input)) {
            return when {
                isJsonObject -> asJsonObject
                isJsonArray -> addRootWrapper(asJsonArray, args.rootClassName)
                else -> throw IllegalStateException("Failed to read json object")
            }
        }
    }

    /**
     * Adds an object as root which wraps the array
     */
    private fun addRootWrapper(jsonArray: JsonArray, className: String): JsonObject {
        return JsonObject().apply { add(nameForArrayField(className).decapitalize(), jsonArray) }
    }

}