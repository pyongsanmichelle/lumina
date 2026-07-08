package com.example.api.usecase;

import com.example.api.domain.User;
import com.example.api.domain.UserRepository;
import com.example.api.domain.UserSearchCondition;
import com.example.api.domain.UserStatus;
import com.example.api.usecase.exception.ResourceNotFoundException;
import com.example.api.usecase.mapper.UserMapper;
import com.example.api.presentation.request.CreateUserRequest;
import com.example.api.presentation.request.UpdateUserRequest;
import com.example.api.presentation.response.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceのテスト")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("searchUsers_全件検索_有効なユーザーのみ取得できる")
    void searchUsers_全件検索_有効なユーザーのみ取得できる() {
        // 準備
        User user1 = createUser(1L, "sso-user-uuid-0001", "admin@example.com", "管理者", "Asia/Tokyo", 0L, UserStatus.ENABLED);
        User user2 = createUser(2L, "sso-user-uuid-0002", "user@example.com", "一般ユーザー", "Asia/Tokyo", 0L, UserStatus.ENABLED);
        
        when(userRepository.search(any(UserSearchCondition.class))).thenReturn(Arrays.asList(user1, user2));
        when(userMapper.toResponseList(any())).thenReturn(Arrays.asList(createUserResponse(1L), createUserResponse(2L)));

        // 実行
        List<UserResponse> result = userService.searchUsers(null, null);

        // 検証
        assertThat(result).hasSize(2);
        verify(userRepository).search(argThat(condition -> 
            condition.name() == null && 
            condition.email() == null && 
            condition.status() == UserStatus.ENABLED
        ));
        verify(userMapper).toResponseList(any());
    }

    @Test
    @DisplayName("searchUsers_名前で部分一致検索_該当ユーザーのみ取得できる")
    void searchUsers_名前で部分一致検索_該当ユーザーのみ取得できる() {
        // 準備
        User user = createUser(1L, "sso-user-uuid-0001", "admin@example.com", "管理者", "Asia/Tokyo", 0L, UserStatus.ENABLED);
        
        when(userRepository.search(any(UserSearchCondition.class))).thenReturn(Arrays.asList(user));
        when(userMapper.toResponseList(any())).thenReturn(Arrays.asList(createUserResponse(1L)));

        // 実行
        List<UserResponse> result = userService.searchUsers("管理者", null);

        // 検証
        assertThat(result).hasSize(1);
        verify(userRepository).search(argThat(condition -> 
            "管理者".equals(condition.name()) && 
            condition.email() == null && 
            condition.status() == UserStatus.ENABLED
        ));
    }

    @Test
    @DisplayName("searchUsers_メールで前方一致検索_該当ユーザーのみ取得できる")
    void searchUsers_メールで前方一致検索_該当ユーザーのみ取得できる() {
        // 準備
        User user = createUser(1L, "sso-user-uuid-0001", "admin@example.com", "管理者", "Asia/Tokyo", 0L, UserStatus.ENABLED);
        
        when(userRepository.search(any(UserSearchCondition.class))).thenReturn(Arrays.asList(user));
        when(userMapper.toResponseList(any())).thenReturn(Arrays.asList(createUserResponse(1L)));

        // 実行
        List<UserResponse> result = userService.searchUsers(null, "admin");

        // 検証
        assertThat(result).hasSize(1);
        verify(userRepository).search(argThat(condition -> 
            condition.name() == null && 
            "admin".equals(condition.email()) && 
            condition.status() == UserStatus.ENABLED
        ));
    }

    @Test
    @DisplayName("searchUsers_名前とメールの複合検索_該当ユーザーのみ取得できる")
    void searchUsers_名前とメールの複合検索_該当ユーザーのみ取得できる() {
        // 準備
        User user = createUser(1L, "sso-user-uuid-0001", "admin@example.com", "管理者", "Asia/Tokyo", 0L, UserStatus.ENABLED);
        
        when(userRepository.search(any(UserSearchCondition.class))).thenReturn(Arrays.asList(user));
        when(userMapper.toResponseList(any())).thenReturn(Arrays.asList(createUserResponse(1L)));

        // 実行
        List<UserResponse> result = userService.searchUsers("管理者", "admin");

        // 検証
        assertThat(result).hasSize(1);
        verify(userRepository).search(argThat(condition -> 
            "管理者".equals(condition.name()) && 
            "admin".equals(condition.email()) && 
            condition.status() == UserStatus.ENABLED
        ));
    }

    @Test
    @DisplayName("getUserById_存在するID_ユーザーを取得できる")
    void getUserById_存在するID_ユーザーを取得できる() {
        // 準備
        User user = createUser(1L, "sso-user-uuid-0001", "admin@example.com", "管理者", "Asia/Tokyo", 0L, UserStatus.ENABLED);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(createUserResponse(1L));

        // 実行
        UserResponse result = userService.getUserById(1L);

        // 検証
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(userRepository).findById(1L);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("getUserById_存在しないID_例外がスローされる")
    void getUserById_存在しないID_例外がスローされる() {
        // 準備
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // 実行・検証
        assertThatThrownBy(() -> userService.getUserById(999L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("User not found with id: 999");
        
        verify(userRepository).findById(999L);
    }

    @Test
    @DisplayName("createUser_正常登録_ユーザーが作成される")
    void createUser_正常登録_ユーザーが作成される() {
        // 準備
        CreateUserRequest request = new CreateUserRequest();
        request.setIdpSubject("sso-user-uuid-0003");
        request.setEmail("newuser@example.com");
        request.setName("新規ユーザー");
        request.setTimezone("Asia/Tokyo");

        User savedUser = createUser(3L, "sso-user-uuid-0003", "newuser@example.com", "新規ユーザー", "Asia/Tokyo", 0L, UserStatus.ENABLED);
        
        when(userRepository.existsByIdpSubject("sso-user-uuid-0003")).thenReturn(false);
        when(userRepository.existsByEmail("newuser@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toResponse(savedUser)).thenReturn(createUserResponse(3L));

        // 実行
        UserResponse result = userService.createUser(request);

        // 検証
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3L);
        verify(userRepository).existsByIdpSubject("sso-user-uuid-0003");
        verify(userRepository).existsByEmail("newuser@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("createUser_idpSubject重複_例外がスローされる")
    void createUser_idpSubject重複_例外がスローされる() {
        // 準備
        CreateUserRequest request = new CreateUserRequest();
        request.setIdpSubject("sso-user-uuid-0001");
        request.setEmail("newuser@example.com");
        request.setName("新規ユーザー");
        request.setTimezone("Asia/Tokyo");

        when(userRepository.existsByIdpSubject("sso-user-uuid-0001")).thenReturn(true);

        // 実行・検証
        assertThatThrownBy(() -> userService.createUser(request))
            .isInstanceOf(UserService.DuplicateKeyException.class)
            .hasMessageContaining("idp_subject already exists");
        
        verify(userRepository).existsByIdpSubject("sso-user-uuid-0001");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("createUser_email重複_例外がスローされる")
    void createUser_email重複_例外がスローされる() {
        // 準備
        CreateUserRequest request = new CreateUserRequest();
        request.setIdpSubject("sso-user-uuid-0003");
        request.setEmail("admin@example.com");
        request.setName("新規ユーザー");
        request.setTimezone("Asia/Tokyo");

        when(userRepository.existsByIdpSubject("sso-user-uuid-0003")).thenReturn(false);
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(true);

        // 実行・検証
        assertThatThrownBy(() -> userService.createUser(request))
            .isInstanceOf(UserService.DuplicateKeyException.class)
            .hasMessageContaining("email already exists");
        
        verify(userRepository).existsByEmail("admin@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateUser_正常更新_ユーザーが更新される")
    void updateUser_正常更新_ユーザーが更新される() {
        // 準備
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("更新後のユーザー名");
        request.setTimezone("America/New_York");
        request.setVersion(0L);

        User existingUser = createUser(1L, "sso-user-uuid-0001", "admin@example.com", "管理者", "Asia/Tokyo", 0L, UserStatus.ENABLED);
        User updatedUser = createUser(1L, "sso-user-uuid-0001", "admin@example.com", "更新後のユーザー名", "America/New_York", 1L, UserStatus.ENABLED);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(userRepository.saveAndFlush(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(createUserResponse(1L, "更新後のユーザー名", "America/New_York", 1L));

        // 実行
        UserResponse result = userService.updateUser(1L, request);

        // 検証
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("更新後のユーザー名");
        assertThat(result.getTimezone()).isEqualTo("America/New_York");
        assertThat(result.getVersion()).isEqualTo(1L);
        verify(userRepository).findById(1L);
        verify(userRepository).saveAndFlush(any(User.class));
    }

    @Test
    @DisplayName("updateUser_存在しないID_例外がスローされる")
    void updateUser_存在しないID_例外がスローされる() {
        // 準備
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("更新後のユーザー名");
        request.setTimezone("America/New_York");
        request.setVersion(0L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // 実行・検証
        assertThatThrownBy(() -> userService.updateUser(999L, request))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("User not found with id: 999");
        
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateUser_無効化されたユーザー_例外がスローされる")
    void updateUser_無効化されたユーザー_例外がスローされる() {
        // 準備
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("更新後のユーザー名");
        request.setTimezone("America/New_York");
        request.setVersion(0L);

        User disabledUser = createUser(1L, "sso-user-uuid-0001", "admin@example.com", "管理者", "Asia/Tokyo", 0L, UserStatus.DISABLED);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(disabledUser));

        // 実行・検証
        assertThatThrownBy(() -> userService.updateUser(1L, request))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("Cannot update disabled user");
        
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateUser_楽観ロックエラー_バージョン不一致で例外がスローされる")
    void updateUser_楽観ロックエラー_バージョン不一致で例外がスローされる() {
        // 準備
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("更新後のユーザー名");
        request.setTimezone("America/New_York");
        request.setVersion(0L); // クライアントが持っている古いバージョン

        User existingUser = createUser(1L, "sso-user-uuid-0001", "admin@example.com", "管理者", "Asia/Tokyo", 1L, UserStatus.ENABLED); // サーバー側はバージョン1
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        // 実行・検証
        assertThatThrownBy(() -> userService.updateUser(1L, request))
            .isInstanceOf(UserService.OptimisticLockException.class);
        
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("deleteUser_正常削除_ステータスがDELETEDに設定される")
    void deleteUser_正常削除_ステータスがDELETEDに設定される() {
        // 準備
        User user = createUser(1L, "sso-user-uuid-0001", "admin@example.com", "管理者", "Asia/Tokyo", 0L, UserStatus.ENABLED);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // 実行
        userService.deleteUser(1L);

        // 検証
        assertThat(user.getStatus()).isEqualTo(UserStatus.DELETED);
        verify(userRepository).findById(1L);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("deleteUser_存在しないID_例外がスローされる")
    void deleteUser_存在しないID_例外がスローされる() {
        // 準備
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // 実行・検証
        assertThatThrownBy(() -> userService.deleteUser(999L))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("User not found with id: 999");
        
        verify(userRepository, never()).save(any());
    }

    // ヘルパーメソッド
    private User createUser(Long id, String idpSubject, String email, String name, String timezone, Long version, UserStatus status) {
        User user = new User();
        user.setId(id);
        user.setIdpSubject(idpSubject);
        user.setEmail(email);
        user.setName(name);
        user.setTimezone(timezone);
        user.setVersion(version);
        user.setStatus(status);
        user.setCreatedBy(0L);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedBy(0L);
        user.setUpdatedAt(OffsetDateTime.now());
        return user;
    }

    private UserResponse createUserResponse(Long id) {
        return createUserResponse(id, "ユーザー" + id, "Asia/Tokyo", 0L);
    }

    private UserResponse createUserResponse(Long id, String name, String timezone, Long version) {
        UserResponse response = new UserResponse();
        response.setId(id);
        response.setIdpSubject("sso-user-uuid-" + String.format("%04d", id));
        response.setEmail("user" + id + "@example.com");
        response.setName(name);
        response.setTimezone(timezone);
        response.setVersion(version);
        response.setStatus(UserStatus.ENABLED.name());
        response.setCreatedBy(0L);
        response.setCreatedAt(OffsetDateTime.now().toString());
        response.setUpdatedBy(0L);
        response.setUpdatedAt(OffsetDateTime.now().toString());
        return response;
    }
}