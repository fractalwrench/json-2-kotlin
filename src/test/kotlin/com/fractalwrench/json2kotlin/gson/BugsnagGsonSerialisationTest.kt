package com.fractalwrench.json2kotlin.gson

import com.fractalwrench.json2kotlin.valid.BugsnagErrorsExampleArray
import com.google.gson.Gson
import org.junit.Test

class BugsnagGsonSerialisationTest : AbstractSerialisationTest() {

    override fun filename() = "BugsnagErrorsExample"

    @Test
    override fun testGsonSerialisation() {
        val gson = Gson().fromJson(json, Array<BugsnagErrorsExampleArray>::class.java)
        TODO("implement me")
    }

}