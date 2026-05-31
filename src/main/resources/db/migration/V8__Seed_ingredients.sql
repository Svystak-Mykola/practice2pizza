INSERT INTO ingredients (id, name)
SELECT gen_random_uuid()::text, v.name FROM (VALUES
    ('Моцарела'), ('Томатний соус'), ('Базилік'), ('Оливкова олія'),
    ('Пармезан'), ('Горгонзола'), ('Фета'), ('Моцарела'),
    ('Салямі пепероні'), ('Моцарела'), ('Томатний соус'),
    ('Яловичина'), ('Свинина'), ('Курка'), ('Цибуля'), ('Перець болгарський'), ('Томатний соус'),
    ('Халапеньйо'), ('Кукурудза'), ('Квасоля червона'), ('Фарш яловичий'), ('Перець чилі'), ('Соус сальса'),
    ('Чоріззо'), ('Моцарела'), ('Томатний соус'), ('Орегано'),
    ('Курка гриль'), ('Соус вершковий'), ('Гриби'), ('Кукурудза'), ('Сир моцарела'),
    ('Курка'), ('Соус Цезар'), ('Помідори чері'), ('Салат Айсберг'), ('Пармезан'),
    ('Бекон'), ('Цибуля червона'), ('Соус томатний'), ('Сир моцарела'),
    ('Мисливські ковбаски'), ('Цибуля маринована'), ('Гриби'), ('Гірчичний соус'),
    ('Курка'), ('Ананас'), ('Соус вершковий'), ('Сир моцарела'),
    ('Шинка'), ('Гриби печериці'), ('Соус вершковий'), ('Сир моцарела')
) AS v(name)
ON CONFLICT (name) DO NOTHING;

INSERT INTO pizza_ingredients (pizza_id, ingredient_id)
SELECT p.id, i.id FROM pizzas p, ingredients i WHERE p.name = 'Маргарита' AND i.name IN ('Моцарела', 'Томатний соус', 'Базилік', 'Оливкова олія')
ON CONFLICT DO NOTHING;

INSERT INTO pizza_ingredients (pizza_id, ingredient_id)
SELECT p.id, i.id FROM pizzas p, ingredients i WHERE p.name = 'Чотири сири' AND i.name IN ('Пармезан', 'Горгонзола', 'Фета', 'Моцарела')
ON CONFLICT DO NOTHING;

INSERT INTO pizza_ingredients (pizza_id, ingredient_id)
SELECT p.id, i.id FROM pizzas p, ingredients i WHERE p.name = 'Пепероні' AND i.name IN ('Салямі пепероні', 'Моцарела', 'Томатний соус')
ON CONFLICT DO NOTHING;

INSERT INTO pizza_ingredients (pizza_id, ingredient_id)
SELECT p.id, i.id FROM pizzas p, ingredients i WHERE p.name = 'М''ясна' AND i.name IN ('Яловичина', 'Свинина', 'Курка', 'Цибуля', 'Перець болгарський', 'Томатний соус')
ON CONFLICT DO NOTHING;

INSERT INTO pizza_ingredients (pizza_id, ingredient_id)
SELECT p.id, i.id FROM pizzas p, ingredients i WHERE p.name = 'Мексиканська' AND i.name IN ('Халапеньйо', 'Кукурудза', 'Квасоля червона', 'Фарш яловичий', 'Перець чилі', 'Соус сальса')
ON CONFLICT DO NOTHING;

INSERT INTO pizza_ingredients (pizza_id, ingredient_id)
SELECT p.id, i.id FROM pizzas p, ingredients i WHERE p.name = 'Чоріззо' AND i.name IN ('Чоріззо', 'Моцарела', 'Томатний соус', 'Орегано')
ON CONFLICT DO NOTHING;

INSERT INTO pizza_ingredients (pizza_id, ingredient_id)
SELECT p.id, i.id FROM pizzas p, ingredients i WHERE p.name = 'Поло' AND i.name IN ('Курка гриль', 'Соус вершковий', 'Гриби', 'Кукурудза', 'Сир моцарела')
ON CONFLICT DO NOTHING;

INSERT INTO pizza_ingredients (pizza_id, ingredient_id)
SELECT p.id, i.id FROM pizzas p, ingredients i WHERE p.name = 'Цезарь' AND i.name IN ('Курка', 'Соус Цезар', 'Помідори чері', 'Салат Айсберг', 'Пармезан')
ON CONFLICT DO NOTHING;

INSERT INTO pizza_ingredients (pizza_id, ingredient_id)
SELECT p.id, i.id FROM pizzas p, ingredients i WHERE p.name = 'Аматрічіано' AND i.name IN ('Бекон', 'Цибуля червона', 'Соус томатний', 'Сир моцарела')
ON CONFLICT DO NOTHING;

INSERT INTO pizza_ingredients (pizza_id, ingredient_id)
SELECT p.id, i.id FROM pizzas p, ingredients i WHERE p.name = 'Мисливська' AND i.name IN ('Мисливські ковбаски', 'Цибуля маринована', 'Гриби', 'Гірчичний соус')
ON CONFLICT DO NOTHING;

INSERT INTO pizza_ingredients (pizza_id, ingredient_id)
SELECT p.id, i.id FROM pizzas p, ingredients i WHERE p.name = 'Гавайська' AND i.name IN ('Курка', 'Ананас', 'Соус вершковий', 'Сир моцарела')
ON CONFLICT DO NOTHING;

INSERT INTO pizza_ingredients (pizza_id, ingredient_id)
SELECT p.id, i.id FROM pizzas p, ingredients i WHERE p.name = 'Шинка та гриби' AND i.name IN ('Шинка', 'Гриби печериці', 'Соус вершковий', 'Сир моцарела')
ON CONFLICT DO NOTHING;
