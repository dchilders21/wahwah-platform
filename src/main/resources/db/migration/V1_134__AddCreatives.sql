CREATE TABLE creatives (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  demand_source_id INT NOT NULL,
  creative_type ENUM('VAST_XML','HTML') NOT NULL,
  tag_contents VARCHAR(255) NOT NULL,
  nominal_ecpm_cents INT NULL,
  PRIMARY KEY(id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

ALTER TABLE creatives ADD FOREIGN KEY fk_creatives_demand_source(demand_source_id) REFERENCES demand_sources(id);
ALTER TABLE creatives ADD CONSTRAINT UQ_CreativeName UNIQUE(name);

ALTER TABLE demand_sources ADD CONSTRAINT UQ_DemandSourceName UNIQUE(name);