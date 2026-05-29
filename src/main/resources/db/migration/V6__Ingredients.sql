CREATE TABLE ingredients (
    id TEXT PRIMARY KEY,
    name TEXT UNIQUE NOT NULL
);

CREATE TABLE pizza_ingredients (
    pizza_id TEXT NOT NULL REFERENCES pizzas(id) ON DELETE CASCADE,
    ingredient_id TEXT NOT NULL REFERENCES ingredients(id) ON DELETE CASCADE,
    PRIMARY KEY (pizza_id, ingredient_id)
);

INSERT INTO ingredients (id, name)
SELECT gen_random_uuid()::text, trim(unnest(string_to_array(ingredients, ',')))
FROM pizzas
WHERE ingredients IS NOT NULL AND ingredients != ''
ON CONFLICT (name) DO NOTHING;

INSERT INTO pizza_ingredients (pizza_id, ingredient_id)
SELECT DISTINCT p.id, i.id
FROM pizzas p
CROSS JOIN LATERAL unnest(string_to_array(p.ingredients, ',')) AS ing
JOIN ingredients i ON i.name = trim(ing)
WHERE p.ingredients IS NOT NULL AND p.ingredients != '';

ALTER TABLE pizzas DROP COLUMN ingredients;
