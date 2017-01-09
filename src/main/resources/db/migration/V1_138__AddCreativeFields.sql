ALTER TABLE creatives ADD COLUMN primary_ad_server ENUM('OPEN_X','LIVERAIL','GENERIC_URL') NULL;
ALTER TABLE creatives ADD COLUMN primary_ad_server_id VARCHAR(255) NULL;
ALTER TABLE creatives ADD COLUMN secondary_ad_server ENUM('OPEN_X','LIVERAIL','GENERIC_URL') NULL;
ALTER TABLE creatives ADD COLUMN secondary_ad_server_id VARCHAR(255) NULL;

ALTER TABLE demand_sources ADD COLUMN needs_creative_sync BOOLEAN NOT NULL DEFAULT TRUE;