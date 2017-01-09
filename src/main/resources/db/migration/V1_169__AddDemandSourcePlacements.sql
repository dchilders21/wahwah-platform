CREATE TABLE demand_source_placements (
    id INT NOT NULL AUTO_INCREMENT,
    demand_source_id INT NOT NULL,
    placement_name VARCHAR(255) NOT NULL,
    PRIMARY KEY(id)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8;

ALTER TABLE demand_source_placements ADD FOREIGN KEY fk_demand_source_placement (demand_source_id) REFERENCES demand_sources(id);
ALTER TABLE demand_source_placements ADD UNIQUE KEY UQ_DemandSourcePlacement (demand_source_id,placement_name);