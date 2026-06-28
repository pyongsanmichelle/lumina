package com.example.api.domain;

/**
 * ユーザーステータスEnum
 * データの状態を表す
 */
public enum UserStatus {
    
    /**
     * 有効
     */
    ENABLED("有効"),
    
    /**
     * 無効
     */
    DISABLED("無効"),
    
    /**
     * 削除
     */
    DELETED("削除");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}