UPDATE account_publishers ap INNER JOIN accounts a ON ap.account_id = a.id SET ap.default_product_format = 'OUTSTREAM' where ap.default_product_format IS NULL AND a.parent_account_id IS NULL;
UPDATE account_networks SET default_product_format = 'OUTSTREAM' where default_product_format is NULL;
UPDATE sites SET default_product_format = NULL;