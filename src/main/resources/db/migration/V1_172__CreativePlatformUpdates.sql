ALTER TABLE creatives DROP COLUMN supports_mobile;
ALTER TABLE creatives ADD COLUMN creative_platform ENUM('DESKTOP','MOBILE','ALL') NOT NULL DEFAULT 'DESKTOP';