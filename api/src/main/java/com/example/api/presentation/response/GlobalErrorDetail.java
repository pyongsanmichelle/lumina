package com.example.api.presentation.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * グローバルエラー詳細（画面全体への警告用）
 */
@Data
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