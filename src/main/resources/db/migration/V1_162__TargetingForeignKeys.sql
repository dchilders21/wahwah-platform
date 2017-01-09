DELETE FROM line_item_targeting;
ALTER TABLE line_item_targeting ADD FOREIGN KEY fk_target_publisher (publisher_id) REFERENCES account_publishers(account_id);
ALTER TABLE line_item_targeting ADD FOREIGN KEY fk_target_site (site_id) REFERENCES sites(id);
ALTER TABLE line_item_targeting ADD FOREIGN KEY fk_target_product (product_id) REFERENCES products(id);