ALTER TABLE billable_entities ADD COLUMN demand_source_placement_id INT NULL;
ALTER TABLE billable_entities MODIFY COLUMN entity_type ENUM('NETWORK','PUBLISHER','SITE','PRODUCT','DEMAND_SOURCE_PLACEMENT');
ALTER TABLE billable_entities ADD FOREIGN KEY fk_billable_entity_placement (demand_source_placement_id) REFERENCES demand_source_placements(id);