package com.fractalwrench.json2kotlin

import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Test

class ResourceFileReaderTest {

    private val reader = ResourceFileReader()

    @Test
    fun testFakeFileEmpty() {
        assertEquals("", reader.readContents("fake.txt"))
    }

    @Test
    fun testRealFileRead() {
        val contents = reader.readContents("test.txt")
        Assert.assertEquals("File contents don't match", "Hello World!", contents)
    }

}