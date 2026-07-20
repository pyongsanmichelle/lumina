package com.example.bff.integration.config

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * BFFアプリケーションのカスタム設定プロパティ。
 * application.yml の app.* に対応し、環境変数から注入される。
 *
 * Spring Boot 4.x では単一コンストラクタの data class は自動的に
 * コンストラクタバインディングされるため、@ConstructorBinding は不要。
 * デフォルト値はコード内に持たず、application.yml 側の環境変数プレースホルダーで担保する。
 */
@ConfigurationProperties(prefix = "app")
data class AppProperties(
    /** ブラウザから見たフロントエンドの公開アドレス（ログイン成功後のリダイレクト先等） */
    val frontendOrigin: String,
    /** ブラウザから見たBFFの公開アドレス（Keycloak Redirect URI等） */
    val bffOrigin: String,
    /** APIサーバーのベースURL */
    val apiBaseUrl: String,
    /** KeycloakのログアウトURL */
    val keycloakLogoutUrl: String,
)
