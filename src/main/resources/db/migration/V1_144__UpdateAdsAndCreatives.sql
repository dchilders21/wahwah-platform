ALTER TABLE creatives DROP COLUMN nominal_ecpm_cents;
ALTER TABLE creatives ADD COLUMN supports_mobile BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE line_item_ads ADD COLUMN weight_percentage INT NULL;

ALTER TABLE line_items ADD COLUMN ad_weighting_mode ENUM('EQUAL','MANUAL') NOT NULL DEFAULT 'EQUAL';