ALTER TABLE line_items ADD COLUMN site_id INT NOT NULL;
-- See PLATFORM-662. Existing entries will fail foreign key check until manually mapped, so temporarily disable.
SET FOREIGN_KEY_CHECKS=0;
ALTER TABLE line_items ADD FOREIGN KEY fk_line_item_site_id(site_id) REFERENCES sites(id) ON DELETE CASCADE;
SET FOREIGN_KEY_CHECKS=1;

ALTER TABLE line_item_ads ADD COLUMN is_archived BOOLEAN NOT NULL DEFAULT FALSE;