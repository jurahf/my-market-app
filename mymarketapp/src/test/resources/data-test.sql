INSERT INTO item (id, title, description, img_path, price) VALUES (1, 'Test Item', 'Test Description', '/images/test_item.jpg', 100);
INSERT INTO users (id, login, password_hash) VALUES (1, 'user', '$2b$10$W16P9J3qtZWprtsYRolqpeIGwN6amvHG1dVaQN452hIoi6sSK/G.a');
INSERT INTO orders (id, user_id, total_sum) VALUES (1, 1, 100);
INSERT INTO order_items (order_id, item_id, count) VALUES (1, 1, 1);
