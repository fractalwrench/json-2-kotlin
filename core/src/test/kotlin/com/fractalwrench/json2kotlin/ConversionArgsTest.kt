package com.fractalwrench.json2kotlin

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ConversionArgsTest {

    lateinit var conversionArgs: ConversionArgs

    @Before
    fun setUp() {
        conversionArgs = ConversionArgs()
    }

    @Test
    fun testDefaults() {
        assertEquals("Example", conversionArgs.rootClassName)
    }

}

