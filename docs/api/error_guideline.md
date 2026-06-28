# 例外・エラーハンドリング定義書 (Error Handling Guideline)

本システムにおける例外、バリデーション、業務チェックの分類と、それに対応するHTTPステータスコード、およびレスポンスフォーマットの定義。

---

## 1. エラー分類とHTTPステータスコード対応表

| エラー種別 | HTTPコード | 発生契機・原因 | BFF（クライアント）の期待される振る舞い |
| :--- | :--- | :--- | :--- |
| **単項目バリデーション** | `400 Bad Request` | `@NotNull`, `@Size`, 型不一致など、単一の入力値が不正 | 対象フィールドの直下にエラーメッセージを表示する |
| **相関バリデーション** | `400 Bad Request` | 「開始日 < 終了日」など、複数項目にまたがる整合性エラー | 画面上部にアラートを表示、または関連する複数フィールドを強調 |
| **認証エラー** | `401 Unauthorized` | トークン切れ、未ログイン状態でのアクセス | ログイン画面へ強制リダイレクトする |
| **認可エラー** | `403 Forbidden` | 権限のないリソースへのアクセス（一般ユーザーが管理者APIを叩いた等） | 「権限がありません」というエラー画面・トーストを表示 |
| **リソース不在** | `404 Not Found` | 指定されたIDのデータがDBに存在しない | 404専用のマイページやエラー画面を表示 |
| **ユニーク制約違反** | `409 Conflict` | メールアドレス重複など、DBの既存データと衝突 | 対象項目（email等）に「既に登録されています」と表示 |
| **楽観ロックエラー** | `412 Precondition Failed` | `version` が他者によって既に更新されている | ダイアログを表示し、画面のリロード（再取得）を促す |
| **業務ルール違反** | `422 Unprocessable Entity` | 「無効化済みのユーザーは編集不可」など、データの状態による却下 | 画面上部に業務エラー内容（トーストやダイアログ）を表示 |
| **システムエラー** | `500 Internal Server Error` | DB接続遮断、NullPointerExceptionなどの予期せぬ例外 | 「システムエラーが発生しました。管理者にお問い合わせください」と表示 |

---

## 2. 多言語化（i18n）方針
エラーレスポンスに含まれる `message` 等の文字列は、Javaコード内へのハードコーディングを禁止し、すべて `messages.properties` から動的に取得する。
詳細な命名規則や管理方針については、[メッセージ管理・多言語化定義書 (message_guideline.md)](./message_guideline.md) を参照すること。

---

## 3. レンスポンスフォーマット定義
例に記載している `message` は日本語で例示しているが、実際は `2. 多言語化（i18n）方針` に従い `messages.properties` から動的に取得する。

### 3.1. 共通基本フォーマット (デフォルト)
異常時は一律で以下の基本フォーマットでエラー内容を返却する。入力チェックエラーや業務エラーなどの詳細情報がある場合は、globalErrors（画面全体への警告用）または fieldErrors（特定項目への紐づけ用）の配列に格納して返却する。
```json
{
  "timestamp": "2026-06-28T07:15:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "リクエストの検証または業務ルールに違反しました。"
}
```

### 3.2 パターン別レスポンスフォーマット例

#### 3.2.1. 条件付き必須チェック・相関チェック（複数フィールドにまたがる場合）
「Aの入力値が〇〇の場合、Bは必須」といった条件付き必須や、「開始日 < 終了日」のような相関チェックでは、特定の1項目だけを責められないため、globalErrors または 関連する双方のフィールド を指摘します。
* **レスポンス例 (400 Bad Request)**:
```json
{
  "timestamp": "2026-06-28T07:15:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "globalErrors": [
    {
      "code": "InvalidPeriod",
      "message": "終了日時は開始日時より後の時刻を指定してください。"
    }
  ],
  "fieldErrors": [
    { "field": "startDate", "rejectedValue": "2026-06-28T12:00:00Z", "message": "日時の整合性が不正です。" },
    { "field": "endDate", "rejectedValue": "2026-06-28T11:00:00Z", "message": "日時の整合性が不正です。" }
  ]
}
```

#### 3.2.2. 業務チェック（データの状態でエラーになる、特定項目に起因しない場合）
「ステータスが `DISABLED` のユーザーは更新できない」「既に上限数に達している」など、リクエストされた値ではなく **DB側のデータ状態に依存する業務エラー** の場合、特定のフィールドを指摘できないため `globalErrors` を使ってBFFに伝えます。
* **レスポンス例 (422 Unprocessable Entity または 400)**:
```json
{
  "timestamp": "2026-06-28T07:16:00Z",
  "status": 422,
  "error": "Unprocessable Entity",
  "message": "Business rule violation",
  "globalErrors": [
    {
      "code": "UserAlreadyDisabled",
      "message": "無効化されているユーザーに対して、この操作を行うことはできません。"
    }
  ]
}
```

#### 3.2.3. 楽観ロックエラー
クライアントが保持していたデータのバージョンと、サーバー（DB）側の最新バージョンが一致しない（同時更新衝突が発生した）場合に返却します。
* **レスポンス例 (412 Precondition Failed)**:
```json
{
  "timestamp": "2026-06-28T07:17:00Z",
  "status": 412,
  "error": "Precondition Failed",
  "message": "Optimistic lock conflict",
  "globalErrors": [
    {
      "code": "OptimisticLockException",
      "message": "対象のデータは他のユーザーによって既に更新されています。画面をリロードしてやり直してください。"
    }
  ]
}
```

#### 3.2.4. リクエストJSON自体の構文エラー、型不一致
値型（id や version）の項目にクライアントが誤って文字列（"abc"）を送った場合や、JSONの構文（カンマ漏れなど）が壊れている場合に返却します。
* **レスポンス例 (400 Bad Request)**:
```json
{
  "timestamp": "2026-06-28T07:18:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Malformed JSON request or type mismatch",
  "fieldErrors": [
    {
      "field": "version",
      "rejectedValue": "abc",
      "message": "数値を入力してください。"
    }
  ]
}
```

#### 3.2.5. 純粋な単項目バリデーションエラー（最も多発するケース）
`email` が未入力、`name` の文字数オーバーなど（`globalErrors` が空のケース）。
* **レスポンス例 (400 Bad Request)**:
```json
{
  "timestamp": "2026-06-28T07:19:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "fieldErrors": [
    {
      "field": "email",
      "rejectedValue": "",
      "message": "メールアドレスは必須入力です。"
    }
  ]
}
```
