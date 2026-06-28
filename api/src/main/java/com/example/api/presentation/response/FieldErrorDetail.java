package com.example.api.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * フィールドエラー詳細（特定項目への紐づけ用）
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldErrorDetail {

    /**
     * フィールド名
     */
    private String field;

    /**
     * 拒否された値
     */
    private Object rejectedValue;

    /**
     * エラーメッセージ
     */
    private String message;
}