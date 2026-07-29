# テーブル定義書

## `general.user_employee_links` (ユーザー従業員関連)

### 概要

外部IdPで認証されたユーザーと社内従業員を紐づける。すべての従業員がアプリユーザーを持つとは限らない。

### テーブル定義

| カラム名          | 論理名     | データ型                 | 主キー | オプション | デフォルト        | 説明                                                  |
| :---------------- | :--------- | :----------------------- | :----: | :--------- | :---------------- | :---------------------------------------------------- |
| `user_id`         | ユーザーID | BIGINT                   |   〇   | NOT NULL   |                   | ユーザーのID（users.idへの外部キー）                  |
| `employee_number` | 従業員番号 | VARCHAR(50)              |   〇   | NOT NULL   |                   | 従業員の番号（employees.employee_numberへの外部キー） |
| `version`         | バージョン | BIGINT                   |        | NOT NULL   | 0                 | 楽観ロック用バージョンカウンター                      |
| `created_by`      | 作成者     | BIGINT                   |        | NOT NULL   | 0                 | 作成者（ユーザーIDで、SQLで登録する場合は0）          |
| `created_at`      | 作成日時   | TIMESTAMP WITH TIME ZONE |        | NOT NULL   | CURRENT_TIMESTAMP | 作成日時 (UTC)                                        |
| `updated_by`      | 更新者     | BIGINT                   |        | NOT NULL   | 0                 | 更新者（ユーザーIDで、SQLで更新する場合は0）          |
| `updated_at`      | 更新日時   | TIMESTAMP WITH TIME ZONE |        | NOT NULL   | CURRENT_TIMESTAMP | 更新日時 (UTC)                                        |

### 主キー

| 主キー名                            |
| :---------------------------------- |
| pk_user_employee_links (複合主キー) |

### 外部キー

| 外部キー名           | カラム          | 参照テーブル      | 参照カラム      |
| :------------------- | :-------------- | :---------------- | :-------------- |
| fk_user_emp_user     | user_id         | general.users     | id              |
| fk_user_emp_employee | employee_number | general.employees | employee_number |

### インデックス

| インデックス名                    | カラム          |
| :-------------------------------- | :-------------- |
| idx_user_employee_user_id         | user_id         |
| idx_user_employee_employee_number | employee_number |
