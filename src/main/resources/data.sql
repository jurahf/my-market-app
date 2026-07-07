
-- Вставка тестовых товаров
INSERT INTO item (title, description, img_path, price) VALUES
                                                           ('Смартфон Xiaomi Redmi Note 11',
                                                            'Смартфон с 6.43-дюймовым AMOLED-дисплеем, 4 ГБ ОЗУ и 128 ГБ встроенной памяти, процессор Snapdragon 680',
                                                            '/images/xiaomi_redmi_note_11.jpg',
                                                            15999),

                                                           ('Ноутбук Lenovo IdeaPad 3',
                                                            '15.6-дюймовый ноутбук с процессором Intel Core i5, 8 ГБ ОЗУ, 256 ГБ SSD, Windows 11',
                                                            '/images/lenovo_ideapad_3.jpg',
                                                            34999),

                                                           ('Наушники Sony WH-1000XM5',
                                                            'Беспроводные наушники с активным шумоподавлением, до 30 часов работы, поддерживают LDAC',
                                                            '/images/sony_wh_1000xm5.jpg',
                                                            29999),

                                                           ('Клавиатура Logitech MX Keys',
                                                            'Беспроводная клавиатура с подсветкой, эргономичный дизайн, подключение до 3 устройств',
                                                            '/images/logitech_mx_keys.jpg',
                                                            8999),

                                                           ('Монитор Samsung Odyssey G5',
                                                            '27-дюймовый монитор с разрешением QHD (2560x1440), частотой 144 Гц, временем отклика 1 мс',
                                                            '/images/samsung_odyssey_g5.jpg',
                                                            25999);

-- Вставка данных в корзину (связь с первым товаром - Xiaomi Redmi Note 11)
INSERT INTO cart (item_id, count) VALUES
    (1, 2);  -- 2 штуки Xiaomi Redmi Note 11 в корзине