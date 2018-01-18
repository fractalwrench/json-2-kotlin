package com.fractalwrench.json2kotlin

internal class ResourceFileReader {
    fun readContents(resourceName: String): String {
        val resource = ResourceFileReader::class.java.classLoader.getResource(resourceName)
        return resource?.readText() ?: ""
    }
}