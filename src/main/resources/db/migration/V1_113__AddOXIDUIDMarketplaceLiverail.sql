ALTER TABLE account_networks ADD COLUMN ox_marketplace_advertiser_uid VARCHAR(255) DEFAULT NULL;
ALTER TABLE account_networks ADD COLUMN lr_ox_advertiser_uid VARCHAR(255) DEFAULT NULL;
ALTER TABLE account_networks ADD COLUMN lr_ox_order_uid VARCHAR(255) DEFAULT NULL;
ALTER TABLE account_networks ADD COLUMN ox_marketplace_order_id INT DEFAULT NULL;
ALTER TABLE account_networks ADD COLUMN ox_marketplace_order_uid VARCHAR(255) DEFAULT NULL;