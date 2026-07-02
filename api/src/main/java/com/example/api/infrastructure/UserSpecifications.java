package com.example.api.infrastructure;

import com.example.api.domain.User;
import com.example.api.domain.UserStatus;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

/**
 * ユーザー検索用Specificationファクトリー
 * 各条件を個別のSpecificationとして提供し、呼び出し側でAND結合可能にする
 */
public class UserSpecifications {

    private UserSpecifications() {
        // ユーティリティクラスとしてインスタンス化禁止
    }

    /**
     * 名前の部分一致条件
     * @param name 検索文字列（nullの場合は条件なし）
     * @return Specification
     */
    public static Specification<User> nameContains(String name) {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (name == null || name.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    /**
     * メールアドレスの前方一致条件
     * @param email 検索文字列（nullの場合は条件なし）
     * @return Specification
     */
    public static Specification<User> emailStartsWith(String email) {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (email == null || email.isEmpty()) {
                return cb.conjunction();
            }
            return cb.like(cb.lower(root.get("email")), email.toLowerCase() + "%");
        };
    }

    /**
     * ステータスの完全一致条件
     * @param status ステータス（nullの場合は条件なし）
     * @return Specification
     */
    public static Specification<User> statusEquals(UserStatus status) {
        return (Root<User> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
            if (status == null) {
                return cb.conjunction();
            }
            return cb.equal(root.get("status"), status);
        };
    }
}