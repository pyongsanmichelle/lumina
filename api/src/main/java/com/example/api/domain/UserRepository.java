package com.example.api.domain;

import java.util.List;
import java.util.Optional;

/**
 * ユーザーリポジトリ（ドメイン層のインターフェース）
 * フレームワーク非依存の契約を定義
 */
public interface UserRepository {

    /**
     * 検索条件に基づいてユーザーを検索
     */
    List<User> search(UserSearchCondition condition);

    /**
     * IDでユーザーを取得
     */
    Optional<User> findById(Long id);

    /**
     * ユーザーを保存
     */
    <S extends User> S save(S user);

    /**
     * ユーザーを保存し、即座にDBにフラッシュする
     */
    <S extends User> S saveAndFlush(S user);

    /**
     * idp_subjectの存在チェック
     */
    boolean existsByIdpSubject(String idpSubject);

    /**
     * emailの存在チェック
     */
    boolean existsByEmail(String email);

    /**
     * 全ユーザーを削除
     */
    void deleteAll();
}
