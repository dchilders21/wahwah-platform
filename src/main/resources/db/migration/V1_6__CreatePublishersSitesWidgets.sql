CREATE TABLE publishers (
  id INT NOT NULL AUTO_INCREMENT,
  name VARCHAR(255) NULL,
  psf VARCHAR(255) NULL,
  is_friendly BIT(1) NULL,
  PRIMARY KEY (id)
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE sites (
  id INT NOT NULL AUTO_INCREMENT,
  publisher_id INT NOT NULL,
  site_name VARCHAR(255) NOT NULL,
  site_url VARCHAR(255) NOT NULL,
  contact_name VARCHAR(255),
  contact_email VARCHAR(255),
  site_language ENUM('en','es','pt') NOT NULL,
  skin_color ENUM('dark','light') NOT NULL,
  site_type VARCHAR(20) NULL,
  is_media_portal BIT(1) NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (publisher_id) REFERENCES publishers(id) ON DELETE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE widgets (
  id INT NOT NULL AUTO_INCREMENT,
  site_id INT NOT NULL,
  environment VARCHAR(20) NOT NULL,
  position ENUM('bottom','top') NOT NULL,
  radio_type ENUM('tab','inline') NOT NULL,
  radio_align ENUM('left','right') NOT NULL,
  log_level TINYINT NULL,
  debug_mode BIT(1) NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (site_id) REFERENCES sites(id) ON DELETE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE adconfig_inpage (
  id INT NOT NULL AUTO_INCREMENT,
  widget_id INT NOT NULL,
  enabled BIT(1) NULL,
  width SMALLINT NULL,
  height SMALLINT NULL,
  display_url VARCHAR(255) NULL,
  video_url VARCHAR(255) NULL,
  audio_url VARCHAR(255) NULL,
  vid_countdown_num_format VARCHAR(20) NULL,
  banner_ads_per_video_ad SMALLINT NULL,
  seconds_between_banners SMALLINT NULL,
  per_session_cap SMALLINT NULL,
  seconds_to_expand SMALLINT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (widget_id) REFERENCES widgets(id) ON DELETE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE adconfig_preroll (
  id INT NOT NULL AUTO_INCREMENT,
  widget_id INT NOT NULL,
  enabled BIT(1) NULL,
  width SMALLINT NULL,
  height SMALLINT NULL,
  display_url VARCHAR(255) NULL,
  video_url VARCHAR(255) NULL,
  audio_url VARCHAR(255) NULL,
  linear_type ENUM('radio') NOT NULL,
  vid_countdown_num_format VARCHAR(20) NULL,
  display_seconds_to_close SMALLINT NULL,
  allow_close_button_after SMALLINT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (widget_id) REFERENCES widgets(id) ON DELETE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE adconfig_midroll (
  id INT NOT NULL AUTO_INCREMENT,
  widget_id INT NOT NULL,
  enabled BIT(1) NULL,
  width SMALLINT NULL,
  height SMALLINT NULL,
  display_url VARCHAR(255) NULL,
  video_url VARCHAR(255) NULL,
  audio_url VARCHAR(255) NULL,
  linear_type ENUM('radio') NOT NULL,
  vid_countdown_num_format VARCHAR(20) NULL,
  display_seconds_to_close SMALLINT NULL,
  allow_close_button_after SMALLINT NULL,
  max_seconds_between_ads SMALLINT NULL,
  songs_between_ads SMALLINT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (widget_id) REFERENCES widgets(id) ON DELETE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

ALTER TABLE users ADD COLUMN publisher_id INT NULL;
ALTER TABLE users ADD FOREIGN KEY (publisher_id) REFERENCES publishers(id) ON DELETE SET NULL;