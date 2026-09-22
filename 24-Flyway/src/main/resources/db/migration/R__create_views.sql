CREATE OR REPLACE VIEW product_summary AS
SELECT p.id, p.name, p.price, c.name AS category_name, p.status, p.description
FROM products p
JOIN categories c ON p.category_id = c.id;
