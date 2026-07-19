package com.example.bff

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * BFF (Backend For Frontend) アプリケーションの起動クラス。
 *
 * Spring Bootアプリケーションのエントリポイント（起点）であり、
 * ここから各種設定の読み込み、コンポーネントのスキャン、
 * および組み込みサーバー（Nettyなど）の起動が行われる。
 *
 * `@SpringBootApplication` により、
 * Spring Bootの自動設定・コンポーネントスキャンを有効化する。
 * `@ConfigurationPropertiesScan` により、
 * @ConfigurationProperties を付与した設定クラスを自動スキャンする。
 * （application.yml や application.properties の設定値をKotlinのクラスにバインドするため）
 */
@SpringBootApplication
@ConfigurationPropertiesScan
class BffApplication

/**
 * プログラムの実行エントリーポイント（メイン関数）。
 * JVMから一番最初に呼び出される。
 *
 * @param args 起動時に渡されるコマンドライン引数
 */
fun main(args: Array<String>) {
    // BffApplicationクラスを基点としてSpring Bootアプリケーションを起動する
    runApplication<BffApplication>(*args)
}
