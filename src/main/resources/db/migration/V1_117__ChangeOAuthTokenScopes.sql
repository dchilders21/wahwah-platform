ALTER TABLE oauth_token_scopes DROP FOREIGN KEY fk_oauth_token_scopes;
DROP TABLE oauth_token_scopes;

ALTER TABLE oauth_tokens ADD COLUMN scopes VARCHAR(1024) NOT NULL;