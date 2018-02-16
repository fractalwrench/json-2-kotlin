package com.fractalwrench.json2kotlin

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ConversionController {

    @GetMapping("/conversion")
    fun greeting() = "Hello"

}