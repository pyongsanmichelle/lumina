package com.example.api.presentation;

import com.example.api.domain.UserStatus;
import com.example.api.domain.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@DisplayName("UserControllerの統合テスト")
class UserControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // テストデータをクリアし、IDENTITY採番をリセット
        // DELETEではIDENTITYの採番がリセットされないため、TRUNCATE RESTART IDENTITYを使用する
        jdbcTemplate.execute("TRUNCATE TABLE general.users RESTART IDENTITY CASCADE");

        // MockMvcをセットアップ
        // MOCK環境ではcontext-path(/api/v1)は適用されないため、
        // Controllerの@RequestMappingに指定されたパスそのものでアクセスする
        // セキュリティフィルターを有効化し、全リクエストを検証済みJWTとして扱う
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .defaultRequest(get("/").with(jwt()))
            .alwaysDo(print())
            .build();
    }

    @Test
    @DisplayName("CRUD_正常系_一連の操作が成功する")
    void CRUD_正常系_一連の操作が成功する() throws Exception {
        // 1. ユーザー登録
        // Jacksonはデフォルトでキャメルケースを期待するため、
        // JSONキーはJavaフィールド名（キャメルケース）に合わせる
        String createRequest = """
            {
                "idpSubject": "sso-user-uuid-0001",
                "email": "test@example.com",
                "name": "テストユーザー",
                "timezone": "Asia/Tokyo"
            }
            """;

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.idpSubject").value("sso-user-uuid-0001"))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.name").value("テストユーザー"))
            .andExpect(jsonPath("$.version").value(0))
            .andExpect(jsonPath("$.status").value(UserStatus.ENABLED.name()));

        // 2. ユーザー検索
        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].email").value("test@example.com"));

        // 3. ユーザー個別取得
        mockMvc.perform(get("/users/1"))
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

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("更新後のユーザー名"))
            .andExpect(jsonPath("$.timezone").value("America/New_York"))
            .andExpect(jsonPath("$.version").value(1));

        // 5. ユーザー削除
        mockMvc.perform(delete("/users/1"))
            .andExpect(status().isNoContent());

        // 6. 削除確認（検索結果に含まれない）
        mockMvc.perform(get("/users"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("バリデーションエラー_必須項目漏れで400が返却される")
    void バリデーションエラー_必須項目漏れで400が返却される() throws Exception {
        String createRequest = """
            {
                "idpSubject": "",
                "email": "",
                "name": ""
            }
            """;

        mockMvc.perform(post("/users")
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

        mockMvc.perform(put("/users/1")
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
                "idpSubject": "sso-user-uuid-0001",
                "email": "test@example.com",
                "name": "テストユーザー",
                "timezone": "Asia/Tokyo"
            }
            """;

        mockMvc.perform(post("/users")
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

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Accept-Language", "ja")
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
                "idpSubject": "sso-user-uuid-0001",
                "email": "test@example.com",
                "name": "テストユーザー1",
                "timezone": "Asia/Tokyo"
            }
            """;

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(createRequest1))
            .andExpect(status().isCreated());

        // 同じメールで2人目を作成
        String createRequest2 = """
            {
                "idpSubject": "sso-user-uuid-0002",
                "email": "test@example.com",
                "name": "テストユーザー2",
                "timezone": "Asia/Tokyo"
            }
            """;

        mockMvc.perform(post("/users")
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
                "idpSubject": "",
                "email": "test@example.com",
                "name": "テストユーザー"
            }
            """;

        mockMvc.perform(post("/users")
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
        mockMvc.perform(get("/users")
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
        mockMvc.perform(get("/users")
                .param("email", "admin"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].email").value("admin@example.com"));
    }

    @Test
    @DisplayName("存在しないユーザー取得_404が返却される")
    void 存在しないユーザー取得_404が返却される() throws Exception {
        mockMvc.perform(get("/users/999"))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.status").value(404))
            .andExpect(jsonPath("$.globalErrors", hasSize(1)))
            .andExpect(jsonPath("$.globalErrors[0].code").value("UserNotFound"));
    }

    // ヘルパーメソッド
    private void createTestUser(String idpSubject, String email, String name) throws Exception {
        // String.format + テキストブロックはフォーマットが正しく適用されないため、
        // 直接文字列連結を使用する
        String request = "{\"idpSubject\":\"" + idpSubject
            + "\",\"email\":\"" + email
            + "\",\"name\":\"" + name
            + "\",\"timezone\":\"Asia/Tokyo\"}";

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
            .andExpect(status().isCreated());
    }
}