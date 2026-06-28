package com.example.api.presentation;

import com.example.api.domain.UserStatus;
import com.example.api.infrastructure.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("UserControllerの統合テスト")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        // テストデータをクリア
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("CRUD_正常系_一連の操作が成功する")
    void CRUD_正常系_一連の操作が成功する() throws Exception {
        // 1. ユーザー登録
        String createRequest = """
            {
                "idp_subject": "sso-user-uuid-0001",
                "email": "test@example.com",
                "name": "テストユーザー",
                "timezone": "Asia/Tokyo"
            }
            """;

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.idp_subject").value("sso-user-uuid-0001"))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.name").value("テストユーザー"))
            .andExpect(jsonPath("$.version").value(0))
            .andExpect(jsonPath("$.status").value(UserStatus.ENABLED.name()));

        // 2. ユーザー検索
        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].email").value("test@example.com"));

        // 3. ユーザー個別取得
        mockMvc.perform(get("/api/v1/users/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("テストユーザー"));

        // 4. ユーザー更新
        String updateRequest = """
            {
                "name": "更新後のユーザー名",
                "timezone": "America/New_York",
                "version": 0
            }
            """;

        mockMvc.perform(put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("更新後のユーザー名"))
            .andExpect(jsonPath("$.timezone").value("America/New_York"))
            .andExpect(jsonPath("$.version").value(1));

        // 5. ユーザー削除
        mockMvc.perform(delete("/api/v1/users/1"))
            .andExpect(status().isNoContent());

        // 6. 削除確認（検索結果に含まれない）
        mockMvc.perform(get("/api/v1/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("バリデーションエラー_必須項目漏れで400が返却される")
    void バリデーションエラー_必須項目漏れで400が返却される() throws Exception {
        String createRequest = """
            {
                "idp_subject": "",
                "email": "",
                "name": ""
            }
            """;

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.error").value("Bad Request"))
            .andExpect(jsonPath("$.fieldErrors", hasSize(greaterThanOrEqualTo(3))))
            .andExpect(jsonPath("$.fieldErrors[*].field", containsInAnyOrder("idpSubject", "email", "name")));
    }

    @Test
    @DisplayName("バリデーションエラー_型不一致で400が返却される")
    void バリデーションエラー_型不一致で400が返却される() throws Exception {
        String updateRequest = """
            {
                "name": "テストユーザー",
                "timezone": "Asia/Tokyo",
                "version": "abc"
            }
            """;

        mockMvc.perform(put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400))
            .andExpect(jsonPath("$.fieldErrors", hasSize(1)))
            .andExpect(jsonPath("$.fieldErrors[0].field").value("version"))
            .andExpect(jsonPath("$.fieldErrors[0].rejectedValue").value("abc"));
    }

    @Test
    @DisplayName("楽観ロックエラー_バージョン不一致で412が返却される")
    void 楽観ロックエラー_バージョン不一致で412が返却される() throws Exception {
        // 事前にユーザーを作成
        String createRequest = """
            {
                "idp_subject": "sso-user-uuid-0001",
                "email": "test@example.com",
                "name": "テストユーザー",
                "timezone": "Asia/Tokyo"
            }
            """;

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest))
            .andExpect(status().isCreated());

        // 古いバージョンで更新を試みる
        String updateRequest = """
            {
                "name": "更新後のユーザー名",
                "timezone": "America/New_York",
                "version": 999
            }
            """;

        mockMvc.perform(put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
            .andExpect(status().isPreconditionFailed())
            .andExpect(jsonPath("$.status").value(412))
            .andExpect(jsonPath("$.error").value("Precondition Failed"))
            .andExpect(jsonPath("$.globalErrors", hasSize(1)))
            .andExpect(jsonPath("$.globalErrors[0].code").value("OptimisticLockException"))
            .andExpect(jsonPath("$.globalErrors[0].message").value(containsString("更新されています")));
    }

    @Test
    @DisplayName("重複エラー_メールアドレス重複で409が返却される")
    void 重複エラー_メールアドレス重複で409が返却される() throws Exception {
        // 1人目のユーザーを作成
        String createRequest1 = """
            {
                "idp_subject": "sso-user-uuid-0001",
                "email": "test@example.com",
                "name": "テストユーザー1",
                "timezone": "Asia/Tokyo"
            }
            """;

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest1))
            .andExpect(status().isCreated());

        // 同じメールで2人目を作成
        String createRequest2 = """
            {
                "idp_subject": "sso-user-uuid-0002",
                "email": "test@example.com",
                "name": "テストユーザー2",
                "timezone": "Asia/Tokyo"
            }
            """;

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest2))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status").value(409))
            .andExpect(jsonPath("$.globalErrors", hasSize(1)))
            .andExpect(jsonPath("$.globalErrors[0].code").value("DuplicateKey"));
    }

    @Test
    @DisplayName("多言語化_日本語ヘッダーで日本語メッセージが返却される")
    void 多言語化_日本語ヘッダーで日本語メッセージが返却される() throws Exception {
        String createRequest = """
            {
                "idp_subject": "",
                "email": "test@example.com",
                "name": "テストユーザー"
            }
            """;

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "ja")
                .content(createRequest))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fieldErrors[0].message").value(containsString("必須です")));
    }

    @Test
    @DisplayName("検索_名前で部分一致検索ができる")
    void 検索_名前で部分一致検索ができる() throws Exception {
        // テストデータ作成
        createTestUser("sso-user-uuid-0001", "admin@example.com", "管理者ユーザー");
        createTestUser("sso-user-uuid-0002", "user@example.com", "一般ユーザー");

        // 名前で検索
        mockMvc.perform(get("/api/v1/users")
                .param("name", "管理者"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].name").value("管理者ユーザー"));
    }

    @Test
    @DisplayName("検索_メールで前方一致検索ができる")
    void 検索_メールで前方一致検索ができる() throws Exception {
        // テストデータ作成
        createTestUser("sso-user-uuid-0001", "admin@example.com", "管理者ユーザー");
        createTestUser("sso-user-uuid-0002", "user@example.com", "一般ユーザー");

        // メールで前方一致検索
        mockMvc.perform(get("/api/v1/users")
                .param("email", "admin"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].email").value("admin@example.com"));
    }

    @Test
    @DisplayName("存在しないユーザー取得_404が返却される")
    void 存在しないユーザー取得_404が返却される() throws Exception {
        mockMvc.perform(get("/api/v1/users/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.globalErrors", hasSize(1)))
            .andExpect(jsonPath("$.globalErrors[0].code").value("UserNotFound"));
    }

    // ヘルパーメソッド
    private void createTestUser(String idpSubject, String email, String name) throws Exception {
        String request = String.format("""
            {
                "idp_subject": "%s",
                "email": "%s",
                "name": "%s",
                "timezone": "Asia/Tokyo"
            }
            """, idpSubject, email, name);

        mockMvc.perform(post("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isCreated());
    }
}