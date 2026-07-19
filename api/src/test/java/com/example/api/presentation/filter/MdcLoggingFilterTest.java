package com.example.api.presentation.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayName("MdcLoggingFilterのテスト")
class MdcLoggingFilterTest {

    private final MdcLoggingFilter filter = new MdcLoggingFilter();

    @Test
    @DisplayName("ヘッダーが無い場合はtrace_idを新規生成しレスポンスヘッダーにセットする")
    void ヘッダーが無い場合はtrace_idを新規生成しレスポンスヘッダーにセットする() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        String traceId = response.getHeader("X-Trace-Id");
        assertThat(traceId).isNotBlank();
        verify(chain).doFilter(request, response);
        // 処理後はMDCがクリアされている
        assertThat(MDC.get("traceId")).isNull();
        assertThat(MDC.get("userId")).isNull();
    }

    @Test
    @DisplayName("ヘッダーにtrace_idがある場合はそれを引き継ぐ")
    void ヘッダーにtrace_idがある場合はそれを引き継ぐ() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Trace-Id", "existing-trace-id");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(response.getHeader("X-Trace-Id")).isEqualTo("existing-trace-id");
    }

    @Test
    @DisplayName("空白のtrace_idヘッダーは新規生成にフォールバックする")
    void 空白のtrace_idヘッダーは新規生成にフォールバックする() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Trace-Id", "   ");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertThat(response.getHeader("X-Trace-Id")).isNotBlank().isNotEqualTo("   ");
    }

    @Test
    @DisplayName("フィルターチェーンが例外を投げてもMDCはクリアされる")
    void フィルターチェーンが例外を投げてもMDCはクリアされる() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        doThrow(new ServletException("boom")).when(chain).doFilter(request, response);

        assertThatCode(() -> filter.doFilter(request, response, chain))
            .isInstanceOf(ServletException.class);

        assertThat(MDC.get("traceId")).isNull();
        assertThat(MDC.get("userId")).isNull();
    }

    @Test
    @DisplayName("フィルターチェーン内でMDCにtrace_idとuser_idが設定されている")
    void フィルターチェーン内でMDCにtrace_idとuser_idが設定されている() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        String[] captured = new String[2];
        FilterChain chain = (req, res) -> {
            captured[0] = MDC.get("traceId");
            captured[1] = MDC.get("userId");
        };

        filter.doFilter(request, response, chain);

        assertThat(captured[0]).isNotBlank();
        assertThat(captured[1]).isEqualTo("anonymous");
    }
}
