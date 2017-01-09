CREATE TABLE oauth_grants (
  id INT NOT NULL AUTO_INCREMENT,
  application_id INT NOT NULL,
  user_id INT NOT NULL,
  auth_code CHAR(36) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE oauth_grant_scopes (
  oauth_grant_id INT NOT NULL,
  scope VARCHAR(250)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE oauth_grant_scopes ADD FOREIGN KEY fk_oauth_grant_scopes(oauth_grant_id) REFERENCES oauth_grants(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE oauth_grants ADD FOREIGN KEY fk_oauth_grant_app(application_id) REFERENCES oauth_applications(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE oauth_grants ADD FOREIGN KEY fk_oauth_grant_user(user_id) REFERENCES users(id) ON DELETE CASCADE ON UPDATE CASCADE;
