ALTER TABLE publishers RENAME TO accounts;



ALTER TABLE accounts ADD
(
-- account-viewable notes
account_notes TEXT,
-- internal only
internal_notes TEXT,
-- May be possible to have advertiser accounts, etc in future
type ENUM('PUBLISHER') DEFAULT 'PUBLISHER'
);

-- foreign key constraints updated automatically
ALTER TABLE sites CHANGE publisher_id account_id INT NOT NULL;
-- foreign key constraints updated automatically
ALTER TABLE users CHANGE publisher_id account_id INT NULL;


ALTER TABLE accounts DROP COLUMN psf;
ALTER TABLE accounts DROP COLUMN is_friendly;

-- user-viewable notes
ALTER TABLE sites ADD COLUMN user_notes TEXT;
-- internal only
ALTER TABLE sites ADD COLUMN internal_notes TEXT;
ALTER TABLE sites ADD COLUMN psf VARCHAR(255) NULL;
ALTER TABLE sites ADD COLUMN is_friendly BIT(1) DEFAULT 0;
