package com.fractalwrench.json2kotlin

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Reads and serialises a JSON string to a JSONElement, using GSON.
 *
 * If the root element is an array, a wrapper object will be added to the tree.
 */
class JsonReader(private val jsonParser: JsonParser) {

    /**
     * Reads a JSON string using GSON.
     *
     * If the root value is an array, a wrapper element will be added to the tree.
     */
    internal fun readJsonTree(input: InputStream, args: ConversionArgs): JsonObject {

        BufferedReader(InputStreamReader(input)).use {
            with(jsonParser.parse(it)) {
                return when {
                    isJsonObject -> asJsonObject
                    isJsonArray -> addRootWrapper(asJsonArray, args.rootClassName)
                    else -> throw IllegalStateException("Failed to read json object")
                }
            }
        }
    }

    /**
     * Adds an object as root which wraps the array
     */
    private fun addRootWrapper(jsonArray: JsonArray, className: String): JsonObject {
        return JsonObject().apply {
            val identifier = nameForArrayField(0, "${className}Array").decapitalize()
            add(identifier, jsonArray)
        }
    }

}