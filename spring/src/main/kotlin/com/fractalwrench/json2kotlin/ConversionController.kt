package com.fractalwrench.json2kotlin

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpRequest
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping


@Controller
class ConversionController {

    companion object {
        val formKey = "conversionForm"
    }

    @Autowired
    lateinit var conversionService: KotlinConversionService

    @GetMapping("/")
    fun displayConversionForm(model: Model): String {
        if (!model.containsAttribute(formKey)) {
            model.addAttribute(formKey, ConversionForm())
        }
        return "conversion"
    }

    @PostMapping("/")
    fun convertToKotlin(model: Model, @ModelAttribute conversionForm: ConversionForm): String {
        val os = conversionService.convert(conversionForm.json)
        model.addAttribute(formKey, conversionForm)
        model.addAttribute("kotlin", String(os.toByteArray()))
        return displayConversionForm(model)
    }

}