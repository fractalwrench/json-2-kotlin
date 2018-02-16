package com.fractalwrench.json2kotlin

import com.google.gson.JsonElement

internal fun nameForArrayField(sanitisedName: String) = "${sanitisedName}Array"

internal fun nameForObjectInArray(it: IndexedValue<JsonElement>, sanitisedName: String): String {
    return if (it.index > 0) "$sanitisedName${it.index + 1}" else sanitisedName
}
