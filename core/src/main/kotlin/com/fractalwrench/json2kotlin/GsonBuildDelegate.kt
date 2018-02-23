package com.fractalwrench.json2kotlin

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

class GsonBuildDelegate: SourceBuildDelegate {

    override fun prepareClassProperty(propertyBuilder: PropertySpec.Builder,
                                      kotlinIdentifier: String,
                                      jsonElement: TypedJsonElement?) {
        // TODO
    }

    override fun prepareClass(classBuilder: TypeSpec.Builder,
                              kotlinIdentifier: String,
                              jsonElement: TypedJsonElement) {
        // TODO
    }

}