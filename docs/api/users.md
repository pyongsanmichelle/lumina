# API定義書

## ユーザー管理 API (Users API)

* **対象ベースパス**: `/users`

---

## エンドポイント定義

### 1. ユーザー一覧取得 (ユーザー検索)
* **パス**: `GET /users`
* **クエリパラメータ（任意）**: 
  * `name`: ユーザー名による部分一致検索
  * `email`: メールアドレスによる前方一致検索
* **レスポンス (200 OK)**:
 ```json
  [
    {
      "id": 1,
      "idp_subject": "sso-user-uuid-0001",
      "email": "admin@example.com",
      "name": "管理者ユーザー",
      "timezone": "Asia/Tokyo",
      "version": 1,
      "status": "ENABLED",
      "created_by": 0,
      "created_at": "2026-06-28T00:00:00Z",
      "updated_by": 0,
      "updated_at": "2026-06-28T00:00:00Z"
    }
  ]
```

### 2. ユーザー個別取得 (照会)
* **パス**: `GET /users/{id}`
* **レスポンス (200 OK)**: ユーザーオブジェクト（単体）
* **エラーレスポンス**:
  * `404 Not Found`: 指定されたIDのユーザーが存在しない場合

### 3. ユーザー登録 (登録)
* **パス**: `POST /users`
* **リクエストボディ**:
```json
{
  "idp_subject": "sso-user-uuid-0003",
  "email": "newuser@example.com",
  "name": "新規ユーザー",
  "timezone": "Asia/Tokyo"
}
```
* **レスポンス (201 Created)**: 登録されたユーザーオブジェクト（初期 version は 1 または 0 となる）
* **エラーレスポンス**:
  * `400 Bad Request`: 必須項目漏れ、またはバリデーションエラー
  * `409 Conflict`: `idp_subject` または `email` が重複している場合

### 4. ユーザー更新 (更新 - 楽観ロック適用)
* **パス**: `PUT /users/{id}`
* **リクエストボディ**:
```json
{
  "name": "更新後のユーザー名",
  "timezone": "America/New_York",
  "version": 1
}
```
※注意: `version` には、事前に `GET` で取得していた現在のバージョン値を指定すること。
* **レスポンス (200 OK)**: 更新後のユーザーオブジェクト（サーバー側で `version` がインクリメントされる）
* **エラーレスポンス**:
  * `400 Bad Request`: 必須項目漏れ、またはバリデーションエラー
  * `404 Not Found`: 指定されたIDのユーザーが存在しない場合
  * `412 Precondition Failed`: 送信された `version` がサーバー側の最新データと一致しない場合（楽観ロックエラー）

### 5. ユーザー削除 (削除)
* **パス**: `DELETE /users/{id}`
* **レスポンス (204 No Content)**:ボディなし
* **エラーレスポンス**:
  * `400 Bad Request`: 必須項目漏れ、またはバリデーションエラー

