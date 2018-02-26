package com.fractalwrench.json2kotlin

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

/**
 * Implementations of this interface will receive callbacks whenever a class or property is
 * about to be constructed, allowing modification of the generated source code.
 */
interface SourceBuildDelegate {

    /**
     * Provides access to a PropertyBuilder and common TypedJsonElements at the point of construction,
     * allowing for modifications before it is turned into a TypeSpec.
     */
    fun prepareProperty(propertyBuilder: PropertySpec.Builder,
                        kotlinIdentifier: String,
                        jsonKey: String,
                        commonElements: List<TypedJsonElement>)

    /**
     * Provides access to a ClassBuilder and TypedJsonElement at the point of construction,
     * allowing for modifications before it is turned into a TypeSpec.
     */
    fun prepareClass(classBuilder: TypeSpec.Builder,
                     jsonElement: TypedJsonElement)

}