package com.example.api.usecase;

import com.example.api.domain.User;
import com.example.api.domain.UserStatus;
import com.example.api.infrastructure.UserRepository;
import com.example.api.usecase.exception.ResourceNotFoundException;
import com.example.api.usecase.mapper.UserMapper;
import com.example.api.presentation.request.CreateUserRequest;
import com.example.api.presentation.request.UpdateUserRequest;
import com.example.api.presentation.response.UserResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * ユーザー管理サービス
 * ビジネスロジックを実装
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * ユーザー一覧取得（検索）
     */
    @Transactional(readOnly = true)
    public List<UserResponse> searchUsers(String name, String email) {
        List<User> users;
        
        if (name != null && !name.isEmpty() && email != null && !email.isEmpty()) {
            // 名前とメールの両方で検索
            users = userRepository.findByNameContainingAndEmailStartingWithAndStatus(
                name, email, UserStatus.ENABLED);
        } else if (name != null && !name.isEmpty()) {
            // 名前で部分一致検索
            users = userRepository.findByNameContainingAndStatus(name, UserStatus.ENABLED);
        } else if (email != null && !email.isEmpty()) {
            // メールで前方一致検索
            users = userRepository.findByEmailStartingWithAndStatus(email, UserStatus.ENABLED);
        } else {
            // 全件取得（有効なユーザーのみ）
            users = userRepository.findByStatus(UserStatus.ENABLED);
        }
        
        return userMapper.toResponseList(users);
    }

    /**
     * ユーザー個別取得（照会）
     */
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));
        return userMapper.toResponse(user);
    }

    /**
     * ユーザー登録
     */
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // 重複チェック
        checkDuplicateIdpSubject(request.getIdpSubject());
        checkDuplicateEmail(request.getEmail());

        // ユーザー作成
        User user = new User();
        user.setIdpSubject(request.getIdpSubject());
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setTimezone(request.getTimezone());
        user.setVersion(0L);
        user.setStatus(UserStatus.ENABLED);
        user.setCreatedBy(0L);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedBy(0L);
        user.setUpdatedAt(OffsetDateTime.now());

        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    /**
     * ユーザー更新（楽観ロック適用）
     */
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        // ユーザー存在チェック
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));

        // ステータスチェック（無効化されたユーザーは更新不可）
        if (UserStatus.DISABLED.equals(user.getStatus())) {
            throw new IllegalStateException("Cannot update disabled user");
        }

        // バージョンチェック（楽観ロック）
        if (!user.getVersion().equals(request.getVersion())) {
            throw new OptimisticLockException();
        }

        // ユーザー情報更新
        user.setName(request.getName());
        user.setTimezone(request.getTimezone());
        user.setUpdatedBy(0L);
        user.setUpdatedAt(OffsetDateTime.now());
        // versionはJPAの@Versionにより自動インクリメント

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    /**
     * ユーザー削除（論理削除）
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", id));

        // 論理削除：ステータスをDELETEDに設定
        user.setStatus(UserStatus.DELETED);
        user.setUpdatedBy(0L);
        user.setUpdatedAt(OffsetDateTime.now());
        userRepository.save(user);
    }

    /**
     * idp_subjectの重複チェック
     */
    private void checkDuplicateIdpSubject(String idpSubject) {
        if (userRepository.existsByIdpSubject(idpSubject)) {
            throw new DuplicateKeyException("idp_subject already exists: " + idpSubject);
        }
    }

    /**
     * emailの重複チェック
     */
    private void checkDuplicateEmail(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateKeyException("email already exists: " + email);
        }
    }

    /**
     * 楽観ロック例外
     */
    public static class OptimisticLockException extends RuntimeException {
        public OptimisticLockException() {
            super("Optimistic lock conflict");
        }
    }

    /**
     * 重複キー例外
     */
    public static class DuplicateKeyException extends RuntimeException {
        public DuplicateKeyException(String message) {
            super(message);
        }
    }
}
