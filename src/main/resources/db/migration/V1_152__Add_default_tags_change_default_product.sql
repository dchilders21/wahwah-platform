ALTER TABLE sites DROP COLUMN default_product;
ALTER TABLE sites ADD COLUMN default_product_id INT NULL;