package com.example.api.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * エラーレスポンスの基本形式
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    /**
     * タイムスタンプ（UTC）
     */
    private String timestamp;

    /**
     * HTTPステータスコード
     */
    private Integer status;

    /**
     * エラー種別
     */
    private String error;

    /**
     * エラーメッセージ
     */
    private String message;

    /**
     * グローバルエラー詳細（画面全体への警告用）
     */
    private List<GlobalErrorDetail> globalErrors;

    /**
     * フィールドエラー詳細（特定項目への紐づけ用）
     */
    private List<FieldErrorDetail> fieldErrors;
}