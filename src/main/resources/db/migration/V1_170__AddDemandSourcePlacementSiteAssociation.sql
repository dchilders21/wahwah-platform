CREATE TABLE demand_source_placements_sites (
    demand_source_placement_id INT NOT NULL,
    site_id INT NOT NULL
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8;

ALTER TABLE demand_source_placements_sites ADD FOREIGN KEY fk_placement_site_placement(demand_source_placement_id) REFERENCES demand_source_placements(id);
ALTER TABLE demand_source_placements_sites ADD FOREIGN KEY fk_placement_site_site(site_id) REFERENCES sites(id);