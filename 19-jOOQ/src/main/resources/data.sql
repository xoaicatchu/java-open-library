INSERT INTO categories (name) VALUES ('Electronics'), ('Clothing'), ('Home');

INSERT INTO products (name, category_id, price) VALUES 
('Laptop', 1, 1000.00),
('Phone', 1, 500.00),
('T-Shirt', 2, 20.00),
('Jeans', 2, 50.00),
('Blender', 3, 100.00);

INSERT INTO sales (product_id, quantity, sale_date, revenue) VALUES
(1, 2, '2023-10-01', 2000.00),
(2, 5, '2023-10-02', 2500.00),
(1, 1, '2023-10-03', 1000.00),
(3, 10, '2023-10-04', 200.00),
(4, 2, '2023-10-05', 100.00),
(5, 3, '2023-10-06', 300.00),
(2, 2, '2023-10-07', 1000.00);
