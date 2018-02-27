package com.fractalwrench.json2kotlin

import com.google.gson.annotations.SerializedName
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec

/**
 * A build delegate which alters the generated source code to include GSON annotations
 */
internal class GsonBuildDelegate : SourceBuildDelegate {

    private val regex = "%".toRegex()

    override fun prepareProperty(propertyBuilder: PropertySpec.Builder,
                                 kotlinIdentifier: String,
                                 jsonKey: String,
                                 commonElements: List<TypedJsonElement>) {
        if (kotlinIdentifier != jsonKey) {
            val serializedNameBuilder = AnnotationSpec.builder(SerializedName::class)
            serializedNameBuilder.addMember("value=\"${jsonKey.replace(regex, "%%")}\"", "")
            propertyBuilder.addAnnotation(serializedNameBuilder.build())
        }
    }

    override fun prepareClass(classBuilder: TypeSpec.Builder,
                              jsonElement: TypedJsonElement) {
    }

}