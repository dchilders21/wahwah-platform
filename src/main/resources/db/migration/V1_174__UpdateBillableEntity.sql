ALTER TABLE billable_entities ADD CONSTRAINT UQ_BillableEntityReference UNIQUE (entity_type,network_id,publisher_id,site_id,product_id);