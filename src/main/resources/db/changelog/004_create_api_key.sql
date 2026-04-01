--liquibase formatted sql

--changeset billing-manager:004_create_api_key
CREATE TABLE api_key (
    id UUID PRIMARY KEY,
    app_user_id UUID NOT NULL,
    key_hash VARCHAR(255) NOT NULL,
    litellm_key_id VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    revoked_at TIMESTAMP,
    CONSTRAINT fkey_api_key_app_user_id FOREIGN KEY (app_user_id) REFERENCES app_user (id)
);

CREATE INDEX idx_api_key_app_user_id ON api_key (app_user_id);
CREATE UNIQUE INDEX idx_api_key_key_hash ON api_key (key_hash);
