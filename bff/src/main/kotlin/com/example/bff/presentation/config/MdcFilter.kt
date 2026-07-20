package com.example.bff.presentation.config

import org.slf4j.MDC
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.UUID

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class MdcFilter : WebFilter {
    override fun filter(
        exchange: ServerWebExchange,
        chain: WebFilterChain,
    ): Mono<Void> {
        val traceId = exchange.request.headers.getFirst("X-Trace-Id") ?: UUID.randomUUID().toString()

        return Mono
            .defer {
                MDC.put("trace_id", traceId)
                MDC.put("user_id", "anonymous")
                chain.filter(exchange)
            }
            .doFinally {
                MDC.remove("trace_id")
                MDC.remove("user_id")
            }
    }
}
