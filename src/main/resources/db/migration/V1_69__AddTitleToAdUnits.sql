ALTER TABLE ad_units ADD COLUMN title VARCHAR(256);
UPDATE ad_units SET title = "Additional Passbacks" WHERE platform_created = 0;