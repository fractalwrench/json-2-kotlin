package com.fractalwrench.json2kotlin

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

interface SourceBuildDelegate {
    fun prepareClassProperty(propertyBuilder: PropertySpec.Builder,
                             kotlinIdentifier: String,
                             jsonElement: TypedJsonElement?)

    fun prepareClass(classBuilder: TypeSpec.Builder,
                     kotlinIdentifier: String,
                     jsonElement: TypedJsonElement)
}