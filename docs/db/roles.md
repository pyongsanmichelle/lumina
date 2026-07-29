# テーブル定義書

## `general.roles` (ロールマスター)

### 概要

システムで使用する権限ロールを管理するマスター。画面でロールの新規作成・編集が可能。日本語のロール名を登録する。

### テーブル定義

| カラム名      | 論理名       | データ型                 | 主キー | オプション | デフォルト        | 説明                                                    |
| :------------ | :----------- | :----------------------- | :----: | :--------- | :---------------- | :------------------------------------------------------ |
| `code`        | ロールコード | VARCHAR(50)              |   〇   | NOT NULL   |                   | ロールを一意に識別するコード                            |
| `name`        | ロール名     | VARCHAR(100)             |        | NOT NULL   |                   | ロールの日本語名                                        |
| `description` | 説明         | VARCHAR(500)             |        |            |                   | ロールの説明                                            |
| `version`     | バージョン   | BIGINT                   |        | NOT NULL   | 0                 | 楽観ロック用バージョンカウンター                        |
| `status`      | ステータス   | VARCHAR(50)              |        | NOT NULL   | 'ENABLED'         | データの状態 ENABLED: 有効 DISABLED: 無効 DELETED: 削除 |
| `created_by`  | 作成者       | BIGINT                   |        | NOT NULL   | 0                 | 作成者（ユーザーIDで、SQLで登録する場合は0）            |
| `created_at`  | 作成日時     | TIMESTAMP WITH TIME ZONE |        | NOT NULL   | CURRENT_TIMESTAMP | 作成日時 (UTC)                                          |
| `updated_by`  | 更新者       | BIGINT                   |        | NOT NULL   | 0                 | 更新者（ユーザーIDで、SQLで更新する場合は0）            |
| `updated_at`  | 更新日時     | TIMESTAMP WITH TIME ZONE |        | NOT NULL   | CURRENT_TIMESTAMP | 更新日時 (UTC)                                          |

### 主キー

| 主キー名 |
| :------- |
| pk_roles |

### ユニークキー

| ユニークキー名 | カラム |
| :------------- | :----- |
| uk_roles_code  | code   |

### インデックス

| インデックス名 | カラム |
| :------------- | :----- |
| idx_roles_name | name   |

### 初期データ

開発環境では以下の検証用データを投入する。

| code  | name         | description    | status  |
| :---- | :----------- | :------------- | :------ |
| ADMIN | 管理者       | システム管理者 | ENABLED |
| USER  | 一般ユーザー | 一般利用者     | ENABLED |
