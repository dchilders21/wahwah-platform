CREATE TABLE publisher_revenue_model (
  id INT NOT NULL AUTO_INCREMENT,
  publisher_id INT NOT NULL,
  revenue_model_type ENUM('REVENUE_SHARE','GUARANTEED_CPM','GUARANTEED_CPM_WITH_MINIMUM'),
  revenue_share_percent INT NULL,
  us_desktop_gcpm_cents INT NULL,
  gcpm_cents INT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

ALTER TABLE publisher_revenue_model ADD FOREIGN KEY fk_revenue_model_publisher(publisher_id) REFERENCES accounts(id) ON DELETE CASCADE ON UPDATE CASCADE;