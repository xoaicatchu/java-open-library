CREATE TABLE IF NOT EXISTS product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DOUBLE NOT NULL
);

INSERT INTO product (name, price) VALUES ('Laptop', 1500.0);
INSERT INTO product (name, price) VALUES ('Smartphone', 800.0);
INSERT INTO product (name, price) VALUES ('Headphones', 150.0);
