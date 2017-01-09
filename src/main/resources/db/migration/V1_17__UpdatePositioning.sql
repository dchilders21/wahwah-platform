
ALTER TABLE product_toolbar DROP position;
ALTER TABLE product_toolbar ADD align_horiz ENUM('left', 'right', 'center') NOT NULL DEFAULT 'center';
ALTER TABLE product_toolbar ADD align_vert ENUM('top', 'bottom', 'middle') NOT NULL DEFAULT 'bottom';
UPDATE product_toolbar SET align_horiz='center';
UPDATE product_toolbar SET align_vert='bottom';
ALTER TABLE product_standalone DROP position;
ALTER TABLE product_standalone DROP radio_align;
ALTER TABLE product_standalone DROP radio_type;
ALTER TABLE product_standalone DROP FOREIGN KEY product_standalone_ibfk_2;
ALTER TABLE product_mediaportal DROP FOREIGN KEY product_mediaportal_ibfk_2;
ALTER TABLE product_standalone DROP site_id;
ALTER TABLE product_standalone DROP widget_id;
ALTER TABLE product_standalone ADD ad_format ENUM('banner', 'floater') NOT NULL DEFAULT 'banner';
ALTER TABLE product_standalone ADD align_horiz ENUM('left', 'right', 'center') NOT NULL DEFAULT 'left';
ALTER TABLE product_standalone ADD align_vert ENUM('top', 'bottom', 'middle') NOT NULL DEFAULT 'top';
UPDATE product_standalone SET align_horiz='left';
UPDATE product_standalone SET align_vert='top';
UPDATE product_standalone SET ad_format='banner';
ALTER TABLE product_standalone DROP log_level;
ALTER TABLE product_mediaportal DROP log_level;
ALTER TABLE product_toolbar DROP log_level;
ALTER TABLE products ADD log_level ENUM('error', 'debug', 'info', 'warn') NOT NULL DEFAULT 'debug';
UPDATE products SET log_level='debug';
ALTER TABLE products MODIFY COLUMN type ENUM('TOOLBAR','MEDIA_PORTAL','STANDALONE_AD') NOT NULL;
UPDATE products SET type='TOOLBAR';
RENAME TABLE product_standalone TO product_standalone_ad;