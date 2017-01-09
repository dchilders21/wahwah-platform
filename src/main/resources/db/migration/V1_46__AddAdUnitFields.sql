ALTER TABLE adconfig_inpage ADD COLUMN combined_adunit_id INT;
ALTER TABLE adconfig_inpage ADD COLUMN backup_adunit_id INT;
ALTER TABLE adconfig_inpage ADD COLUMN video_adunit_id INT;
ALTER TABLE adconfig_inpage ADD COLUMN mobile_adunit_id INT;
ALTER TABLE adconfig_inpage ADD COLUMN tablet_adunit_id INT;
ALTER TABLE adconfig_inpage ADD COLUMN leavebehind_adunit_id INT;

ALTER TABLE adconfig_inpage ADD FOREIGN KEY fk_adconfig_combined(combined_adunit_id) REFERENCES ad_units(id);
ALTER TABLE adconfig_inpage ADD FOREIGN KEY fk_adconfig_backup(backup_adunit_id) REFERENCES ad_units(id);
ALTER TABLE adconfig_inpage ADD FOREIGN KEY fk_adconfig_video(video_adunit_id) REFERENCES ad_units(id);
ALTER TABLE adconfig_inpage ADD FOREIGN KEY fk_adconfig_mobile(mobile_adunit_id) REFERENCES ad_units(id);
ALTER TABLE adconfig_inpage ADD FOREIGN KEY fk_adconfig_tablet(tablet_adunit_id) REFERENCES ad_units(id);
ALTER TABLE adconfig_inpage ADD FOREIGN KEY fk_adconfig_leavebehind(leavebehind_adunit_id) REFERENCES ad_units(id);