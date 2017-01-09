ALTER TABLE account_networks ADD COLUMN default_publisher_id INT NULL;
ALTER TABLE account_publishers ADD COLUMN default_site_id INT NULL;
ALTER TABLE sites ADD COLUMN default_product INT NULL;
ALTER TABLE account_publishers ADD COLUMN is_default BOOLEAN DEFAULT FALSE;
ALTER TABLE sites ADD COLUMN is_default BOOLEAN DEFAULT FALSE;
ALTER TABLE products ADD COLUMN is_default BOOLEAN DEFAULT FALSE;