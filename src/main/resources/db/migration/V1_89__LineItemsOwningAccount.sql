ALTER TABLE line_items ADD COLUMN publisher_id INT NOT NULL;
ALTER TABLE line_items ADD COLUMN name VARCHAR(255) NOT NULL;

ALTER TABLE line_items ADD FOREIGN KEY fk_line_item_publisher_id(publisher_id) REFERENCES accounts(id) ON DELETE CASCADE;