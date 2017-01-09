ALTER TABLE product_standalone_ad
ADD inad_breakout BIT(1) DEFAULT 0;

ALTER TABLE product_standalone_ad
ADD outstream_autoload BIT(1) DEFAULT 0;

ALTER TABLE product_standalone_ad
ADD outstream_float BIT(1) DEFAULT 0;

ALTER TABLE product_standalone_ad
ADD outstream_triggerid VARCHAR(255) DEFAULT NULL;

ALTER TABLE product_standalone_ad CHANGE ad_format ad_format ENUM('banner','floater','ostream');