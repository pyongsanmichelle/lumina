package com.example.api.presentation;

import com.example.api.presentation.response.ErrorResponse;
import com.example.api.usecase.UserService;
import com.example.api.usecase.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("GlobalExceptionHandlerのテスト")
class GlobalExceptionHandlerTest {

    private MessageSource messageSource;
    private GlobalExceptionHandler handler;
    private final MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/users");

    @BeforeEach
    void setUp() {
        messageSource = mock(MessageSource.class);
        // メッセージキーをそのまま返すことで、キーの選択を検証できるようにする
        when(messageSource.getMessage(any(String.class), any(), any()))
            .thenAnswer(invocation -> invocation.getArgument(0));
        handler = new GlobalExceptionHandler(messageSource);
    }

    @Test
    @DisplayName("BindExceptionは400とフィールドエラーを返す")
    void BindExceptionは400とフィールドエラーを返す() {
        BindException ex = new BindException(new Object(), "createUserRequest");
        ex.addError(new FieldError("createUserRequest", "email", "bad", false, null, null, "must not be blank"));

        ResponseEntity<ErrorResponse> response = handler.handleBindException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getStatus()).isEqualTo(400);
        assertThat(body.getMessage()).isEqualTo("error.validation.failed");
        assertThat(body.getFieldErrors()).hasSize(1);
        assertThat(body.getFieldErrors().get(0).getField()).isEqualTo("email");
        assertThat(body.getFieldErrors().get(0).getRejectedValue()).isEqualTo("bad");
    }

    @Test
    @DisplayName("HttpMessageNotReadableExceptionは400を返し、フィールド名と拒否値を抽出する")
    void HttpMessageNotReadableExceptionは400を返す() {
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        Throwable cause = new RuntimeException(
            "through reference chain: com.example.api.presentation.request.UpdateUserRequest[\"version\"]");
        HttpMessageNotReadableException ex = new HttpMessageNotReadableException(
            "Cannot deserialize value of type `java.lang.Long` from String \"abc\": not a valid `long`",
            cause, inputMessage);

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadable(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        ErrorResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getMessage()).isEqualTo("error.malformed.request");
        assertThat(body.getFieldErrors()).hasSize(1);
        assertThat(body.getFieldErrors().get(0).getField()).isEqualTo("version");
        assertThat(body.getFieldErrors().get(0).getRejectedValue()).isEqualTo("abc");
    }

    @Test
    @DisplayName("HttpMessageNotReadableExceptionでcauseがない場合はfieldがvalueになる")
    void HttpMessageNotReadableExceptionでcauseがない場合はfieldがvalueになる() {
        HttpInputMessage inputMessage = mock(HttpInputMessage.class);
        HttpMessageNotReadableException ex =
            new HttpMessageNotReadableException("malformed json", inputMessage);

        ResponseEntity<ErrorResponse> response = handler.handleHttpMessageNotReadable(ex, request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFieldErrors().get(0).getField()).isEqualTo("value");
        assertThat(response.getBody().getFieldErrors().get(0).getRejectedValue()).isNull();
    }

    @Test
    @DisplayName("MethodArgumentTypeMismatchExceptionは400とフィールドエラーを返す")
    void MethodArgumentTypeMismatchExceptionは400とフィールドエラーを返す() {
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
            "abc", Long.class, "id", null, new RuntimeException("cause"));

        ResponseEntity<ErrorResponse> response =
            handler.handleMethodArgumentTypeMismatch(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFieldErrors()).hasSize(1);
        assertThat(response.getBody().getFieldErrors().get(0).getField()).isEqualTo("id");
        assertThat(response.getBody().getFieldErrors().get(0).getRejectedValue()).isEqualTo("abc");
    }

    @Test
    @DisplayName("ResourceNotFoundExceptionは404を返す")
    void ResourceNotFoundExceptionは404を返す() {
        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFoundException(
            new ResourceNotFoundException("User", 1L), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getGlobalErrors()).hasSize(1);
        assertThat(response.getBody().getGlobalErrors().get(0).getCode()).isEqualTo("UserNotFound");
    }

    @Test
    @DisplayName("DuplicateKeyExceptionは409を返し、メッセージからフィールド名を抽出する")
    void DuplicateKeyExceptionは409を返す() {
        UserService.DuplicateKeyException ex =
            new UserService.DuplicateKeyException("idp_subject already exists: xxx");

        ResponseEntity<ErrorResponse> response = handler.handleDuplicateKeyException(ex, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getGlobalErrors().get(0).getCode()).isEqualTo("DuplicateKey");
        // フィールド名がメッセージテンプレートに埋め込まれるためgetMessageが呼ばれる
        assertThat(response.getBody().getGlobalErrors().get(0).getMessage())
            .isEqualTo("error.user.duplicate");
    }

    @Test
    @DisplayName("OptimisticLockExceptionは412を返す")
    void OptimisticLockExceptionは412を返す() {
        ResponseEntity<ErrorResponse> response = handler.handleOptimisticLockException(
            new UserService.OptimisticLockException(), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.PRECONDITION_FAILED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getGlobalErrors().get(0).getCode())
            .isEqualTo("OptimisticLockException");
    }

    @Test
    @DisplayName("IllegalStateExceptionは422を返す")
    void IllegalStateExceptionは422を返す() {
        ResponseEntity<ErrorResponse> response = handler.handleIllegalStateException(
            new IllegalStateException("business rule"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getGlobalErrors().get(0).getCode())
            .isEqualTo("BusinessRuleViolation");
    }

    @Test
    @DisplayName("汎用Exceptionは500を返す")
    void 汎用Exceptionは500を返す() {
        ResponseEntity<ErrorResponse> response =
            handler.handleGenericException(new Exception("boom"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getGlobalErrors().get(0).getCode()).isEqualTo("SystemError");
    }

    @Test
    @DisplayName("メッセージ解決に失敗した場合はキーをそのまま返す")
    void メッセージ解決に失敗した場合はキーをそのまま返す() {
        MessageSource failing = mock(MessageSource.class);
        when(failing.getMessage(eq("error.system"), any(), any()))
            .thenThrow(new RuntimeException("no message"));
        GlobalExceptionHandler failingHandler = new GlobalExceptionHandler(failing);

        ResponseEntity<ErrorResponse> response =
            failingHandler.handleGenericException(new Exception("boom"), request);

        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isEqualTo("error.system");
    }
}
