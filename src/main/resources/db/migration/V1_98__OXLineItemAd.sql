CREATE TABLE line_item_ads (
  id INT NOT NULL AUTO_INCREMENT,
  line_item_id INT NOT NULL,
  primary_ad_server ENUM('OPEN_X','LIVERAIL','GENERIC_URL'),
  secondary_ad_server ENUM('OPEN_X','LIVERAIL','GENERIC_URL'),
  primary_ad_server_id VARCHAR(255) NULL,
  secondary_ad_server_id VARCHAR(255) NULL,
  ox_ad_server_uid VARCHAR(255) NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (line_item_id) REFERENCES line_items(id) ON DELETE CASCADE
)ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

ALTER TABLE line_items CHANGE COLUMN primary_ad_server_id  primary_ad_server_id VARCHAR(255) NULL;
ALTER TABLE line_items CHANGE COLUMN secondary_ad_server_id secondary_ad_server_id VARCHAR(255) NULL;
ALTER TABLE line_items ADD COLUMN ox_ad_server_uid VARCHAR(255) NULL;

UPDATE line_items SET primary_ad_server_id=NULL  WHERE primary_ad_server_id = 0;
UPDATE line_items SET primary_ad_server_id=NULL  WHERE primary_ad_server_id = '';
UPDATE line_items SET secondary_ad_server_id=NULL  WHERE secondary_ad_server_id = 0;
UPDATE line_items SET secondary_ad_server_id=NULL  WHERE secondary_ad_server_id = '';