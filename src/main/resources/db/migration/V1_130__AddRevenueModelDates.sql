ALTER TABLE publisher_revenue_model ADD COLUMN start_date DATE NULL;
ALTER TABLE publisher_revenue_model ADD COLUMN end_date DATE NULL;

UPDATE publisher_revenue_model SET start_date = '2015-04-01';

ALTER TABLE publisher_revenue_model MODIFY COLUMN start_date DATE NOT NULL;