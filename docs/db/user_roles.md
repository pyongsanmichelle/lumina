# テーブル定義書

## `general.user_roles` (ユーザーロール関連)

### 概要

ユーザーとロールの関連を管理する中間テーブル。1ユーザーに複数のロールを割り当て可能。

### テーブル定義

| カラム名     | 論理名       | データ型                 | 主キー | オプション | デフォルト        | 説明                                         |
| :----------- | :----------- | :----------------------- | :----: | :--------- | :---------------- | :------------------------------------------- |
| `user_id`    | ユーザーID   | BIGINT                   |   〇   | NOT NULL   |                   | ユーザーのID（users.idへの外部キー）         |
| `role_code`  | ロールコード | VARCHAR(50)              |   〇   | NOT NULL   |                   | ロールのコード（roles.codeへの外部キー）     |
| `version`    | バージョン   | BIGINT                   |        | NOT NULL   | 0                 | 楽観ロック用バージョンカウンター             |
| `created_by` | 作成者       | BIGINT                   |        | NOT NULL   | 0                 | 作成者（ユーザーIDで、SQLで登録する場合は0） |
| `created_at` | 作成日時     | TIMESTAMP WITH TIME ZONE |        | NOT NULL   | CURRENT_TIMESTAMP | 作成日時 (UTC)                               |
| `updated_by` | 更新者       | BIGINT                   |        | NOT NULL   | 0                 | 更新者（ユーザーIDで、SQLで更新する場合は0） |
| `updated_at` | 更新日時     | TIMESTAMP WITH TIME ZONE |        | NOT NULL   | CURRENT_TIMESTAMP | 更新日時 (UTC)                               |

### 主キー

| 主キー名                   |
| :------------------------- |
| pk_user_roles (複合主キー) |

### 外部キー

| 外部キー名         | カラム    | 参照テーブル  | 参照カラム |
| :----------------- | :-------- | :------------ | :--------- |
| fk_user_roles_user | user_id   | general.users | id         |
| fk_user_roles_role | role_code | general.roles | code       |

### インデックス

| インデックス名           | カラム    |
| :----------------------- | :-------- |
| idx_user_roles_role_code | role_code |
