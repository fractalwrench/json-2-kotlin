package com.fractalwrench.json2kotlin

import com.google.gson.JsonElement

// TODO move to a symbol pool class (or equivalent)

internal fun nameForArrayField(sanitisedName: String) = "${sanitisedName}Array"

internal fun nameForObjectInArray(it: IndexedValue<JsonElement>, sanitisedName: String): String {
    return if (it.index > 0) "$sanitisedName${it.index + 1}" else sanitisedName
}


// TODO move to a symbol pool class (or equivalent)

// FIXME pattern compilation

fun String.standardiseNewline(): String {
    return this.replace("\r\n", "\n")
}

fun String.toKotlinIdentifier(): String {
    return when {
        KEYWORDS.contains(this) -> "`$this`" // escape
        else -> {
            val sanitisedOutput = this.replace("[^0-9A-Za-z_]+".toRegex(), "_")
            val regex = "^[^A-Za-z_].*".toRegex()

            when {
                regex.matches(sanitisedOutput) -> "_$sanitisedOutput"
                else -> sanitisedOutput
            }
        }
    }
}

private val KEYWORDS = listOf(
        "as",
        "as?",
        "break",
        "class",
        "continue",
        "do",
        "else",
        "false",
        "for",
        "fun",
        "if",
        "in",
        "!in",
        "interface",
        "is",
        "!is",
        "null",
        "object",
        "package",
        "return",
        "super",
        "this",
        "throw",
        "try",
        "typealias",
        "val",
        "var",
        "when",
        "while"
)