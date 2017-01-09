UPDATE users SET login_enabled = 1;
ALTER TABLE users CHANGE login_enabled login_enabled BIT NOT NULL DEFAULT 1;