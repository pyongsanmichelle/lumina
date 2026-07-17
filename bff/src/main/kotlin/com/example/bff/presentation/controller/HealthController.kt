package com.example.bff.presentation.controller

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthController {

    @GetMapping("/health-debug", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun healthDebug() = mapOf("status" to "ok")
}