ALTER TABLE line_item_targeting MODIFY COLUMN type ENUM('SITE','PUBLISHER','PRODUCT','NETWORK') NOT NULL;
ALTER TABLE line_item_targeting ADD COLUMN network_id INT NULL;

ALTER TABLE line_item_targeting ADD FOREIGN KEY fk_target_network (network_id) REFERENCES account_networks(account_id);