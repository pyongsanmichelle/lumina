-- ============================================
-- V2: 初期データ投入
-- ============================================

-- 管理者ユーザー
INSERT INTO general.users (
    id,
    idp_subject,
    email,
    name,
    timezone,
    created_by,
    created_at,
    updated_by,
    updated_at
) VALUES (
    1,
    'sso-user-uuid-0001',
    'admin@example.com',
    '管理者ユーザー',
    'Asia/Tokyo',
    0,
    CURRENT_TIMESTAMP,
    0,
    CURRENT_TIMESTAMP
);

-- 一般ユーザー
INSERT INTO general.users (
    id,
    idp_subject,
    email,
    name,
    timezone,
    created_by,
    created_at,
    updated_by,
    updated_at
) VALUES (
    2,
    'sso-user-uuid-0002',
    'user@example.com',
    '一般ユーザー',
    'Asia/Tokyo',
    0,
    CURRENT_TIMESTAMP,
    0,
    CURRENT_TIMESTAMP
);