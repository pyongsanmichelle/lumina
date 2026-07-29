# テーブル定義書

## `general.grades` (等級マスター)

### 概要

従業員の等級（職級）を管理するマスター。grade_code（例：C1-1、C1-2）を主キーとして使用する。

### テーブル定義

| カラム名        | 論理名     | データ型                 | 主キー | オプション | デフォルト        | 説明                                                    |
| :-------------- | :--------- | :----------------------- | :----: | :--------- | :---------------- | :------------------------------------------------------ |
| `code`          | 等級コード | VARCHAR(20)              |   〇   | NOT NULL   |                   | 等級コード（例：C1-1、C1-2）                            |
| `grade`         | 等級       | VARCHAR(10)              |        | NOT NULL   |                   | 等級の部分（例：C1）                                    |
| `branch_number` | 枝番       | INTEGER                  |        | NOT NULL   |                   | 等級内の枝番（例：1、2）                                |
| `version`       | バージョン | BIGINT                   |        | NOT NULL   | 0                 | 楽観ロック用バージョンカウンター                        |
| `status`        | ステータス | VARCHAR(50)              |        | NOT NULL   | 'ENABLED'         | データの状態 ENABLED: 有効 DISABLED: 無効 DELETED: 削除 |
| `created_by`    | 作成者     | BIGINT                   |        | NOT NULL   | 0                 | 作成者（ユーザーIDで、SQLで登録する場合は0）            |
| `created_at`    | 作成日時   | TIMESTAMP WITH TIME ZONE |        | NOT NULL   | CURRENT_TIMESTAMP | 作成日時 (UTC)                                          |
| `updated_by`    | 更新者     | BIGINT                   |        | NOT NULL   | 0                 | 更新者（ユーザーIDで、SQLで更新する場合は0）            |
| `updated_at`    | 更新日時   | TIMESTAMP WITH TIME ZONE |        | NOT NULL   | CURRENT_TIMESTAMP | 更新日時 (UTC)                                          |

### 主キー

| 主キー名  |
| :-------- |
| pk_grades |

### ユニークキー

| ユニークキー名        | カラム                |
| :-------------------- | :-------------------- |
| uk_grades_code_branch | grade + branch_number |

### インデックス

| インデックス名   | カラム |
| :--------------- | :----- |
| idx_grades_grade | grade  |

### 初期データ

開発環境では以下の検証用データを投入する。

| code | grade | branch_number | version | status  |
| :--- | :---- | :------------ | :------ | :------ |
| C1-1 | C1    | 1             | 0       | ENABLED |
| C1-2 | C1    | 2             | 0       | ENABLED |
| C2-1 | C2    | 1             | 0       | ENABLED |
