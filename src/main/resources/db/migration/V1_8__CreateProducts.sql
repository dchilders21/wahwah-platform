SET foreign_key_checks = 0;

CREATE TABLE products ( -- list table
  id INT NOT NULL AUTO_INCREMENT,
  type ENUM('TOOLBAR','MEDIA_PORTAL','STANDALONE'),
  site_id INT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE product_toolbar (
  id INT NOT NULL AUTO_INCREMENT,
  widget_id INT NOT NULL,
  lang VARCHAR(5) NOT NULL,
  environment VARCHAR(20) NOT NULL,
  product_id INT NOT NULL,
  position ENUM('bottom','top') NOT NULL,
  radio_type ENUM('tab','inline') NOT NULL,
  radio_align ENUM('left','right') NOT NULL,
  log_level TINYINT NULL,
  debug_mode BIT(1) NULL,
  skin_color INTEGER NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE product_mediaportal (
  id INT NOT NULL AUTO_INCREMENT,
  site_id INT NOT NULL,
  widget_id INT NOT NULL,
  lang VARCHAR(5) NOT NULL,
  environment VARCHAR(20) NOT NULL,
  product_id INT NOT NULL,
  radioLeft TINYINT NOT NULL,
  radioTop TINYINT NOT NULL,
  historyLeft TINYINT NOT NULL,
  historyTop TINYINT NOT NULL,
  backgroundImage VARCHAR(2083),
  log_level TINYINT NULL,
  debug_mode BIT(1) NULL,
  skin_color INTEGER NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE product_standalone (
  id INT NOT NULL AUTO_INCREMENT,
  site_id INT NOT NULL,
  widget_id INT NOT NULL,
  lang VARCHAR(5) NOT NULL,
  environment VARCHAR(20) NOT NULL,
  product_id INT NOT NULL,
  position ENUM('bottom','top') NOT NULL,
  radio_type ENUM('tab','inline') NOT NULL,
  radio_align ENUM('left','right') NOT NULL,
  log_level TINYINT NULL,
  debug_mode BIT(1) NULL,
  skin_color INTEGER NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

ALTER TABLE adconfig_inpage
    DROP COLUMN widget_id,
    ADD COLUMN product_id INT NOT NULL,
    DROP FOREIGN KEY adconfig_inpage_ibfk_1;
ALTER TABLE adconfig_inpage
    ADD FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE;
ALTER TABLE adconfig_midroll
    DROP COLUMN widget_id,
    ADD COLUMN product_id INT NOT NULL,
    DROP FOREIGN KEY adconfig_midroll_ibfk_1;
ALTER TABLE adconfig_midroll
    ADD FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE;
ALTER TABLE adconfig_preroll
    DROP COLUMN widget_id,
    ADD COLUMN product_id INT NOT NULL,
    DROP FOREIGN KEY adconfig_preroll_ibfk_1;
ALTER TABLE adconfig_preroll
    ADD FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE;

ALTER TABLE user_roles CHANGE role_name role_name ENUM('USER','SUPER_USER','PUBLISHER_ADMIN','SITE_ADMIN','PRODUCT_ADMIN','TOOLBAR_PUBLISHER');

DROP TABLE widgets;

SET foreign_key_checks = 1;
