UPDATE publisher_revenue_model SET revenue_model_type = 'GUARANTEED_CPM' WHERE revenue_model_type = 'GUARANTEED_CPM_WITH_MINIMUM';
ALTER TABLE publisher_revenue_model MODIFY COLUMN revenue_model_type ENUM('REVENUE_SHARE','GUARANTEED_CPM','GUARANTEED_CPM_WITH_FILL') NOT NULL;
UPDATE publisher_revenue_model SET revenue_model_type = 'GUARANTEED_CPM_WITH_FILL' WHERE is_guaranteed_fill = TRUE;
ALTER TABLE publisher_revenue_model DROP COLUMN is_guaranteed_fill;
ALTER TABLE publisher_revenue_model ADD COLUMN format ENUM('OUTSTREAM','FLOATER','BANNER','CUSTOM','ADSERVERNATIVE') NULL;