package com.example.api.infrastructure;

import com.example.api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ユーザーリポジトリ
 * general.users テーブルへのデータアクセスを提供
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}