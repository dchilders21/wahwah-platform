UPDATE accounts SET type = 'PUBLISHER' WHERE type IS NULL;
ALTER TABLE accounts MODIFY COLUMN type ENUM('PUBLISHER','NETWORK', 'ADVERTISER') NOT NULL;