-- ============================================
-- V6: positions, grade_positionsテーブル作成
-- ============================================

-- positionsテーブルの作成
CREATE TABLE general.positions (
    code VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    version BIGINT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'ENABLED',
    created_by BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 主キー制約
ALTER TABLE general.positions ADD CONSTRAINT pk_positions PRIMARY KEY (code);

-- インデックス作成（パフォーマンス最適化）
CREATE INDEX idx_positions_name ON general.positions(name);

-- grade_positionsテーブルの作成
CREATE TABLE general.grade_positions (
    grade_code VARCHAR(20) NOT NULL,
    position_code VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 複合主キー制約
ALTER TABLE general.grade_positions ADD CONSTRAINT pk_grade_positions PRIMARY KEY (grade_code, position_code);

-- 外部キー制約: grades
ALTER TABLE general.grade_positions ADD CONSTRAINT fk_grade_positions_grade 
    FOREIGN KEY (grade_code) REFERENCES general.grades(code);

-- 外部キー制約: positions
ALTER TABLE general.grade_positions ADD CONSTRAINT fk_grade_positions_position 
    FOREIGN KEY (position_code) REFERENCES general.positions(code);

-- インデックス作成（パフォーマンス最適化）
CREATE INDEX idx_grade_positions_position_code ON general.grade_positions(position_code);