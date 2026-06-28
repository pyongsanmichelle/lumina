package com.example.api.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * グローバルエラー詳細（画面全体への警告用）
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GlobalErrorDetail {

    /**
     * エラーコード
     */
    private String code;

    /**
     * エラーメッセージ
     */
    private String message;
}