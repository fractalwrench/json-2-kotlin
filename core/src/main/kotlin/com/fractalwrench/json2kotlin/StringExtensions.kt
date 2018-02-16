package com.fractalwrench.json2kotlin


fun String.standardiseNewline(): String {
    return this.replace("\r\n", "\n")
}

fun String.toKotlinIdentifier(): String {
    return when {
        KEYWORDS.contains(this) -> "`$this`" // escape
        else -> this.replace("[^0-9A-Za-z_]+".toRegex(), "_")
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