--liquibase formatted sql

--changeset billing-manager:007_insert_default_plans
INSERT INTO subscription_plan (id, name, description, monthly_token_limit, price_kopecks, active)
VALUES
    ('a0000000-0000-0000-0000-000000000001', 'Free', 'Бесплатный тариф с ограниченным количеством токенов', 100000, 0, TRUE),
    ('a0000000-0000-0000-0000-000000000002', 'Standard', 'Стандартный тариф для регулярного использования', 5000000, 99000, TRUE),
    ('a0000000-0000-0000-0000-000000000003', 'Pro', 'Профессиональный тариф с увеличенным лимитом', 25000000, 299000, TRUE);
