ALTER TABLE line_item_ads ADD COLUMN creative_id INT NULL;
ALTER TABLE line_item_ads ADD FOREIGN KEY fk_ad_creative(creative_id) REFERENCES creatives(id);