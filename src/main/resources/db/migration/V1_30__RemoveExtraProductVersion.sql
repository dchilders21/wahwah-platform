ALTER TABLE products DROP FOREIGN KEY fk_product_version;
ALTER TABLE products DROP COLUMN version_id;

UPDATE sites SET site_type = 'ENTERTAINMENT';