CREATE TABLE line_items (
  id INT NOT NULL AUTO_INCREMENT,
  primary_ad_server ENUM('OPEN_X','LIVERAIL','GENERIC_URL'),
  secondary_ad_server ENUM('OPEN_X','LIVERAIL','GENERIC_URL'),
  primary_ad_server_id VARCHAR(255) NOT NULL,
  secondary_ad_server_id VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

