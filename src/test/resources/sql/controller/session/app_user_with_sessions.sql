TRUNCATE TABLE session;
TRUNCATE TABLE app_user;

INSERT INTO app_user(id, email, name, google_id, create_time, create_user, last_update_time, last_update_user)
VALUES ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'owner@example.com', 'Owner User', 'google-owner',
        CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test'),
       ('b2c3d4e5-f6a7-8901-bcde-f23456789012', 'stranger@example.com', 'Stranger', 'google-stranger',
        CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test');

INSERT INTO session(id, user_id, title, model, system_prompt, create_time, create_user, last_update_time, last_update_user)
VALUES ('11111111-1111-1111-1111-111111111111', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
        'owned first', 'gpt-4o', NULL, CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test'),
       ('22222222-2222-2222-2222-222222222222', 'a1b2c3d4-e5f6-7890-abcd-ef1234567890',
        'owned second', 'gpt-4o', NULL, CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test'),
       ('33333333-3333-3333-3333-333333333333', 'b2c3d4e5-f6a7-8901-bcde-f23456789012',
        'foreign chat', 'gpt-4o', NULL, CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test');
