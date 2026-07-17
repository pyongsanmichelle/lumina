package com.example.bff

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContext
import org.springframework.beans.factory.getBean // Kotlinの拡張関数 getBean<T>() を使うためのimport

@SpringBootTest(properties = [
    "KEYCLOAK_CLIENT_SECRET=dummy-secret"
])
class BffApplicationTests {

    @Autowired
    private lateinit var context: ApplicationContext

    @Test
    @DisplayName("コンテキストが正常にロードされること")
    fun contextLoads() {
        org.junit.jupiter.api.assertDoesNotThrow {
            context.getBean("securityWebFilterChain")
        }
    }
}