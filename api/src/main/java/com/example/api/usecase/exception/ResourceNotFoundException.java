package com.example.api.usecase.exception;

/**
 * 指定されたリソース（データ）が存在しない場合の汎用例外
 * 
 * どのリソース（User, Product等）でも使い回せる共通例外クラス。
 * リソース名と識別子を指定することで、自動的にメッセージを生成する。
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * リソース名と識別子からメッセージを自動生成
     * 
     * @param resourceName リソース名（例: "User", "Product"）
     * @param id 識別子（例: ユーザーID、商品ID）
     */
    public ResourceNotFoundException(String resourceName, Object id) {
        super(String.format("%s not found with id: %s", resourceName, id));
    }

    /**
     * カスタムメッセージを指定
     * 
     * @param message エラーメッセージ
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}