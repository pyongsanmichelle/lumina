-- ============================================
-- V2: departmentsテーブル作成
-- ============================================

-- departmentsテーブルの作成
CREATE TABLE general.departments (
    code VARCHAR(50) NOT NULL,
    parent_department_code VARCHAR(50),
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    sort_order INTEGER,
    level INTEGER,
    path VARCHAR(1000),
    version BIGINT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'ENABLED',
    created_by BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 主キー制約
ALTER TABLE general.departments ADD CONSTRAINT pk_departments PRIMARY KEY (code);

-- 外部キー制約: 自己参照（階層構造）
ALTER TABLE general.departments ADD CONSTRAINT fk_departments_parent 
    FOREIGN KEY (parent_department_code) REFERENCES general.departments(code);

-- インデックス作成（パフォーマンス最適化）
CREATE INDEX idx_departments_parent_code ON general.departments(parent_department_code);
CREATE INDEX idx_departments_sort_order ON general.departments(sort_order);