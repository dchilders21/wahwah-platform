DROP PROCEDURE IF EXISTS flyway_sp_103;
DELIMITER $$

CREATE PROCEDURE flyway_sp_103()
BEGIN
	DECLARE done INT DEFAULT FALSE;
	DECLARE _account_id INT;

	DECLARE _external_id VARCHAR(255);
  DECLARE _adserver_type VARCHAR(255);
  DECLARE _external_id2 VARCHAR(255);
  DECLARE _liverail_sitelist_id VARCHAR(255);

  DECLARE _ox_network_id VARCHAR(255);

  DECLARE publisher_cursor CURSOR FOR (SELECT id,external_id,adserver_type,external_id2,liverail_sitelist_id FROM accounts WHERE type = 'PUBLISHER');
  DECLARE network_cursor CURSOR FOR (SELECT id,ox_network_id FROM accounts WHERE type = 'NETWORK');

  DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

  OPEN publisher_cursor;

  read_publisher_loop: LOOP
    FETCH NEXT FROM publisher_cursor INTO _account_id,_external_id,_adserver_type,_external_id2,_liverail_sitelist_id;

    IF done THEN
      LEAVE read_publisher_loop;
    END IF;

    INSERT INTO account_publishers (account_id,external_id,adserver_type,external_id2,liverail_sitelist_id) VALUES (_account_id,_external_id,_adserver_type,_external_id2,_liverail_sitelist_id);
  END LOOP;

  CLOSE publisher_cursor;

  SET done = FALSE;

  OPEN network_cursor;

  read_network_loop: LOOP
    FETCH NEXT FROM network_cursor INTO _account_id,_ox_network_id;

    IF done THEN
      LEAVE read_network_loop;
    END IF;

    INSERT INTO account_networks (account_id,ox_network_id) VALUES (_account_id,_ox_network_id);

  END LOOP;

  CLOSE network_cursor;
END $$

DELIMITER ;
CALL flyway_sp_103();
DROP PROCEDURE flyway_sp_103;