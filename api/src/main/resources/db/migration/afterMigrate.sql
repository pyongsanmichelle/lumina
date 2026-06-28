-- ============================================
-- afterMigrate.sql
-- Flyway マイグレーション完了後に実行されるコールバック
-- 開発環境向けの初期テストデータを投入する
-- ============================================

-- 既存のデータをクリア（冪等性の確保）
TRUNCATE TABLE general.users RESTART IDENTITY CASCADE;

-- 管理者ユーザー
INSERT INTO general.users (
    id,
    idp_subject,
    email,
    name,
    timezone,
    version,
    status,
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
    'ENABLED',
    0,
    '2026-06-28T00:00:00Z',
    0,
    '2026-06-28T00:00:00Z'
) ON CONFLICT (id) DO NOTHING;

-- 一般ユーザー
INSERT INTO general.users (
    id,
    idp_subject,
    email,
    name,
    timezone,
    version,
    status,
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
    'ENABLED',
    0,
    '2026-06-28T00:00:00Z',
    0,
    '2026-06-28T00:00:00Z'
) ON CONFLICT (id) DO NOTHING;