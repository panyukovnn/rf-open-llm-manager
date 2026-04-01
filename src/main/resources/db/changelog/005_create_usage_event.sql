--liquibase formatted sql

--changeset billing-manager:005_create_usage_event
CREATE TABLE usage_event (
    id UUID PRIMARY KEY,
    api_key_id UUID NOT NULL,
    app_user_id UUID NOT NULL,
    model VARCHAR(255) NOT NULL,
    prompt_tokens BIGINT NOT NULL,
    completion_tokens BIGINT NOT NULL,
    total_tokens BIGINT NOT NULL,
    cost_usd NUMERIC(18, 8) NOT NULL,
    litellm_call_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fkey_usage_event_api_key_id FOREIGN KEY (api_key_id) REFERENCES api_key (id),
    CONSTRAINT fkey_usage_event_app_user_id FOREIGN KEY (app_user_id) REFERENCES app_user (id)
);

CREATE INDEX idx_usage_event_app_user_id ON usage_event (app_user_id);
CREATE INDEX idx_usage_event_api_key_id ON usage_event (api_key_id);
CREATE INDEX idx_usage_event_created_at ON usage_event (created_at);
