package com.example.api.presentation.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * ユーザー作成リクエスト
 */
@Data
public class CreateUserRequest {

    /**
     * IdPユーザーID（OIDCのsubクレーム値）
     */
    @NotBlank(message = "{valid.user.idpSubject.required}")
    @Size(max = 255, message = "{valid.user.idpSubject.size}")
    private String idpSubject;

    /**
     * メールアドレス
     */
    @NotBlank(message = "{valid.user.email.required}")
    @Email(message = "{valid.user.email.format}")
    @Size(max = 255, message = "{valid.user.email.size}")
    private String email;

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
    private String timezone = "Asia/Tokyo";
}