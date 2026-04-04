TRUNCATE TABLE user_subscription;
TRUNCATE TABLE app_user;
TRUNCATE TABLE subscription_plan;

INSERT INTO subscription_plan(id, name, description, monthly_token_limit, price_kopecks, active,
                              create_time, create_user, last_update_time, last_update_user)
VALUES ('197529ec-41e7-4903-a5b7-5bd479d0ffd8', 'Free', 'Бесплатный тариф', 100000, 0, TRUE,
        CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test');

INSERT INTO app_user(id, email, name, google_id, create_time, create_user, last_update_time, last_update_user)
VALUES ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'test@example.com', 'Test User', 'google-test-123',
        CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test');

INSERT INTO user_subscription(id, app_user_id, subscription_plan_id, status, tokens_used, period_start, period_end,
                              create_time, create_user, last_update_time, last_update_user)
VALUES ('a221bb7b-3f76-4e0a-a4d2-ddf74f0d1502', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
        '197529ec-41e7-4903-a5b7-5bd479d0ffd8', 'ACTIVE', 500, '2020-01-01T00:00:00', '2020-02-01T00:00:00',
        CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test'),
       ('3fdba915-40d2-499d-be46-c3ac95652e9f', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
        '197529ec-41e7-4903-a5b7-5bd479d0ffd8', 'ACTIVE', 100, '2024-01-01T00:00:00', '2099-01-01T00:00:00',
        CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test');
