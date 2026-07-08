package com.example.api.infrastructure;

import com.example.api.domain.User;
import com.example.api.domain.UserRepository;
import com.example.api.domain.UserSearchCondition;
import com.example.api.domain.UserStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@Rollback(true)
@DisplayName("UserRepositoryImplのテスト")
class UserRepositoryImplTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private jakarta.persistence.EntityManager entityManager;

    @BeforeEach
    void setUp() {
        // 開発に利用する既存データをクリア
        entityManager.createNativeQuery("TRUNCATE TABLE users").executeUpdate();
        // テストデータをクリア
        entityManager.clear();
    }

    @Test
    @DisplayName("search_条件なし_全件取得できる")
    void search_条件なし_全件取得できる() {
        // 準備
        User user1 = createUser("test-uuid-001", "test1@example.com", "テストユーザー1", "Asia/Tokyo", UserStatus.ENABLED);
        User user2 = createUser("test-uuid-002", "test2@example.com", "テストユーザー2", "Asia/Tokyo", UserStatus.ENABLED);
        User user3 = createUser("test-uuid-003", "test3@example.com", "削除ユーザー", "Asia/Tokyo", UserStatus.DELETED);

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.flush();

        // 実行
        UserSearchCondition condition = new UserSearchCondition(null, null, null);
        List<User> result = userRepository.search(condition);

        // 検証
        assertThat(result).hasSize(3);
    }

    @Test
    @DisplayName("search_nameのみ指定_部分一致検索ができる")
    void search_nameのみ指定_部分一致検索ができる() {
        // 準備
        User user1 = createUser("test-uuid-001", "test1@example.com", "管理者ユーザー", "Asia/Tokyo", UserStatus.ENABLED);
        User user2 = createUser("test-uuid-002", "test2@example.com", "一般ユーザー", "Asia/Tokyo", UserStatus.ENABLED);
        User user3 = createUser("test-uuid-003", "test3@example.com", "削除ユーザー", "Asia/Tokyo", UserStatus.DELETED);

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.flush();

        // 実行
        UserSearchCondition condition = new UserSearchCondition("管理者", null, null);
        List<User> result = userRepository.search(condition);

        // 検証
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("管理者ユーザー");
    }

    @Test
    @DisplayName("search_emailのみ指定_前方一致検索ができる")
    void search_emailのみ指定_前方一致検索ができる() {
        // 準備
        User user1 = createUser("test-uuid-001", "test1@example.com", "テストユーザー1", "Asia/Tokyo", UserStatus.ENABLED);
        User user2 = createUser("test-uuid-002", "test2@example.com", "テストユーザー2", "Asia/Tokyo", UserStatus.ENABLED);
        User user3 = createUser("test-uuid-003", "test3@example.com", "削除ユーザー", "Asia/Tokyo", UserStatus.DELETED);

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.flush();

        // 実行
        UserSearchCondition condition = new UserSearchCondition(null, "test1", null);
        List<User> result = userRepository.search(condition);
        
        // 検証
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEmail()).isEqualTo("test1@example.com");
    }

    @Test
    @DisplayName("search_status指定_完全一致検索ができる")
    void search_status指定_完全一致検索ができる() {
        // 準備
        User user1 = createUser("test-uuid-001", "test1@example.com", "テストユーザー1", "Asia/Tokyo", UserStatus.ENABLED);
        User user2 = createUser("test-uuid-002", "test2@example.com", "テストユーザー2", "Asia/Tokyo", UserStatus.ENABLED);
        User user3 = createUser("test-uuid-003", "test3@example.com", "削除ユーザー", "Asia/Tokyo", UserStatus.DELETED);

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.flush();

        // 実行
        UserSearchCondition condition = new UserSearchCondition(null, null, UserStatus.ENABLED);
        List<User> result = userRepository.search(condition);

        // 検証
        assertThat(result).hasSize(2);
        assertThat(result).extracting(User::getStatus).containsOnly(UserStatus.ENABLED);
    }

    @Test
    @DisplayName("search_全条件指定_複合検索ができる")
    void search_全条件指定_複合検索ができる() {
        // 準備
        User user1 = createUser("test-uuid-001", "test1@example.com", "管理者ユーザー", "Asia/Tokyo", UserStatus.ENABLED);
        User user2 = createUser("test-uuid-002", "test2@example.com", "一般ユーザー", "Asia/Tokyo", UserStatus.ENABLED);
        User user3 = createUser("test-uuid-003", "test3@example.com", "削除ユーザー", "Asia/Tokyo", UserStatus.DELETED);

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.persist(user3);
        entityManager.flush();

        // 実行
        UserSearchCondition condition = new UserSearchCondition("管理者", "test1", UserStatus.ENABLED);
        List<User> result = userRepository.search(condition);

        // 検証
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("管理者ユーザー");
        assertThat(result.get(0).getEmail()).isEqualTo("test1@example.com");
        assertThat(result.get(0).getStatus()).isEqualTo(UserStatus.ENABLED);
    }

    @Test
    @DisplayName("search_大文字小文字を区別しない_部分一致検索ができる")
    void search_大文字小文字を区別しない_部分一致検索ができる() {
        // 準備
        User user1 = createUser("test-uuid-001", "test1@example.com", "adminuser", "Asia/Tokyo", UserStatus.ENABLED);
        User user2 = createUser("test-uuid-002", "test2@example.com", "publicuser", "Asia/Tokyo", UserStatus.ENABLED);

        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        // 実行（大文字で検索）
        UserSearchCondition condition = new UserSearchCondition("ADMIN", null, null);
        List<User> result = userRepository.search(condition);
        
        // 検証
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("adminuser");
    }

    // ヘルパーメソッド
    private User createUser(String idpSubject, String email, String name, String timezone, UserStatus status) {
        User user = new User();
        user.setIdpSubject(idpSubject);
        user.setEmail(email);
        user.setName(name);
        user.setTimezone(timezone);
        user.setStatus(status);
        user.setVersion(0L);
        user.setCreatedBy(0L);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedBy(0L);
        user.setUpdatedAt(OffsetDateTime.now());
        return user;
    }
}