package com.fractalwrench.json2kotlin.gson

import com.fractalwrench.json2kotlin.valid.GithubProjectExample
import com.fractalwrench.json2kotlin.valid.MoviesAryExample
import com.fractalwrench.json2kotlin.valid.MoviesAryExampleArray
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test

class MoviesGsonSerialisationTest : AbstractSerialisationTest() {

    override fun filename() = "MoviesAryExample"

    @Test
    override fun testGsonSerialisation() {
        val gson = Gson().fromJson(json, Array<MoviesAryExampleArray>::class.java)
        TODO("implement me")
    }

}