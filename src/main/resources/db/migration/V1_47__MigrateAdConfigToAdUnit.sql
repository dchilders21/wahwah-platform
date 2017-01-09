UPDATE adconfig_inpage SET combined_adunit_id = NULL;
UPDATE adconfig_inpage SET backup_adunit_id = NULL;
UPDATE adconfig_inpage SET video_adunit_id = NULL;
UPDATE adconfig_inpage SET mobile_adunit_id = NULL;
UPDATE adconfig_inpage SET tablet_adunit_id = NULL;
UPDATE adconfig_inpage SET leavebehind_adunit_id = NULL;
DELETE FROM ad_units;

DROP PROCEDURE IF EXISTS flyway_sp_47;
DELIMITER $$

CREATE PROCEDURE flyway_sp_47()
BEGIN
	DECLARE done INT DEFAULT FALSE;
	DECLARE _adconfig_id INT DEFAULT 0;
	DECLARE _product_id INT DEFAULT 0;
	DECLARE _auid VARCHAR(255) DEFAULT '';
	DECLARE _width INT DEFAULT 0;
	DECLARE _height INT DEFAULT 0;

	DECLARE combined_cursor CURSOR FOR (SELECT id,REPLACE(REPLACE(combined_url,"ww-openx:auid=",""),";","") AS combined_auid,product_id,display_width,display_height FROM adconfig_inpage WHERE combined_url IS NOT NULL HAVING combined_auid NOT IN ('','0'));
	DECLARE backup_cursor CURSOR FOR (SELECT id,REPLACE(REPLACE(backup_display_url,"ww-openx:auid=",""),";","") AS backup_auid,product_id,video_width,video_height FROM adconfig_inpage WHERE backup_display_url IS NOT NULL HAVING backup_auid NOT IN ('','0'));
	DECLARE video_cursor CURSOR FOR (SELECT id,REPLACE(backup_video_url,"http://ox-d.wahwahnetworks.com/v/1.0/av?auid=","") AS video_auid,product_id,display_width,display_height FROM adconfig_inpage WHERE backup_video_url IS NOT NULL HAVING video_auid NOT IN ('','0'));
	DECLARE mobile_cursor CURSOR FOR (SELECT id,REPLACE(REPLACE(mobile_320_url,"ww-openx:auid=",""),";","") AS mobile_auid,product_id FROM adconfig_inpage WHERE mobile_320_url IS NOT NULL HAVING mobile_auid NOT IN ('','0'));
	DECLARE tablet_cursor CURSOR FOR (SELECT id,REPLACE(REPLACE(mobile_728_url,"ww-openx:auid=",""),";","") AS tablet_auid,product_id FROM adconfig_inpage WHERE mobile_728_url IS NOT NULL HAVING tablet_auid NOT IN ('','0'));
	DECLARE leavebehind_cursor CURSOR FOR (SELECT id,REPLACE(REPLACE(leave_behind_url,"ww-openx:auid=",""),";","") AS leavebehind_auid,product_id,display_width,display_height FROM adconfig_inpage WHERE leave_behind_url IS NOT NULL HAVING leavebehind_auid NOT IN ('','0'));

	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

	OPEN combined_cursor;

	read_combined_loop: LOOP
		FETCH NEXT FROM combined_cursor INTO _adconfig_id,_auid,_product_id,_width,_height;

		IF done THEN
			LEAVE read_combined_loop;
		END IF;

		INSERT INTO ad_units (ad_server,external_id,width,height,unit_type,product_id) VALUES ('OPEN_X',_auid,_width,_height,'DISPLAY',_product_id);
		UPDATE adconfig_inpage SET combined_adunit_id = LAST_INSERT_ID() WHERE id = _adconfig_id;
	END LOOP;

	CLOSE combined_cursor;

	SET done = FALSE;

	OPEN backup_cursor;

	read_backup_loop: LOOP
		FETCH NEXT FROM backup_cursor INTO _adconfig_id,_auid,_product_id,_width,_height;

		IF done THEN
			LEAVE read_backup_loop;
		END IF;

		INSERT INTO ad_units (ad_server,external_id,width,height,unit_type,product_id) VALUES ('OPEN_X',_auid,_width,_height,'DISPLAY',_product_id);
		UPDATE adconfig_inpage SET backup_adunit_id = LAST_INSERT_ID() WHERE id = _adconfig_id;
	END LOOP;

	CLOSE backup_cursor;

	SET done = FALSE;

	OPEN video_cursor;

	read_video_loop: LOOP
		FETCH NEXT FROM video_cursor INTO _adconfig_id,_auid,_product_id,_width,_height;

		IF done THEN
			LEAVE read_video_loop;
		END IF;

		INSERT INTO ad_units (ad_server,external_id,width,height,unit_type,product_id) VALUES ('OPEN_X',_auid,_width,_height,'VIDEO',_product_id);
		UPDATE adconfig_inpage SET video_adunit_id = LAST_INSERT_ID() WHERE id = _adconfig_id;
	END LOOP;

	CLOSE video_cursor;

	SET done = FALSE;

	OPEN mobile_cursor;

	read_mobile_loop: LOOP
		FETCH NEXT FROM mobile_cursor INTO _adconfig_id,_auid,_product_id;

		IF done THEN
			LEAVE read_mobile_loop;
		END IF;

		INSERT INTO ad_units (ad_server,external_id,width,height,unit_type,product_id) VALUES ('OPEN_X',_auid,320,50,'MOBILE',_product_id);
		UPDATE adconfig_inpage SET mobile_adunit_id = LAST_INSERT_ID() WHERE id = _adconfig_id;
	END LOOP;

	CLOSE mobile_cursor;

	SET done = FALSE;

	OPEN tablet_cursor;

	read_tablet_loop: LOOP
		FETCH NEXT FROM tablet_cursor INTO _adconfig_id,_auid,_product_id;

		IF done THEN
			LEAVE read_tablet_loop;
		END IF;

		INSERT INTO ad_units (ad_server,external_id,width,height,unit_type,product_id) VALUES ('OPEN_X',_auid,728,90,'MOBILE',_product_id);
		UPDATE adconfig_inpage SET tablet_adunit_id = LAST_INSERT_ID() WHERE id = _adconfig_id;
	END LOOP;

	CLOSE tablet_cursor;

	SET done = FALSE;

	OPEN leavebehind_cursor;

	read_leavebehind_loop: LOOP
		FETCH NEXT FROM leavebehind_cursor INTO _adconfig_id,_auid,_product_id,_width,_height;

		IF done THEN
			LEAVE read_leavebehind_loop;
		END IF;

		INSERT INTO ad_units (ad_server,external_id,width,height,unit_type,product_id) VALUES ('OPEN_X',_auid,_width,_height,'DISPLAY',_product_id);
		UPDATE adconfig_inpage SET leavebehind_adunit_id = LAST_INSERT_ID() WHERE id = _adconfig_id;
	END LOOP;

	CLOSE leavebehind_cursor;
END $$

DELIMITER ;
CALL flyway_sp_47();
DROP PROCEDURE flyway_sp_47;



