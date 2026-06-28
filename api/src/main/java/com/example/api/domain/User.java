package com.example.api.domain;

import java.time.OffsetDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * ユーザーエンティティ
 * general.users テーブルとマッピング
 */
@Getter
@Setter
@ToString
@Entity
@Table(
    name = "users",
    schema = "general",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_users_idp_subject", columnNames = "idp_subject"),
        @UniqueConstraint(name = "uk_users_idp_email", columnNames = "email")
    }
)
public class User {

    /**
     * ユーザーID（自動採番）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * IdPユーザーID（OIDCのsubクレーム値）
     */
    @Column(name = "idp_subject", nullable = false, length = 255)
    private String idpSubject;

    /**
     * メールアドレス
     */
    @Column(name = "email", nullable = false, length = 255)
    private String email;

    /**
     * ユーザー名
     */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /**
     * ユーザーの優先タイムゾーン
     */
    @Column(name = "timezone", nullable = false, length = 50)
    private String timezone = "Asia/Tokyo";

    /**
     * バージョン（楽観ロック用）
     */
    @Version
    @Column(name = "version", nullable = false)
    private Long version = 0L;

    /**
     * ステータス（ENABLED: 有効, DISABLED: 無効, DELETED: 削除）
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private UserStatus status = UserStatus.ENABLED;

    /**
     * 作成者（ユーザーID）
     */
    @Column(name = "created_by", nullable = false)
    private Long createdBy = 0L;

    /**
     * 作成日時（UTC）
     */
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    /**
     * 更新者（ユーザーID）
     */
    @Column(name = "updated_by", nullable = false)
    private Long updatedBy = 0L;

    /**
     * 更新日時（UTC）
     */
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}