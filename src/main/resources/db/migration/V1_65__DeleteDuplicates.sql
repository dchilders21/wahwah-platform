DELETE IGNORE au FROM wahwahplatform.ad_units au WHERE NOT EXISTS (SELECT ai.combined_adunit_id , ai.backup_adunit_id , ai.video_adunit_id, ai.mobile_adunit_id , ai.tablet_adunit_id , ai.leavebehind_adunit_id FROM wahwahplatform.adconfig_inpage ai
WHERE
au.id = ai.combined_adunit_id OR 
au.id = ai.backup_adunit_id OR
au.id = ai.video_adunit_id OR
au.id = ai.mobile_adunit_id OR
au.id = ai.tablet_adunit_id OR
au.id = ai.leavebehind_adunit_id
);