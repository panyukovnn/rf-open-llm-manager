TRUNCATE TABLE app_user;

INSERT INTO app_user(id, email, name, google_id, create_time, create_user, last_update_time, last_update_user)
VALUES ('a1b2c3d4-e5f6-7890-abcd-ef1234567890', 'test@example.com', 'Test User', 'google-test-123',
        CURRENT_TIMESTAMP, 'test', CURRENT_TIMESTAMP, 'test');
