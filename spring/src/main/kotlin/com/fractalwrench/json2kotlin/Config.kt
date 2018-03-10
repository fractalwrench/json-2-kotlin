package com.fractalwrench.json2kotlin

import com.bugsnag.Bugsnag
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class BugsnagConfig {

    @Bean
    fun bugsnag(): Bugsnag = Bugsnag("063150c3f3019d4f4fecc133a54cc8ee")

}