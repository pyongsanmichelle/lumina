# バックエンド設計・実装原則

## 1. APIサーバー（api/）の構造（クリーンアーキテクチャ）
- **com.example.api** をベースとし、以下の4層を厳格に分離する。
  - `domain/`: ビジネスルール、ドメインモデル（自己カプセル化バリデーション内包）、Repositoryインターフェース。他層への依存禁止。
  - `usecase/`: アプリケーション固有ロジック、Service（重複チェック等のビジネスバリデーションを実行）、DTO。
  - `presentation/`: Controller（`@Valid`チェック）、request/、response/、GlobalExceptionHandler。
    - **ルーティング・バージョン管理**: Controller側の `@RequestMapping` に `/api/v1` などの**バージョンやベースパスを直接記述（ハードコード）することは完全に禁止**とする。必ず `application.yml` の `server.servlet.context-path` で一元管理し、Controller側は `@RequestMapping("/users")` のようにリソース名のみを記述すること。
  - `infrastructure/`: Repository実装（Spring Data JPA等）、Entity、config/。

## 2. BFFサーバー（bff/）の構造
- **com.example.bff** をベースとし、画面へのデータ集約と認証プロキシに特化する。
  - `presentation/`: Controller（`@Valid`徹底）、request/、response/。
  - `application/`: Service（複数APIの非同期・並行呼び出しとデータ集約）。
  - `integration/`: Client（APIを叩くHTTPクライアント：RestClient/WebClient）、config/。

## 3. 実装ルール
- API（Java）側は Lombok (@Value, @Builder, @RequiredArgsConstructor) や Java 25 の record を、BFF（Kotlin）側は Kotlin の data class を積極活用する。
- 層間のデータ移送には **MapStruct** を使用し、手動の詰め替え（setter連打）は禁止とする。

### 3.1. データベース管理と開発環境データの初期化
- DBスキーマ変更は Flyway (`api/src/main/resources/db/migration/`) で管理する。
- **開発環境（devプロファイル）の初期テストデータ投入**:
  - テーブルが100個規模に増えた際の肥大化を防ぐため、Java コード内へのデータ直書きや、JSON/CSVファイルをJavaでパースして投入するロジックの作成は禁止とする。
  - テストデータはすべて **`afterMigrate.sql` に SQL（`TRUNCATE` ➕ `INSERT ... ON CONFLICT DO NOTHING`）として記述し、外出し管理**すること。
  - 本番環境（`prod`）やテスト環境（`test`）への誤投入を物理的に防ぐため、Java 側の **`AfterMigrateCallback`（`BaseFlywayCallback` 拡張クラス）でプロファイル（`dev`）の判定を行い、実行ラインを制御（ゲートキーパー化）**すること。

### 3.2. 楽観ロックとステータス（状態）管理、および型安全（Enum/定数）の原則
- **楽観ロック**: データの同時更新衝突を防ぐため、全エンティティに `version` (BIGINT) カラムを設け、JPAの `@Version` アノテーションを付与して楽観ロックを有効化すること。衝突時は `412 Precondition Failed` を返却する。
- **ドメイン固有のEnum徹底**: データの状態（`status` 等）を管理する際、"ENABLED" などの生の文字列（String）の利用は完全に禁止する。必ず型安全な **Enum** を作成すること。
  - **個別定義の原則**: ステータスが類似していても、ドメイン（テーブル）ごとに個別の Enum（例：`UserStatus`, `ProductStatus`）として分離して定義すること。システム共通の単一のStatusクラスに内包させてはならない。
  - **配置場所**: 特定のドメインに依存する Enum は、そのドメインモデルと同じパッケージ（`domain/` 直下またはドメイン個別パッケージ）に同居させること。システム横断の汎用的なEnum（国コード等）のみ、`domain/shared/` 等の共通パッケージへの集約を認める。
  - **JPAマッピング**: エンティティのフィールドには必ず `@Enumerated(EnumType.STRING)` を付与し、DB側には文字列（`"ENABLED"` 等）で保存されるように構成すること（ORDINAL（数値）型での保存は禁止）。
- **マジックストリング・マジックナンバーの排除**:
  - コード内で複数回参照される固定値や、業務上の意味を持つ定数は、ハードコーディングせず Enum または `public static final` な定数クラスに集約すること。

