package com.example.api.infrastructure;

import com.example.api.domain.User;
import com.example.api.domain.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * ユーザーJPAリポジトリ
 * Spring Data JPAの基本機能とSpecification実行を提供
 */
@Repository
public interface UserJpaRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    /**
     * idp_subjectの存在チェック
     */
    boolean existsByIdpSubject(String idpSubject);

    /**
     * emailの存在チェック
     */
    boolean existsByEmail(String email);
}