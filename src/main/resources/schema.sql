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

CREATE INDEX idx_item_title ON item(title);
CREATE INDEX idx_item_price ON item(price);
CREATE INDEX idx_cart_item_id ON cart(item_id);

