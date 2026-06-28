package com.example.api.infrastructure;

import com.example.api.domain.User;
import com.example.api.domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ユーザーリポジトリ
 * general.users テーブルへのデータアクセスを提供
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * ステータスでユーザーを検索
     */
    List<User> findByStatus(UserStatus status);

    /**
     * 名前で部分一致検索（ステータス指定）
     */
    List<User> findByNameContainingAndStatus(String name, UserStatus status);

    /**
     * メールアドレスで前方一致検索（ステータス指定）
     */
    List<User> findByEmailStartingWithAndStatus(String email, UserStatus status);

    /**
     * 名前とメールで複合検索（ステータス指定）
     */
    List<User> findByNameContainingAndEmailStartingWithAndStatus(String name, String email, UserStatus status);

    /**
     * idp_subjectの存在チェック
     */
    boolean existsByIdpSubject(String idpSubject);

    /**
     * emailの存在チェック
     */
    boolean existsByEmail(String email);
}
