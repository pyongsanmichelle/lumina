package com.example.api.infrastructure;

import com.example.api.domain.User;
import com.example.api.domain.UserRepository;
import com.example.api.domain.UserSearchCondition;
import com.example.api.domain.UserStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * ユーザーリポジトリ実装
 * domain層のインターフェースをinfrastructure層で実装
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

    /**
     * 検索条件に基づいてユーザーを検索
     */
    @Override
    public List<User> search(UserSearchCondition condition) {
        Specification<User> spec = Specification.where(UserSpecifications.nameContains(condition.name()))
            .and(UserSpecifications.emailStartsWith(condition.email()))
            .and(UserSpecifications.statusEquals(condition.status()));

        return userJpaRepository.findAll(spec);
    }

    /**
     * IDでユーザーを取得
     */
    @Override
    public Optional<User> findById(Long id) {
        return userJpaRepository.findById(id);
    }

    /**
     * ユーザーを保存
     */
    @Override
    public <S extends User> S save(S user) {
        return userJpaRepository.save(user);
    }

    /**
     * ユーザーを保存し、即座にDBにフラッシュする
     */
    @Override
    public <S extends User> S saveAndFlush(S user) {
        return userJpaRepository.saveAndFlush(user);
    }

    /**
     * idp_subjectの存在チェック
     */
    @Override
    public boolean existsByIdpSubject(String idpSubject) {
        return userJpaRepository.existsByIdpSubject(idpSubject);
    }

    /**
     * emailの存在チェック
     */
    @Override
    public boolean existsByEmail(String email) {
        return userJpaRepository.existsByEmail(email);
    }

    /**
     * 全ユーザーを削除
     */
    @Override
    public void deleteAll() {
        userJpaRepository.deleteAll();
    }
}
