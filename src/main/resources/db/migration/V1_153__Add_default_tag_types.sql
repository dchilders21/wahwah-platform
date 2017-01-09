ALTER TABLE account_networks ADD COLUMN default_product_format enum('OUTSTREAM', 'FLOATER', 'BANNER', 'CUSTOM') DEFAULT 'OUTSTREAM';
ALTER TABLE account_publishers ADD COLUMN default_product_format  enum('OUTSTREAM', 'FLOATER', 'BANNER', 'CUSTOM') DEFAULT 'OUTSTREAM';
ALTER TABLE sites ADD COLUMN default_product_format  enum('OUTSTREAM', 'FLOATER', 'BANNER', 'CUSTOM') DEFAULT 'OUTSTREAM';