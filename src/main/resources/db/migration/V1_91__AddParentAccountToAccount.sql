ALTER TABLE accounts ADD COLUMN parent_account_id INT NULL;
ALTER TABLE accounts ADD FOREIGN KEY fk_account_parent_account(parent_account_id) REFERENCES accounts(id);