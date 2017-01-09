ALTER TABLE account_publishers ADD COLUMN passback_tag_contents longtext DEFAULT NULL;
ALTER TABLE account_networks ADD COLUMN passback_tag_contents longtext DEFAULT NULL;
ALTER TABLE sites ADD COLUMN passback_tag_contents longtext DEFAULT NULL;