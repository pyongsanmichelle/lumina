package com.example.api.domain;

/**
 * ユーザー検索条件
 * Specificationパターンで使用する検索条件を保持するレコード
 */
public record UserSearchCondition(
    String name,      // 名前（部分一致、nullable）
    String email,     // メールアドレス（前方一致、nullable）
    UserStatus status // ステータス（完全一致、nullable）
) {
}