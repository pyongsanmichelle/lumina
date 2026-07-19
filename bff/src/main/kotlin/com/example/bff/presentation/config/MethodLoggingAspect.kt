package com.example.bff.presentation.config

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.time.Instant

/**
 * コントローラおよびサービスのパブリックメソッドに対して、
 * 処理開始時・終了時に自動的にログを出力するAOPアスペクト.
 */
@Aspect
@Component
class MethodLoggingAspect {
    private val log = LoggerFactory.getLogger(MethodLoggingAspect::class.java)

    @Around(
        "execution(public * com.example.bff.presentation.controller..*(..)) || " +
            "execution(public * com.example.bff.application..*(..))",
    )
    fun logMethodExecution(joinPoint: ProceedingJoinPoint): Any? {
        val methodSignature = joinPoint.signature as MethodSignature
        val className = joinPoint.target.javaClass.simpleName
        val methodName = methodSignature.name
        val argTypes = methodSignature.parameterTypes.joinToString(", ") { it.simpleName }
        val traceId = currentTraceId()
        val userId = currentUserId()

        log.info(
            "Method started: {}.{}({}) trace_id={} user_id={}",
            className,
            methodName,
            argTypes,
            traceId,
            userId,
        )

        val start = Instant.now()

        return try {
            val result = joinPoint.proceed()

            if (result is Mono<*>) {
                result
                    .doOnSubscribe { log.info("Method executing: {}.{} trace_id={} user_id={}", className, methodName, traceId, userId) }
                    .doOnSuccess {
                        log.info(
                            "Method finished: {}.{} trace_id={} user_id={} duration={}ms",
                            className,
                            methodName,
                            traceId,
                            userId,
                            Duration.between(start, Instant.now()).toMillis(),
                        )
                    }.doOnError { e ->
                        log.error(
                            "Method failed: {}.{} trace_id={} user_id={} duration={}ms exception={}: {}",
                            className,
                            methodName,
                            traceId,
                            userId,
                            Duration.between(start, Instant.now()).toMillis(),
                            e::class.java.name,
                            e.message,
                            e,
                        )
                    }
            } else if (result is Flux<*>) {
                result
                    .doOnSubscribe { log.info("Method executing: {}.{} trace_id={} user_id={}", className, methodName, traceId, userId) }
                    .doOnComplete {
                        log.info(
                            "Method finished: {}.{} trace_id={} user_id={} duration={}ms",
                            className,
                            methodName,
                            traceId,
                            userId,
                            Duration.between(start, Instant.now()).toMillis(),
                        )
                    }.doOnError { e ->
                        log.error(
                            "Method failed: {}.{} trace_id={} user_id={} duration={}ms exception={}: {}",
                            className,
                            methodName,
                            traceId,
                            userId,
                            Duration.between(start, Instant.now()).toMillis(),
                            e::class.java.name,
                            e.message,
                            e,
                        )
                    }
            } else {
                log.info(
                    "Method finished: {}.{} trace_id={} user_id={} duration={}ms",
                    className,
                    methodName,
                    traceId,
                    userId,
                    Duration.between(start, Instant.now()).toMillis(),
                )
                result
            }
        } catch (e: Exception) {
            log.error(
                "Method failed: {}.{} trace_id={} user_id={} duration={}ms exception={}: {}",
                className,
                methodName,
                traceId,
                userId,
                Duration.between(start, Instant.now()).toMillis(),
                e::class.java.name,
                e.message,
                e,
            )
            throw e
        }
    }

    private fun currentTraceId(): String = MDC.get("trace_id") ?: "no-trace"

    private fun currentUserId(): String = MDC.get("user_id") ?: "anonymous"
}
