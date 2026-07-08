package com.example.api.presentation.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * リクエストごとに trace_id と user_id を MDC に設定するフィルター。
 * JSONログ出力時に trace_id, user_id フィールドとして出力される。
 */
@Component
public class MdcLoggingFilter extends OncePerRequestFilter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";
    private static final String MDC_TRACE_ID = "traceId";
    private static final String MDC_USER_ID = "userId";
    private static final String ANONYMOUS_USER = "anonymous";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // trace_id: リクエストヘッダーから取得、なければ新規生成
            String traceId = request.getHeader(TRACE_ID_HEADER);
            if (traceId == null || traceId.isBlank()) {
                traceId = UUID.randomUUID().toString();
            }
            MDC.put(MDC_TRACE_ID, traceId);

            // レスポンスヘッダーに trace_id をセット（クライアントが追跡可能に）
            response.setHeader(TRACE_ID_HEADER, traceId);

            // user_id: 認証情報から取得（現状は未認証なので anonymous 固定）
            // TODO: 認証基盤（Keycloak等）との連携が完了したら、認証コンテキストから取得する
            String userId = ANONYMOUS_USER;
            MDC.put(MDC_USER_ID, userId);

            filterChain.doFilter(request, response);
        } finally {
            // リクエスト処理後に必ず MDC をクリア（スレッド再利用のため）
            MDC.clear();
        }
    }
}