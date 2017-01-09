ALTER TABLE line_items ADD COLUMN demand_source_id INT NULL;
ALTER TABLE line_items ADD FOREIGN KEY fk_lineitem_demandpartner(demand_source_id) REFERENCES demand_sources(id);