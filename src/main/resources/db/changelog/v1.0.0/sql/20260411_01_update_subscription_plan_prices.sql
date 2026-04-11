UPDATE subscription_plan
SET name              = 'Базовый',
    price_kopecks     = 100000,
    active            = TRUE,
    last_update_user  = 'SYSTEM'
WHERE name = 'Базовый (бесплатный)';

UPDATE subscription_plan
SET price_kopecks    = 199000,
    last_update_user = 'SYSTEM'
WHERE name = 'Средний';

UPDATE subscription_plan
SET price_kopecks    = 999000,
    last_update_user = 'SYSTEM'
WHERE name = 'Максимальный';
