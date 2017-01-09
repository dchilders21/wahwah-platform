ALTER TABLE accounts MODIFY COLUMN type ENUM('PUBLISHER', 'NETWORK', 'ADVERTISER', 'FREE', 'REPORTING_PRO');

CREATE TABLE account_reporting_pro (
  account_id INT NOT NULL,
  PRIMARY KEY (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE account_reporting_pro ADD FOREIGN KEY fk_reporting_pro_account(account_id) REFERENCES accounts(id);