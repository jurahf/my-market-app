CREATE TABLE IF NOT EXISTS item (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    title VARCHAR(255) NOT NULL,
    description TEXT,
    img_path VARCHAR(500),
    price BIGINT NOT NULL
    );

CREATE TABLE IF NOT EXISTS cart (
                                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                    item_id BIGINT NOT NULL UNIQUE,  -- UNIQUE обеспечивает связь 1:1
                                    count INT NOT NULL,
                                    CONSTRAINT fk_cart_item FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE
    );


CREATE TABLE IF NOT EXISTS orders (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  total_sum BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS order_items (
   order_id BIGINT NOT NULL,
   item_id BIGINT NOT NULL,
   count BIGINT NOT NULL DEFAULT 1,
   PRIMARY KEY (order_id, item_id),
   FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
   FOREIGN KEY (item_id) REFERENCES item(id) ON DELETE CASCADE
);


CREATE INDEX idx_item_title ON item(title);
CREATE INDEX idx_item_price ON item(price);
CREATE INDEX idx_cart_item_id ON cart(item_id);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_item_id ON order_items(item_id);

