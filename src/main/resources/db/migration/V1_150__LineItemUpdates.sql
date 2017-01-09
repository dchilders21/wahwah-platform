ALTER TABLE line_items ADD COLUMN expected_cpm_cents INT NULL;
ALTER TABLE line_items ADD COLUMN delivery_mode ENUM('WEB','LINEAR_VIDEO') NULL;

ALTER TABLE demand_sources ADD COLUMN is_direct_advertiser BOOLEAN NOT NULL DEFAULT FALSE;