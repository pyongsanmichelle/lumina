package com.example.bff.presentation.config

import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.example.bff.application.TestTarget
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class MethodLoggingAspectTest {
    @Autowired
    lateinit var target: TestTarget

    private lateinit var listAppender: ListAppender<ILoggingEvent>

    @BeforeEach
    fun setUp() {
        listAppender = ListAppender<ILoggingEvent>()
        listAppender.start()

        val logger =
            LoggerFactory.getLogger(MethodLoggingAspect::class.java) as Logger

        logger.addAppender(listAppender)
    }

    @AfterEach
    fun tearDown() {
        listAppender.stop()

        val logger =
            LoggerFactory.getLogger(MethodLoggingAspect::class.java) as Logger

        logger.detachAppender(listAppender)
    }

    @Test
    fun `アスペクトがメソッド開始・完了ログを出力すること`() {
        target.run()

        val logs = listAppender.list.map(ILoggingEvent::getFormattedMessage)

        println(logs)

        assertTrue(logs.any { it.contains("Method started:") })
        assertTrue(logs.any { it.contains("Method finished:") })
    }
}
