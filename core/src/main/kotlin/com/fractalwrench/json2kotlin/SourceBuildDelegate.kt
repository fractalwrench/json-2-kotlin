package com.fractalwrench.json2kotlin

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

// TODO docs
interface SourceBuildDelegate { // FIXME ensure names are correct, as this will be public API!
    fun prepareClassProperty(propertyBuilder: PropertySpec.Builder,
                             kotlinIdentifier: String,
                             jsonKey: String?)

    fun prepareClass(classBuilder: TypeSpec.Builder,
                     kotlinIdentifier: String,
                     jsonElement: TypedJsonElement)
}