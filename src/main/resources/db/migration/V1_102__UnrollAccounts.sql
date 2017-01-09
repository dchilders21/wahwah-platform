CREATE TABLE account_publishers (
  account_id INT NOT NULL,
  external_id VARCHAR(255) NULL,
  adserver_type VARCHAR(255) NULL,
  external_id2 VARCHAR(255) NULL,
  liverail_sitelist_id VARCHAR(255) NULL,
  PRIMARY KEY (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE account_networks (
  account_id INT NOT NULL,
  ox_network_id VARCHAR(255) NULL,
  lr_ox_advertiser_id INT NULL,
  lr_ox_order_id INT NULL,
  PRIMARY KEY (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE account_advertisers (
  account_id INT NOT NULL,
  PRIMARY KEY (account_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE account_publishers ADD FOREIGN KEY fk_publishers_account(account_id) REFERENCES accounts(id);
ALTER TABLE account_networks ADD FOREIGN KEY fk_networks_account(account_id) REFERENCES accounts(id);
ALTER TABLE account_advertisers ADD FOREIGN KEY fk_advertisers_account(account_id) REFERENCES accounts(id);