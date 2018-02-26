package com.fractalwrench.json2kotlin.gson

import com.fractalwrench.json2kotlin.ResourceFileReader
import org.junit.Before
import org.junit.Test

/**
 * Serialises a Json file using a JSON serialisation library. Relies on copy + paste for class declarations
 */
abstract class AbstractSerialisationTest {

    private val fileReader = ResourceFileReader()
    internal var json: String = ""

    abstract fun filename(): String

    @Before
    fun setUp() {
        json = fileReader.readContents("valid/realworld/${filename()}.json")
    }

    /**
     * Ensures that a JSON file serialises as a GSON object
     */
    @Test
    abstract fun testGsonSerialisation()

}