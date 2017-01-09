ALTER TABLE products ADD product_version_id INT NULL;

ALTER TABLE products
ADD CONSTRAINT fk_products_productversion
FOREIGN KEY (product_version_id)
REFERENCES product_versions(id)
ON DELETE SET NULL
ON UPDATE SET NULL;