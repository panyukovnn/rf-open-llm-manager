--liquibase formatted sql

--changeset billing-manager:002_create_subscription_plan
CREATE TABLE subscription_plan (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    monthly_token_limit BIGINT NOT NULL,
    price_kopecks BIGINT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_subscription_plan_name ON subscription_plan (name);
