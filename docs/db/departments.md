# テーブル定義書

## `general.departments` (部署マスター)

### 概要

社内の部署（組織）を管理するマスター。階層構造（親部署）をサポートし、兼務や異動の履歴管理は employee_departments テーブルで行う。

### テーブル定義

| カラム名                 | 論理名       | データ型                 | 主キー | オプション | デフォルト        | 説明                                                    |
| :----------------------- | :----------- | :----------------------- | :----: | :--------- | :---------------- | :------------------------------------------------------ |
| `code`                   | 部署コード   | VARCHAR(50)              |   〇   | NOT NULL   |                   | 部署を一意に識別するコード                              |
| `parent_department_code` | 親部署コード | VARCHAR(50)              |        |            |                   | 親部署のコード（Nullable、階層構造）                    |
| `name`                   | 部署名       | VARCHAR(100)             |        | NOT NULL   |                   | 部署の名称                                              |
| `description`            | 説明         | VARCHAR(500)             |        |            |                   | 部署の説明                                              |
| `sort_order`             | 表示順       | INTEGER                  |        |            |                   | 一覧表示時の並び順                                      |
| `level`                  | 階層レベル   | INTEGER                  |        |            |                   | 部署の階層深さ（0起点）                                 |
| `path`                   | パス         | VARCHAR(1000)            |        |            |                   | 階層パス（例：/01/02/03/）                              |
| `version`                | バージョン   | BIGINT                   |        | NOT NULL   | 0                 | 楽観ロック用バージョンカウンター                        |
| `status`                 | ステータス   | VARCHAR(50)              |        | NOT NULL   | 'ENABLED'         | データの状態 ENABLED: 有効 DISABLED: 無効 DELETED: 削除 |
| `created_by`             | 作成者       | BIGINT                   |        | NOT NULL   | 0                 | 作成者（ユーザーIDで、SQLで登録する場合は0）            |
| `created_at`             | 作成日時     | TIMESTAMP WITH TIME ZONE |        | NOT NULL   | CURRENT_TIMESTAMP | 作成日時 (UTC)                                          |
| `updated_by`             | 更新者       | BIGINT                   |        | NOT NULL   | 0                 | 更新者（ユーザーIDで、SQLで更新する場合は0）            |
| `updated_at`             | 更新日時     | TIMESTAMP WITH TIME ZONE |        | NOT NULL   | CURRENT_TIMESTAMP | 更新日時 (UTC)                                          |

### 主キー

| 主キー名       |
| :------------- |
| pk_departments |

### 外部キー

| 外部キー名            | カラム                 | 参照テーブル        | 参照カラム |
| :-------------------- | :--------------------- | :------------------ | :--------- |
| fk_departments_parent | parent_department_code | general.departments | code       |

### ユニークキー

| ユニークキー名      | カラム |
| :------------------ | :----- |
| uk_departments_code | code   |

### インデックス

| インデックス名              | カラム                 |
| :-------------------------- | :--------------------- |
| idx_departments_parent_code | parent_department_code |
| idx_departments_sort_order  | sort_order             |

### 初期データ

開発環境では以下の検証用データを投入する。

| code | parent_department_code | name                 | description | sort_order | level | path       | status  |
| :--- | :--------------------- | :------------------- | :---------- | :--------- | :---- | :--------- | :------ |
| 01   |                        | 本社                 |             | 1          | 0     | /01/       | ENABLED |
| 02   | 01                     | 開発部               |             | 1          | 1     | /01/02/    | ENABLED |
| 03   | 02                     | フロントエンドチーム |             | 1          | 2     | /01/02/03/ | ENABLED |
| 04   | 02                     | バックエンドチーム   |             | 2          | 2     | /01/02/04/ | ENABLED |
