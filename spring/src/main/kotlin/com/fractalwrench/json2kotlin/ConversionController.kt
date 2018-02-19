package com.fractalwrench.json2kotlin

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import java.io.ByteArrayOutputStream


@Controller
class ConversionController {

    @GetMapping("/")
    fun displayConversionForm(model: Model): String {
        model.addAttribute("conversionForm", ConversionForm())
        return "conversion"
    }

    @PostMapping("/")
    fun convertToKotlin(model: Model, @ModelAttribute conversionForm: ConversionForm): String {
        val os = ByteArrayOutputStream()
        KotlinJsonConverter().convert(conversionForm.json, os, ConversionArgs())// TODO handle failure etc with redirect
        model.addAttribute("conversionForm", conversionForm)
        model.addAttribute("kotlin", String(os.toByteArray()))
        return displayConversionForm(model)
    }

}