### 3.3. メッセージ管理・多言語化（i18n）原則
- **ハードコーディングの禁止**: バックエンドソースコード（Java）内へのメッセージ文字列のハードコーディングは完全に禁止とする。
- **messages.propertiesでの集中管理**: すべての文言（正常・異常含む）は、プロパティファイル（例: `messages_ja.properties`）にて集中管理すること。
- **命名規則の遵守**: キーは `[種類].[ドメイン/機能].[項目名（任意）].[識別子]`（種類: `info`, `error`, `valid`）の階層構造で命名すること。
- クライアント（BFF）からの `Accept-Language` リクエストヘッダーに応じて、Springのi18n機能（`LocaleContextHolder`）により適切な言語メッセージを動的に返却すること。

### 3.4. グローバル例外ハンドリング（BFF連携）
- `presentation/` 層の `GlobalExceptionHandler`（`@RestControllerAdvice`）にて例外を型安全にキャッチし、[例外・エラーハンドリング定義書(error_guideline.md)] に従った一律のJSON構造に変換すること。
- **エラーフォーマットの分類**:
  - 単項目バリデーション・型不一致（400） ➡️ `fieldErrors` 配列へ格納
  - 相関バリデーション（400） ➡️ `globalErrors` および関連する `fieldErrors` へ格納
  - ユニーク制約違反（409） ➡️ `globalErrors` へ文脈に応じたコード（`code`）とメッセージを格納
  - 業務チェック違反（422） ➡️ `globalErrors` へ文脈に応じたコード（`code`）とメッセージを格納
  - 楽観ロックエラー（412） ➡️ `globalErrors` へ `OptimisticLockException` コードとメッセージを格納
  - **データの不在（404）管理**:
    - リソースが見つからない場合、`UserNotFoundException` や `ProductNotFoundException` のような**ドメイン固有の例外クラスを個別に作成することは完全に禁止**とする。
    - 必ず汎用例外である **`ResourceNotFoundException("リソース名", id)`** をスローし、`GlobalExceptionHandler` で一元的に `404 Not Found` へ変換すること。

## 4. テストコード（JUnit 5 + Mockito + AssertJ）
- API は `src/test/java/`、BFF は `src/test/kotlin/` 配下に対称に配置。
- Domain層は純粋なユニットテスト（モックなし）。Usecase層は `@ExtendWith(MockitoExtension.class)` によるモックテスト（高速化）。
- テストメソッド名は日本語で `テスト対象の機能_状態_期待する結果` とし、`@DisplayName` を付与する。

## 5. ビルド・カバレッジ自動化ルール（JaCoCo）
- **自動実行**: `gradle build` タスク実行時に、すべてのユニットテストおよびスライステストが自動実行（`test` タスク）されること。
- **カバレッジ集約**: テスト実行後、JaCoCoプラグインを用いて自動的にカバレッジ（XML/HTML形式）を集計すること。
- **品質ゲート（境界値）**: ラインカバレッジ（Line Coverage）の最低基準を **80%** に設定し、これを下回る場合はビルド（`build`）を失敗させる設定を `build.gradle` に記述すること（初期の縦通しフェーズでは警告のみ、または一時的に50%等に下げる調整を認める）。
- **配置先**: 集計レポートは `build/reports/jacoco/test/html/index.html` に出力されるように構成すること。

## 6. OpenAPIによるAPIクライアント自動生成（BFF側）
- **手動実装の禁止**: BFF（`bff/`）からAPI（`api/`）を呼び出すためのDTO（型定義）や、RestClient/WebClientの通信コードは、手動で書いてはならない。
- **自動生成の徹底**: `api-spec.yaml` をインプットとし、Gradleの **OpenAPI Generatorプラグイン** を使用して、`bff/integration/client/` 配下に自動生成されたコードを組み込む構成にすること。

## 7. KarateによるAPI自動テスト（API/BFFのE2E）
- **配置先**: APIおよびBFFの機能テストとして、`api/src/test/java/` または独立したテスト用ディレクトリに **Karateフレームワーク**（`.feature` ファイル）を組み込むこと。
- **テストの責務**: Mockitoによるモックテスト（Usecase層）とは別に、実際にHTTPリクエストを投げて、ステータスコード、JSON構造、レスポンス値、およびデータベースの書き換え状態を検証するシナリオテストを記述する。
- **ビルドとの連動**: ローカル環境での特定のテストフェーズ、またはCI/CDパイプラインにおいて、コンテナが立ち上がった状態でKarateテストが自動実行されるようGradleタスク（`karateTest` 等）を構成すること。
