-- ============================================
-- V4: roles, user_rolesテーブル作成
-- ============================================

-- rolesテーブルの作成
CREATE TABLE general.roles (
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
ALTER TABLE general.roles ADD CONSTRAINT pk_roles PRIMARY KEY (code);

-- インデックス作成（パフォーマンス最適化）
CREATE INDEX idx_roles_name ON general.roles(name);

-- user_rolesテーブルの作成
CREATE TABLE general.user_roles (
    user_id BIGINT NOT NULL,
    role_code VARCHAR(50) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_by BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 複合主キー制約
ALTER TABLE general.user_roles ADD CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_code);

-- 外部キー制約: users
ALTER TABLE general.user_roles ADD CONSTRAINT fk_user_roles_user 
    FOREIGN KEY (user_id) REFERENCES general.users(id);

-- 外部キー制約: roles
ALTER TABLE general.user_roles ADD CONSTRAINT fk_user_roles_role 
    FOREIGN KEY (role_code) REFERENCES general.roles(code);

-- インデックス作成（パフォーマンス最適化）
CREATE INDEX idx_user_roles_role_code ON general.user_roles(role_code);