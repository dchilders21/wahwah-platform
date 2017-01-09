ALTER TABLE line_item_targeting CHANGE prouct_id product_id INT;
ALTER TABLE line_item_targeting MODIFY type ENUM('SITE','PUBLISHER','PRODUCT') NOT NULL;
ALTER TABLE line_item_targeting CHANGE displayType display_type ENUM('FIRST_LOOK', 'BACKUP');
