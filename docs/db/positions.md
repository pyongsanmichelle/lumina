# テーブル定義書

## `general.positions` (役職マスター)

### 概要

従業員の役職を管理するマスター。等級と多対多の関連を持ち、一つの等級に複数の役職を結びつけることができる。

### テーブル定義

| カラム名      | 論理名     | データ型                 | 主キー | オプション | デフォルト        | 説明                                                    |
| :------------ | :--------- | :----------------------- | :----: | :--------- | :---------------- | :------------------------------------------------------ |
| `code`        | 役職コード | VARCHAR(50)              |   〇   | NOT NULL   |                   | 役職を一意に識別するコード                              |
| `name`        | 役職名     | VARCHAR(100)             |        | NOT NULL   |                   | 役職の名称（日本語）                                    |
| `description` | 説明       | VARCHAR(500)             |        |            |                   | 役職の説明                                              |
| `version`     | バージョン | BIGINT                   |        | NOT NULL   | 0                 | 楽観ロック用バージョンカウンター                        |
| `status`      | ステータス | VARCHAR(50)              |        | NOT NULL   | 'ENABLED'         | データの状態 ENABLED: 有効 DISABLED: 無効 DELETED: 削除 |
| `created_by`  | 作成者     | BIGINT                   |        | NOT NULL   | 0                 | 作成者（ユーザーIDで、SQLで登録する場合は0）            |
| `created_at`  | 作成日時   | TIMESTAMP WITH TIME ZONE |        | NOT NULL   | CURRENT_TIMESTAMP | 作成日時 (UTC)                                          |
| `updated_by`  | 更新者     | BIGINT                   |        | NOT NULL   | 0                 | 更新者（ユーザーIDで、SQLで更新する場合は0）            |
| `updated_at`  | 更新日時   | TIMESTAMP WITH TIME ZONE |        | NOT NULL   | CURRENT_TIMESTAMP | 更新日時 (UTC)                                          |

### 主キー

| 主キー名     |
| :----------- |
| pk_positions |

### ユニークキー

| ユニークキー名    | カラム |
| :---------------- | :----- |
| uk_positions_code | code   |

### インデックス

| インデックス名     | カラム |
| :----------------- | :----- |
| idx_positions_name | name   |

### 初期データ

開発環境では以下の検証用データを投入する。

| code            | name         | description      | status  |
| :-------------- | :----------- | :--------------- | :------ |
| LEADER          | リーダー     | チームリーダー   | ENABLED |
| MANAGER         | マネージャー | 部門マネージャー | ENABLED |
| SENIOR_ENGINEER | 上級技術者   | 上級エンジニア   | ENABLED |
