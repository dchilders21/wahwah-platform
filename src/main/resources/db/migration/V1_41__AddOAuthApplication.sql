CREATE TABLE oauth_applications (
  id INT NOT NULL AUTO_INCREMENT,
  client_id CHAR(36) NOT NULL,
  client_secret CHAR(64) NOT NULL,
  name VARCHAR(255) NOT NULL,
  requires_consent BIT NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE oauth_redirect_uris (
  oauth_app_id INT NOT NULL,
  redirect_uri VARCHAR(1024)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE oauth_redirect_uris ADD FOREIGN KEY fk_oauth_app_uris(oauth_app_id) REFERENCES oauth_applications(id) ON DELETE CASCADE ON UPDATE CASCADE;

CREATE UNIQUE INDEX uq_oauth_client_id ON oauth_applications (client_id);
CREATE UNIQUE INDEX uq_oauth_name ON oauth_applications (name);