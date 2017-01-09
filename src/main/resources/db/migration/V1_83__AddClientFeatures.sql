CREATE TABLE client_features (
  id INT NOT NULL AUTO_INCREMENT,
  variable_name VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(255) NOT NULL,
  variable_type ENUM('NONE','NUMBER','STRING','BOOLEAN'),
  PRIMARY KEY(id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE product_client_features (
  id INT NOT NULL AUTO_INCREMENT,
  product_id INT NOT NULL,
  client_feature_id INT NOT NULL,
  value_string VARCHAR(255) NULL,
  value_number DOUBLE NULL,
  value_boolean BOOLEAN NULL,
  PRIMARY KEY(id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

ALTER TABLE product_client_features ADD FOREIGN KEY product_client_features_product(product_id) REFERENCES products(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE product_client_features ADD FOREIGN KEY product_client_features_feature(client_feature_id) REFERENCES client_features(id) ON DELETE CASCADE ON UPDATE CASCADE;