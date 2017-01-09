ALTER TABLE sites ADD is_archived BOOLEAN NOT NULL DEFAULT FALSE;
ALTER TABLE sites ADD replaced_with_site_id INT NULL;

ALTER TABLE sites ADD FOREIGN KEY fk_sites_replaced(replaced_with_site_id) REFERENCES sites(id);