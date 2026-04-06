TRUNCATE TABLE message;
TRUNCATE TABLE api_key;
TRUNCATE TABLE user_subscription;
TRUNCATE TABLE subscription_plan;
TRUNCATE TABLE session;
TRUNCATE TABLE app_user;

INSERT INTO app_user(id, email, name, google_id, create_time, create_user, last_update_time, last_update_user)
VALUES ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'owner@example.com', 'Owner User', 'google-owner',
        CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test');

INSERT INTO subscription_plan(id, name, monthly_token_limit, price_kopecks, active, create_time, create_user, last_update_time, last_update_user)
VALUES ('00000000-0000-0000-0000-000000000001', 'Starter', 1000000, 10000, true,
        CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test');

INSERT INTO user_subscription(id, app_user_id, subscription_plan_id, status, tokens_used, period_start, period_end,
                              create_time, create_user, last_update_time, last_update_user)
VALUES ('22222222-2222-2222-2222-222222222222', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
        '00000000-0000-0000-0000-000000000001', 'ACTIVE', 0,
        CURRENT_TIMESTAMP, DATEADD('DAY', 30, CURRENT_TIMESTAMP),
        CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test');

INSERT INTO session(id, user_id, title, model, system_prompt, create_time, create_user, last_update_time, last_update_user)
VALUES ('11111111-1111-1111-1111-111111111111', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
        'test chat', 'gpt-4o', NULL, CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test');

INSERT INTO api_key(id, app_user_id, key_hash, litellm_key_id, virtual_key, name, active,
                    create_time, create_user, last_update_time, last_update_user)
VALUES ('44444444-4444-4444-4444-444444444444', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
        'testhash1234567890abcdef', 'litellm-key-id', 'sk-test-virtual-key', 'test-key', true,
        CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test');
