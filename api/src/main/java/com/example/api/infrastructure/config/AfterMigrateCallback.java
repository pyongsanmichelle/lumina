package com.example.api.infrastructure.config;

import org.flywaydb.core.api.callback.Callback;
import org.flywaydb.core.api.callback.Context;
import org.flywaydb.core.api.callback.Event;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Flyway afterMigrate コールバック
 * 
 * マイグレーション完了後のイベントをフィルタリングする。
 * devプロファイル時のみ afterMigrate.sql の実行を許可する。
 * 
 * 実際のSQL実行は Flyway が afterMigrate.sql を自動的に実行する。
 */
@Component
public class AfterMigrateCallback implements Callback {

    private final Environment environment;

    public AfterMigrateCallback(Environment environment) {
        this.environment = environment;
    }

    /**
     * イベントのサポート可否を判定
     * AFTER_MIGRATE イベント かつ devプロファイル時のみ true を返す
     */
    @Override
    public boolean supports(Event event, Context context) {
        return event == Event.AFTER_MIGRATE && isDevProfile();
    }

    /**
     * コールバック名を返却（Flyway 12.x の Callback インターフェースで必須）
     */
    @Override
    public String getCallbackName() {
        return getClass().getSimpleName();
    }

    /**
     * トランザクション内でハンドリング可能か判定（Flyway 12.x で必須）
     */
    @Override
    public boolean canHandleInTransaction(Event event, Context context) {
        return supports(event, context);
    }

    /**
     * イベントハンドラー
     * SQL実行は Flyway が afterMigrate.sql を自動実行するため、ここでは何もしない
     */
    @Override
    public void handle(Event event, Context context) {
        // SQL実行は afterMigrate.sql に委譲
        // このメソッドは supports() が true を返した場合のみ呼び出される
        System.out.println("[AfterMigrateCallback] afterMigrate.sql の実行を許可しました。");
    }

    /**
     * devプロファイルが有効かどうかを判定
     */
    private boolean isDevProfile() {
        String[] activeProfiles = environment.getActiveProfiles();
        for (String profile : activeProfiles) {
            if ("dev".equals(profile)) {
                return true;
            }
        }
        return false;
    }
}