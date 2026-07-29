# テーブル定義書

## `general.grade_costs` (等級原価条件)

### 概要

等級ごとの年度別原価・割増賃金・目標利益を管理する。等級の適用期間ごとにレコードを保持し、昇給時や年度変更時に新規登録する。

### テーブル定義

| カラム名                  | 論理名         | データ型                 | 主キー | オプション | デフォルト        | 説明                                         |
| :------------------------ | :------------- | :----------------------- | :----: | :--------- | :---------------- | :------------------------------------------- |
| `grade_code`              | 等級コード     | VARCHAR(20)              |   〇   | NOT NULL   |                   | 等級のコード（grades.codeへの外部キー）      |
| `fiscal_year`             | 年度           | INTEGER                  |   〇   | NOT NULL   |                   | 対象年度（例：2025）                         |
| `monthly_cost`            | 月間原価       | NUMERIC                  |        |            |                   | 月間の原価（円）                             |
| `hourly_overtime_premium` | 時間外割増賃金 | NUMERIC                  |        |            |                   | 時間外勤務の1時間あたり割増賃金（円）        |
| `target_monthly_profit`   | 月間目標利益   | NUMERIC                  |        |            |                   | 月間の目標利益（円）                         |
| `created_by`              | 作成者         | BIGINT                   |        | NOT NULL   | 0                 | 作成者（ユーザーIDで、SQLで登録する場合は0） |
| `created_at`              | 作成日時       | TIMESTAMP WITH TIME ZONE |        | NOT NULL   | CURRENT_TIMESTAMP | 作成日時 (UTC)                               |
| `updated_by`              | 更新者         | BIGINT                   |        | NOT NULL   | 0                 | 更新者（ユーザーIDで、SQLで更新する場合は0） |
| `updated_at`              | 更新日時       | TIMESTAMP WITH TIME ZONE |        | NOT NULL   | CURRENT_TIMESTAMP | 更新日時 (UTC)                               |

### 主キー

| 主キー名                    |
| :-------------------------- |
| pk_grade_costs (複合主キー) |

### 外部キー

| 外部キー名           | カラム     | 参照テーブル   | 参照カラム |
| :------------------- | :--------- | :------------- | :--------- |
| fk_grade_costs_grade | grade_code | general.grades | code       |

### インデックス

| インデックス名              | カラム      |
| :-------------------------- | :---------- |
| idx_grade_costs_grade_code  | grade_code  |
| idx_grade_costs_fiscal_year | fiscal_year |
