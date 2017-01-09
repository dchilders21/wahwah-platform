CREATE TABLE ad_servers (
  id INT NOT NULL AUTO_INCREMENT,
  account_id INT NOT NULL,
  ad_server_type ENUM('OPEN_X','LIVERAIL') NOT NULL,
  name VARCHAR(255) NOT NULL,
  ad_server_details TEXT,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

ALTER TABLE ad_servers ADD FOREIGN KEY fk_ad_server_account(account_id) REFERENCES accounts(id);

CREATE TABLE demand_sources (
  id INT NOT NULL AUTO_INCREMENT,
  account_id INT NOT NULL,
  name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

ALTER TABLE demand_sources ADD FOREIGN KEY fk_demand_source_account(account_id) REFERENCES accounts(id);

CREATE TABLE ad_server_link (
  id INT NOT NULL AUTO_INCREMENT,
  ad_server_id INT NOT NULL,
  external_id VARCHAR(255),
  external_id_alt VARCHAR(255),
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

ALTER TABLE ad_server_link ADD FOREIGN KEY fk_ad_server_link(ad_server_id) REFERENCES ad_servers(id);

CREATE TABLE demand_source_ad_server_links (
  demand_source_id INT NOT NULL,
  ad_server_link_id INT NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE demand_source_ad_server_links ADD FOREIGN KEY fk_demandsourcelink_demandsource(demand_source_id) REFERENCES demand_sources(id);
ALTER TABLE demand_source_ad_server_links ADD FOREIGN KEY fk_demandsourcelink_adserver(ad_server_link_id) REFERENCES ad_server_link(id);

CREATE TABLE demand_source_connection_types (
  id INT NOT NULL AUTO_INCREMENT,
  type_key VARCHAR(255) NOT NULL,
  name VARCHAR(255) NOT NULL,
  connection_metadata TEXT NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE demand_source_connections (
  id INT NOT NULL AUTO_INCREMENT,
  demand_source_id INT NOT NULL,
  demand_source_connection_type_id INT NOT NULL,
  connection_details TEXT NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

ALTER TABLE demand_source_connections ADD FOREIGN KEY fk_demand_source_connection_type(demand_source_connection_type_id) REFERENCES demand_source_connection_types(id);
ALTER TABLE demand_source_connections ADD FOREIGN KEY fk_demand_source_connection(demand_source_id) REFERENCES demand_sources(id);