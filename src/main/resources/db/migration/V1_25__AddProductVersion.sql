CREATE TABLE product_versions (
  id INT NOT NULL AUTO_INCREMENT,
  git_branch VARCHAR(255) NOT NULL UNIQUE,
  version_name VARCHAR(255) NOT NULL UNIQUE,
  product_type ENUM('TOOLBAR','MEDIA_PORTAL','STANDALONE'),
  is_released BIT(1) NOT NULL,
  is_obsolete BIT(1) NOT NULL,
  last_published_time TIMESTAMP NOT NULL,
  last_commit_id CHAR(40),
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

INSERT INTO audit_action_types (name,value) VALUES ('Toolbar Published','TOOLBAR_PUBLISH');

ALTER TABLE products ADD COLUMN version_id INT;
ALTER TABLE products ADD FOREIGN KEY fk_product_version(version_id) REFERENCES product_versions(id) ON DELETE CASCADE ON UPDATE CASCADE;