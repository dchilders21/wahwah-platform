CREATE TABLE user_external_credentials (
  id INT NOT NULL AUTO_INCREMENT,
  user_id INT NOT NULL,
  git_username VARCHAR(255),
  git_password VARCHAR(255),
  edgecast_username VARCHAR(255),
  edgecast_password VARCHAR(255),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  UNIQUE(user_id),
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;