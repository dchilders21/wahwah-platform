CREATE TABLE google_data_store (
  id INT NOT NULL AUTO_INCREMENT,
  google_value_id VARCHAR(255) NOT NULL,
  PRIMARY KEY(id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

ALTER TABLE google_data_store ADD UNIQUE INDEX UQ_GoogleValue(google_value_id);

CREATE TABLE google_data_store_values (
    id INT NOT NULL AUTO_INCREMENT,
    google_data_store_id INT NOT NULL,
    data_store_key VARCHAR(255) NOT NULL,
    data_store_value VARCHAR(255) NOT NULL,
    PRIMARY KEY(id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

ALTER TABLE google_data_store_values ADD UNIQUE INDEX UQ_GoogleValue(google_data_store_id,data_store_key);
ALTER TABLE google_data_store_values ADD FOREIGN KEY fk_google_data_store(google_data_store_id) REFERENCES google_data_store(id);