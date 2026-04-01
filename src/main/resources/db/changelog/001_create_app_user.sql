--liquibase formatted sql

--changeset billing-manager:001_create_app_user
CREATE TABLE app_user (
    id UUID PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    google_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_app_user_email ON app_user (email);
CREATE UNIQUE INDEX idx_app_user_google_id ON app_user (google_id);
