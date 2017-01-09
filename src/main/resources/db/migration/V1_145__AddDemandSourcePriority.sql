CREATE TABLE demand_source_network_priorities (
  id INT NOT NULL AUTO_INCREMENT,
  network_id INT NOT NULL,
  demand_source_id INT NOT NULL,
  priority INT NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

ALTER TABLE demand_source_network_priorities ADD FOREIGN KEY fk_network_priority_demand_source(demand_source_id) REFERENCES demand_sources(id);
ALTER TABLE demand_source_network_priorities ADD FOREIGN KEY fk_network_priority_network(network_id) REFERENCES account_networks(account_id);

CREATE TABLE demand_source_publisher_priorities (
    id INT NOT NULL AUTO_INCREMENT,
    network_id INT NOT NULL,
    publisher_id INT NOT NULL,
    demand_source_id INT NOT NULL,
    priority INT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

ALTER TABLE demand_source_publisher_priorities ADD FOREIGN KEY fk_publisher_priority_demand_source(demand_source_id) REFERENCES demand_sources(id);
ALTER TABLE demand_source_publisher_priorities ADD FOREIGN KEY fk_publisher_priority_network(network_id) REFERENCES account_networks(account_id);
ALTER TABLE demand_source_publisher_priorities ADD FOREIGN KEY fk_publisher_priority_publisher(publisher_id) REFERENCES account_publishers(account_id);

CREATE TABLE demand_source_site_priorities (
    id INT NOT NULL AUTO_INCREMENT,
    network_id INT NOT NULL,
    site_id INT NOT NULL,
    demand_source_id INT NOT NULL,
    priority INT NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

ALTER TABLE demand_source_site_priorities ADD FOREIGN KEY fk_site_priority_demand_source(demand_source_id) REFERENCES demand_sources(id);
ALTER TABLE demand_source_site_priorities ADD FOREIGN KEY fk_site_priority_network(network_id) REFERENCES account_networks(account_id);
ALTER TABLE demand_source_site_priorities ADD FOREIGN KEY fk_site_priority_site(site_id) REFERENCES sites(id);
