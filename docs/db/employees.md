# テーブル定義書

## `general.employees` (従業員マスター)

### 概要

社内の従業員（従業員番号を持つ者）の基本情報を管理するマスター。すべての従業員がアプリユーザー（usersテーブル）を持つとは限らない。

### テーブル定義

| カラム名          | 論理名     | データ型                 | 主キー | オプション | デフォルト        | 説明                                                                  |
| :---------------- | :--------- | :----------------------- | :----: | :--------- | :---------------- | :-------------------------------------------------------------------- |
| `employee_number` | 従業員番号 | VARCHAR(50)              |   〇   | NOT NULL   |                   | 従業員を一意に識別する番号                                            |
| `name`            | 従業員名   | VARCHAR(100)             |        | NOT NULL   |                   | 従業員の氏名                                                          |
| `version`         | バージョン | BIGINT                   |        | NOT NULL   | 0                 | 楽観ロック用バージョンカウンター                                      |
| `status`          | ステータス | VARCHAR(50)              |        | NOT NULL   | 'ENABLED'         | データの状態 ENABLED: 有効 DISABLED: 無効 RETIRED: 退職 DELETED: 削除 |
| `created_by`      | 作成者     | BIGINT                   |        | NOT NULL   | 0                 | 作成者（ユーザーIDで、SQLで登録する場合は0）                          |
| `created_at`      | 作成日時   | TIMESTAMP WITH TIME ZONE |        | NOT NULL   | CURRENT_TIMESTAMP | 作成日時 (UTC)                                                        |
| `updated_by`      | 更新者     | BIGINT                   |        | NOT NULL   | 0                 | 更新者（ユーザーIDで、SQLで更新する場合は0）                          |
| `updated_at`      | 更新日時   | TIMESTAMP WITH TIME ZONE |        | NOT NULL   | CURRENT_TIMESTAMP | 更新日時 (UTC)                                                        |

### 主キー

| 主キー名     |
| :----------- |
| pk_employees |

### ユニークキー

| ユニークキー名               | カラム          |
| :--------------------------- | :-------------- |
| uk_employees_employee_number | employee_number |

### インデックス

| インデックス名     | カラム |
| :----------------- | :----- |
| idx_employees_name | name   |

### 初期データ

開発環境では以下の検証用データを投入する。

| employee_number | name     | version | status  |
| :-------------- | :------- | :------ | :------ |
| EMP001          | 山田太郎 | 0       | ENABLED |
| EMP002          | 鈴木花子 | 0       | ENABLED |
| EMP003          | 佐藤次郎 | 0       | RETIRED |
