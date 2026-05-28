CREATE TABLE IF NOT EXISTS cart_items (
    user_id TEXT NOT NULL,
    pizza_id TEXT NOT NULL,
    quantity INTEGER NOT NULL,
    PRIMARY KEY (user_id, pizza_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (pizza_id) REFERENCES pizzas(id) ON DELETE CASCADE
);
