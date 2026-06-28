package com.example.api.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザーレスポンス（単体）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    /**
     * ユーザーID
     */
    private Long id;

    /**
     * IdPユーザーID（OIDCのsubクレーム値）
     */
    private String idpSubject;

    /**
     * メールアドレス
     */
    private String email;

    /**
     * ユーザー名
     */
    private String name;

    /**
     * ユーザーの優先タイムゾーン
     */
    private String timezone;

    /**
     * バージョン（楽観ロック用）
     */
    private Long version;

    /**
     * ステータス
     */
    private String status;

    /**
     * 作成者
     */
    private Long createdBy;

    /**
     * 作成日時（UTC）
     */
    private String createdAt;

    /**
     * 更新者
     */
    private Long updatedBy;

    /**
     * 更新日時（UTC）
     */
    private String updatedAt;
}