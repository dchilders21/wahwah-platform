CREATE TABLE billable_entities (
  id INT NOT NULL AUTO_INCREMENT,
  entity_type ENUM('NETWORK','PUBLISHER','SITE','PRODUCT') NOT NULL,
  network_id INT NULL,
  publisher_id INT NULL,
  site_id INT NULL,
  product_id INT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

ALTER TABLE billable_entities ADD FOREIGN KEY fk_billable_network(network_id) REFERENCES account_networks(account_id);
ALTER TABLE billable_entities ADD FOREIGN KEY fk_billable_publisher(publisher_id) REFERENCES account_publishers(account_id);
ALTER TABLE billable_entities ADD FOREIGN KEY fk_billable_sites(site_id) REFERENCES sites(id);
ALTER TABLE billable_entities ADD FOREIGN KEY fk_billable_products(product_id) REFERENCES products(id);

ALTER TABLE publisher_revenue_model ADD COLUMN billable_entity_id INT NULL;
ALTER TABLE publisher_revenue_model ADD FOREIGN KEY fk_revenue_billable(billable_entity_id) REFERENCES billable_entities(id);