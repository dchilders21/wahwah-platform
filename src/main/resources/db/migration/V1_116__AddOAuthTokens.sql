CREATE TABLE oauth_tokens (
  id INT NOT NULL AUTO_INCREMENT,
  auth_code CHAR(36) NOT NULL,
  oauth_grant_id INT NOT NULL,
  expires_at DATETIME NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE oauth_token_scopes (
  oauth_token_id INT NOT NULL,
  scope VARCHAR(250)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE oauth_tokens ADD FOREIGN KEY fk_oauth_token_grant(oauth_grant_id) REFERENCES oauth_grants(id);
ALTER TAbLE oauth_token_scopes ADD FOREIGN KEY fk_oauth_token_scopes(oauth_token_id) REFERENCES oauth_tokens(id);

ALTER TABLE oauth_grant_scopes DROP FOREIGN KEY fk_oauth_grant_scopes;
DROP TABLE oauth_grant_scopes;

ALTER TABLE oauth_grants DROP COLUMN auth_code;