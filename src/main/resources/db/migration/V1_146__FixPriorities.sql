ALTER TABLE demand_source_network_priorities MODIFY COLUMN network_id INT NULL;
ALTER TABLE demand_source_publisher_priorities MODIFY COLUMN network_id INT NULL;
ALTER TABLE demand_source_site_priorities MODIFY COLUMN network_id INT NULL;