--liquibase formatted sql

--changeset billing-manager:003_create_user_subscription
CREATE TABLE user_subscription (
    id UUID PRIMARY KEY,
    app_user_id UUID NOT NULL,
    subscription_plan_id UUID NOT NULL,
    status VARCHAR(50) NOT NULL,
    tokens_used BIGINT NOT NULL DEFAULT 0,
    period_start TIMESTAMP NOT NULL,
    period_end TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fkey_user_subscription_app_user_id FOREIGN KEY (app_user_id) REFERENCES app_user (id),
    CONSTRAINT fkey_user_subscription_subscription_plan_id FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plan (id)
);

CREATE INDEX idx_user_subscription_app_user_id ON user_subscription (app_user_id);
CREATE INDEX idx_user_subscription_status ON user_subscription (status);
