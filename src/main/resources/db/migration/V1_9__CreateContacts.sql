

ALTER TABLE users
  ADD COLUMN login_enabled BIT(1), --  Whether login is allowed. Sometimes they are just main contacts for publishers, sites, etc
  ADD COLUMN telephone_office VARCHAR(255),
  ADD COLUMN telephone_mobile VARCHAR(255),
  ADD COLUMN relationship VARCHAR(255),
  ADD COLUMN company VARCHAR(255),
  ADD COLUMN title VARCHAR(255),
  ADD COLUMN user_notes TEXT,
  ADD COLUMN internal_notes TEXT; -- internal only
