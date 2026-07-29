-- ============================================
-- V7: user_employee_linksテーブル作成
-- ============================================

-- user_employee_linksテーブルの作成
CREATE TABLE general.user_employee_links (
    user_id BIGINT NOT NULL,
    employee_number VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 複合主キー制約
ALTER TABLE general.user_employee_links ADD CONSTRAINT pk_user_employee_links PRIMARY KEY (user_id, employee_number);

-- 外部キー制約: users
ALTER TABLE general.user_employee_links ADD CONSTRAINT fk_user_emp_user 
    FOREIGN KEY (user_id) REFERENCES general.users(id);

-- 外部キー制約: employees
ALTER TABLE general.user_employee_links ADD CONSTRAINT fk_user_emp_employee 
    FOREIGN KEY (employee_number) REFERENCES general.employees(employee_number);

-- インデックス作成（パフォーマンス最適化）
CREATE INDEX idx_user_employee_employee_number ON general.user_employee_links(employee_number);