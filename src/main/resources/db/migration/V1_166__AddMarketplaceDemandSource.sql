ALTER TABLE account_networks ADD COLUMN marketplace_demand_source_id INT NULL;
ALTER TABLE account_networks ADD FOREIGN KEY fk_network_marketplace_demand (marketplace_demand_source_id) REFERENCES demand_sources(id);

ALTER TABLE sites ADD COLUMN marketplace_line_item_id INT NULL;
ALTER TABLE sites ADD COLUMN marketplace_creative_id INT NULL;