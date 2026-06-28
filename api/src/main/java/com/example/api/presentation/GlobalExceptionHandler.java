package com.example.api.presentation;

import com.example.api.usecase.UserService;
import com.example.api.usecase.UserService.OptimisticLockException;
import com.example.api.usecase.UserService.DuplicateKeyException;
import com.example.api.usecase.exception.ResourceNotFoundException;
import com.example.api.presentation.response.ErrorResponse;
import com.example.api.presentation.response.FieldErrorDetail;
import com.example.api.presentation.response.GlobalErrorDetail;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * グローバル例外ハンドラー
 * すべての例外をキャッチし、統一されたエラーレスポンス形式に変換する
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * バリデーションエラー（@Valid アノテーションによる単項目・相関チェック）
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ErrorResponse> handleBindException(BindException ex, HttpServletRequest request) {
        List<FieldErrorDetail> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(error -> FieldErrorDetail.builder()
                .field(error.getField())
                .rejectedValue(error.getRejectedValue())
                .message(getMessage(error.getDefaultMessage()))
                .build())
            .collect(Collectors.toList());

        List<GlobalErrorDetail> globalErrors = new ArrayList<>();
        
        ErrorResponse errorResponse = buildErrorResponse(
            request,
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            getMessage("error.validation.failed"),
            globalErrors,
            fieldErrors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * リクエストJSONの構文エラー、型不一致
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        List<FieldErrorDetail> fieldErrors = new ArrayList<>();
        String message = ex.getMessage();
        
        // 型不一致エラーの場合、フィールド名と拒否値を抽出
        if (message != null && message.contains("Cannot deserialize value")) {
            String fieldName = extractFieldName(message);
            String rejectedValue = extractRejectedValue(message);
            
            fieldErrors.add(FieldErrorDetail.builder()
                .field(fieldName)
                .rejectedValue(rejectedValue)
                .message(getMessage("valid.type.mismatch"))
                .build());
        }

        ErrorResponse errorResponse = buildErrorResponse(
            request,
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            getMessage("error.malformed.request"),
            new ArrayList<>(),
            fieldErrors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * パス変数の型不一致
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        List<FieldErrorDetail> fieldErrors = new ArrayList<>();
        
        if (ex.getName() != null && ex.getValue() != null) {
            fieldErrors.add(FieldErrorDetail.builder()
                .field(ex.getName())
                .rejectedValue(ex.getValue().toString())
                .message(getMessage("valid.type.mismatch"))
                .build());
        }

        ErrorResponse errorResponse = buildErrorResponse(
            request,
            HttpStatus.BAD_REQUEST.value(),
            HttpStatus.BAD_REQUEST.getReasonPhrase(),
            getMessage("error.validation.failed"),
            new ArrayList<>(),
            fieldErrors
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * リソースが見つからない場合（404）
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        
        List<GlobalErrorDetail> globalErrors = new ArrayList<>();
        globalErrors.add(GlobalErrorDetail.builder()
            .code("UserNotFound")
            .message(getMessage("error.user.notfound"))
            .build());

        ErrorResponse errorResponse = buildErrorResponse(
            request,
            HttpStatus.NOT_FOUND.value(),
            HttpStatus.NOT_FOUND.getReasonPhrase(),
            getMessage("error.resource.notfound"),
            globalErrors,
            new ArrayList<>()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    /**
     * 重複キーエラー（409 Conflict）
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateKeyException(
            DuplicateKeyException ex, HttpServletRequest request) {
        
        List<GlobalErrorDetail> globalErrors = new ArrayList<>();
        String fieldName = extractFieldNameFromMessage(ex.getMessage());
        
        globalErrors.add(GlobalErrorDetail.builder()
            .code("DuplicateKey")
            .message(getMessage("error.user.duplicate", fieldName))
            .build());

        ErrorResponse errorResponse = buildErrorResponse(
            request,
            HttpStatus.CONFLICT.value(),
            HttpStatus.CONFLICT.getReasonPhrase(),
            getMessage("error.conflict"),
            globalErrors,
            new ArrayList<>()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    /**
     * 楽観ロックエラー（412 Precondition Failed）
     */
    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ErrorResponse> handleOptimisticLockException(
            OptimisticLockException ex, HttpServletRequest request) {
        
        List<GlobalErrorDetail> globalErrors = new ArrayList<>();
        globalErrors.add(GlobalErrorDetail.builder()
            .code("OptimisticLockException")
            .message(getMessage("error.optimistic.lock"))
            .build());

        ErrorResponse errorResponse = buildErrorResponse(
            request,
            HttpStatus.PRECONDITION_FAILED.value(),
            HttpStatus.PRECONDITION_FAILED.getReasonPhrase(),
            getMessage("error.optimistic.lock.conflict"),
            globalErrors,
            new ArrayList<>()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.PRECONDITION_FAILED);
    }

    /**
     * 業務ルール違反（422 Unprocessable Entity）
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(
            IllegalStateException ex, HttpServletRequest request) {
        
        List<GlobalErrorDetail> globalErrors = new ArrayList<>();
        globalErrors.add(GlobalErrorDetail.builder()
            .code("BusinessRuleViolation")
            .message(getMessage("error.business.rule.violation"))
            .build());

        ErrorResponse errorResponse = buildErrorResponse(
            request,
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
            getMessage("error.business.rule.violation"),
            globalErrors,
            new ArrayList<>()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    /**
     * 汎用例外（500 Internal Server Error）
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        List<GlobalErrorDetail> globalErrors = new ArrayList<>();
        globalErrors.add(GlobalErrorDetail.builder()
            .code("SystemError")
            .message(getMessage("error.system"))
            .build());

        ErrorResponse errorResponse = buildErrorResponse(
            request,
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            getMessage("error.system"),
            globalErrors,
            new ArrayList<>()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * エラーレスポンスの共通ビルダー
     */
    private ErrorResponse buildErrorResponse(
            HttpServletRequest request,
            Integer status,
            String error,
            String message,
            List<GlobalErrorDetail> globalErrors,
            List<FieldErrorDetail> fieldErrors) {
        
        return ErrorResponse.builder()
            .timestamp(OffsetDateTime.now().toString())
            .status(status)
            .error(error)
            .message(message)
            .globalErrors(globalErrors)
            .fieldErrors(fieldErrors)
            .build();
    }

    /**
     * メッセージプロパティからメッセージを取得（i18n対応）
     */
    private String getMessage(String key, Object... args) {
        try {
            return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return key;
        }
    }

    /**
     * エラーメッセージからフィールド名を抽出
     */
    private String extractFieldName(String message) {
        // "Cannot deserialize value of type `java.lang.Long` from String \"abc\": not a valid `long` value"
        // のようなメッセージからフィールド名を抽出
        try {
            int start = message.indexOf("`");
            if (start != -1) {
                int end = message.indexOf("`", start + 1);
                if (end != -1) {
                    return message.substring(start + 1, end);
                }
            }
        } catch (Exception e) {
            // 抽出失敗時は汎用的な名前を返す
        }
        return "value";
    }

    /**
     * エラーメッセージから拒否された値を抽出
     */
    private String extractRejectedValue(String message) {
        // "Cannot deserialize value of type `java.lang.Long` from String \"abc\": not a valid `long` value"
        // のようなメッセージから拒否値を抽出
        try {
            int start = message.indexOf("String \"");
            if (start != -1) {
                int valueStart = start + 8;
                int end = message.indexOf("\"", valueStart);
                if (end != -1) {
                    return message.substring(valueStart, end);
                }
            }
        } catch (Exception e) {
            // 抽出失敗時はnullを返す
        }
        return null;
    }

    /**
     * 重複エラーメッセージからフィールド名を抽出
     */
    private String extractFieldNameFromMessage(String message) {
        // "idp_subject already exists: xxx" または "email already exists: xxx"
        if (message != null && message.contains("idp_subject")) {
            return "idp_subject";
        } else if (message != null && message.contains("email")) {
            return "email";
        }
        return "field";
    }
}