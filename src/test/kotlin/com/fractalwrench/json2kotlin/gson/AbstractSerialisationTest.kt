package com.fractalwrench.json2kotlin.gson

import com.fractalwrench.json2kotlin.ResourceFileReader
import com.fractalwrench.json2kotlin.valid.BugsnagErrorsExample
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Before
import org.junit.Test

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