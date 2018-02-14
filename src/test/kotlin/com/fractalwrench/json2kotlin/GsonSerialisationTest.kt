package com.fractalwrench.json2kotlin

import com.google.gson.Gson
import com.google.gson.internal.LinkedTreeMap
import org.junit.Assert
import org.junit.Test
import java.io.File
import java.net.URLClassLoader
import java.util.ArrayList

class GsonSerialisationTest(expectedFilename: String,
                            jsonFilename: String) : JsonConverterTest(expectedFilename, jsonFilename) {

    /**
     * Ensures that a JSON file serialises as a GSON object
     */
    @Test
    override fun testJsonToKotlinConversion() {
        super.testJsonToKotlinConversion()
        val clz = Class.forName("com.fractalwrench.json2kotlin.valid.StrExample")
        val fromJson = Gson().fromJson(json, clz)
        Assert.assertTrue((fromJson is ArrayList<*> || fromJson is LinkedTreeMap<*, *>))
    }
}