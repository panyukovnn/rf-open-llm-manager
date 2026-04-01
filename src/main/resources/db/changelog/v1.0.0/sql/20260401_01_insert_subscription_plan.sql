INSERT INTO subscription_plan (id, name, description, monthly_token_limit, price_kopecks, active, create_user, last_update_user)
VALUES
    ('197529ec-41e7-4903-a5b7-5bd479d0ffd8', 'Free', 'Бесплатный тариф с ограниченным количеством токенов', 100000, 0, TRUE, 'SYSTEM', 'SYSTEM'),
    ('6d012530-b47a-44ae-8549-424169a0e58f', 'Standard', 'Стандартный тариф для регулярного использования', 5000000, 99000, TRUE, 'SYSTEM', 'SYSTEM'),
    ('9a30c861-fb88-4f5b-b1f7-980a653e3bc0', 'Pro', 'Профессиональный тариф с увеличенным лимитом', 25000000, 299000, TRUE, 'SYSTEM', 'SYSTEM');