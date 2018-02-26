package com.fractalwrench.json2kotlin

import com.google.gson.*
import java.math.BigDecimal
import java.math.BigInteger

/**
 * Decorated class which wraps a JsonElement with additional information, such as its level in
 * the tree, and a sanitised kotlin identifier.
 */
class TypedJsonElement(val jsonElement: JsonElement, val jsonKey: String, val level: Int) : JsonElement() {

    val kotlinIdentifier: String = jsonKey.toKotlinIdentifier()

    override fun isJsonNull(): Boolean = jsonElement.isJsonNull

    override fun getAsJsonPrimitive(): JsonPrimitive = jsonElement.asJsonPrimitive

    override fun isJsonArray(): Boolean = jsonElement.isJsonArray

    override fun isJsonObject(): Boolean = jsonElement.isJsonObject

    override fun getAsByte(): Byte = jsonElement.asByte

    override fun getAsString(): String = jsonElement.asString

    override fun getAsLong(): Long = jsonElement.asLong

    override fun getAsFloat(): Float = jsonElement.asFloat

    override fun getAsBoolean(): Boolean = jsonElement.asBoolean

    override fun getAsBigInteger(): BigInteger = jsonElement.asBigInteger

    override fun getAsShort(): Short = jsonElement.asShort

    override fun toString(): String = jsonElement.toString()

    override fun getAsBigDecimal(): BigDecimal = jsonElement.asBigDecimal

    override fun getAsInt(): Int = jsonElement.asInt

    override fun getAsNumber(): Number = jsonElement.asNumber

    override fun isJsonPrimitive(): Boolean = jsonElement.isJsonPrimitive

    override fun getAsCharacter(): Char = jsonElement.asCharacter

    override fun getAsJsonObject(): JsonObject = jsonElement.asJsonObject

    override fun getAsDouble(): Double = jsonElement.asDouble

    override fun deepCopy(): JsonElement {
        throw UnsupportedOperationException()
    }

    override fun getAsJsonArray(): JsonArray = jsonElement.asJsonArray

    override fun getAsJsonNull(): JsonNull = jsonElement.asJsonNull
}