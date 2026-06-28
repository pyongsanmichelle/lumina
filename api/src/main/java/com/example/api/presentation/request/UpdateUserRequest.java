package com.example.api.presentation.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * ユーザー更新リクエスト
 */
@Data
public class UpdateUserRequest {

    /**
     * ユーザー名
     */
    @NotBlank(message = "{valid.user.name.required}")
    @Size(max = 100, message = "{valid.user.name.size}")
    private String name;

    /**
     * ユーザーの優先タイムゾーン
     */
    @NotBlank(message = "{valid.user.timezone.required}")
    @Size(max = 50, message = "{valid.user.timezone.size}")
    private String timezone;

    /**
     * バージョン（楽観ロック用）
     */
    @NotNull(message = "{valid.user.version.required}")
    private Long version;
}