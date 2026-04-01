--liquibase formatted sql

--changeset billing-manager:006_create_payment
CREATE TABLE payment (
    id UUID PRIMARY KEY,
    app_user_id UUID NOT NULL,
    subscription_plan_id UUID NOT NULL,
    yookassa_payment_id VARCHAR(255),
    amount_kopecks BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fkey_payment_app_user_id FOREIGN KEY (app_user_id) REFERENCES app_user (id),
    CONSTRAINT fkey_payment_subscription_plan_id FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plan (id)
);

CREATE INDEX idx_payment_app_user_id ON payment (app_user_id);
CREATE INDEX idx_payment_yookassa_payment_id ON payment (yookassa_payment_id);
CREATE INDEX idx_payment_status ON payment (status);
