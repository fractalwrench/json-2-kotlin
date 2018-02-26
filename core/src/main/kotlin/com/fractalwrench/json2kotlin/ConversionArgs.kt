package com.fractalwrench.json2kotlin

/**
 * Holds arguments which are used to control the source code generation.
 *
 * For example, specifying a non-empty package name will set a package name in the generated source file.
 */
data class ConversionArgs(val rootClassName: String = "Example", val packageName: String? = null)
