# テーブル定義書

## `general.grade_positions` (等級役職関連)

### 概要

等級と役職の関連を管理する中間テーブル。等級と役職は多対多の関係であり、一つの等級に複数の役職を結びつけることができる。

### テーブル定義

| カラム名        | 論理名     | データ型                 | 主キー | オプション | デフォルト        | 説明                                         |
| :-------------- | :--------- | :----------------------- | :----: | :--------- | :---------------- | :------------------------------------------- |
| `grade_code`    | 等級コード | VARCHAR(20)              |   〇   | NOT NULL   |                   | 等級のコード（grades.codeへの外部キー）      |
| `position_code` | 役職コード | VARCHAR(50)              |   〇   | NOT NULL   |                   | 役職のコード（positions.codeへの外部キー）   |
| `version`       | バージョン | BIGINT                   |        | NOT NULL   | 0                 | 楽観ロック用バージョンカウンター             |
| `created_by`    | 作成者     | BIGINT                   |        | NOT NULL   | 0                 | 作成者（ユーザーIDで、SQLで登録する場合は0） |
| `created_at`    | 作成日時   | TIMESTAMP WITH TIME ZONE |        | NOT NULL   | CURRENT_TIMESTAMP | 作成日時 (UTC)                               |
| `updated_by`    | 更新者     | BIGINT                   |        | NOT NULL   | 0                 | 更新者（ユーザーIDで、SQLで更新する場合は0） |
| `updated_at`    | 更新日時   | TIMESTAMP WITH TIME ZONE |        | NOT NULL   | CURRENT_TIMESTAMP | 更新日時 (UTC)                               |

### 主キー

| 主キー名                        |
| :------------------------------ |
| pk_grade_positions (複合主キー) |

### 外部キー

| 外部キー名                  | カラム        | 参照テーブル      | 参照カラム |
| :-------------------------- | :------------ | :---------------- | :--------- |
| fk_grade_positions_grade    | grade_code    | general.grades    | code       |
| fk_grade_positions_position | position_code | general.positions | code       |

### インデックス

| インデックス名                    | カラム        |
| :-------------------------------- | :------------ |
| idx_grade_positions_grade_code    | grade_code    |
| idx_grade_positions_position_code | position_code |
