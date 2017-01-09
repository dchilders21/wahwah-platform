ALTER TABLE sites CHANGE COLUMN liverail_id liverail_id VARCHAR(255);
ALTER TABLE accounts CHANGE COLUMN liverail_sitelist_id  liverail_sitelist_id VARCHAR(255);
UPDATE sites SET  liverail_id = NULL WHERE liverail_id = '0';
UPDATE sites SET  liverail_id = NULL WHERE liverail_id = '';
UPDATE accounts SET  liverail_sitelist_id = NULL WHERE liverail_sitelist_id = '0';
UPDATE accounts SET  liverail_sitelist_id = NULL WHERE liverail_sitelist_id = '';