-- rearrange tables and adds product_name
ALTER TABLE product_standalone_ad DROP debug_mode;
ALTER TABLE product_toolbar DROP debug_mode;
ALTER TABLE product_mediaportal DROP debug_mode;
ALTER TABLE products ADD debug_mode BIT(1) NOT NULL DEFAULT FALSE;
UPDATE  products SET debug_mode = FALSE;
ALTER TABLE  products ADD product_name VARCHAR(255);