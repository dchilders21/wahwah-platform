ALTER TABLE accounts MODIFY COLUMN type ENUM('PUBLISHER', 'NETWORK', 'ADVERTISER', 'FREE');

CREATE TABLE account_free (
  account_id INT NOT NULL,
  PRIMARY KEY (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE account_free ADD FOREIGN KEY fk_free_account(account_id) REFERENCES accounts(id);