-- ============================================
-- V5: grades, grade_costsテーブル作成
-- ============================================

-- gradesテーブルの作成
CREATE TABLE general.grades (
    code VARCHAR(20) NOT NULL,
    grade VARCHAR(10) NOT NULL,
    branch_number INTEGER NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'ENABLED',
    created_by BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 主キー制約
ALTER TABLE general.grades ADD CONSTRAINT pk_grades PRIMARY KEY (code);

-- 複合ユニークキー制約: grade + branch_number
ALTER TABLE general.grades ADD CONSTRAINT uk_grades_code_branch UNIQUE (grade, branch_number);

-- インデックス作成（パフォーマンス最適化）
CREATE INDEX idx_grades_grade ON general.grades(grade);

-- grade_costsテーブルの作成
CREATE TABLE general.grade_costs (
    grade_code VARCHAR(20) NOT NULL,
    fiscal_year INTEGER NOT NULL,
    monthly_cost NUMERIC,
    hourly_overtime_premium NUMERIC,
    target_monthly_profit NUMERIC,
    created_by BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 複合主キー制約
ALTER TABLE general.grade_costs ADD CONSTRAINT pk_grade_costs PRIMARY KEY (grade_code, fiscal_year);

-- 外部キー制約: grades
ALTER TABLE general.grade_costs ADD CONSTRAINT fk_grade_costs_grade 
    FOREIGN KEY (grade_code) REFERENCES general.grades(code);

-- インデックス作成（パフォーマンス最適化）
CREATE INDEX idx_grade_costs_grade_code ON general.grade_costs(grade_code);
CREATE INDEX idx_grade_costs_fiscal_year ON general.grade_costs(fiscal_year);