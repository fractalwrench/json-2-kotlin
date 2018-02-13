package com.fractalwrench.json2kotlin

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

// TODO tidy

class JsonReader(val jsonParser: JsonParser) {

    internal fun readJsonTree(input: String, args: ConversionArgs): JsonObject {
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

    private fun nameForArrayField(sanitisedName: String) = "${sanitisedName}Array" // FIXME dupe
}