ALTER TABLE product_toolbar DROP COLUMN widget_id;
ALTER TABLE products ADD COLUMN widget_id INT NULL;

CREATE TABLE product_music_artists (
  id INT NOT NULL AUTO_INCREMENT,
  product_id INT NOT NULL,
  artist_id VARCHAR(255) NOT NULL,
  artist_name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

CREATE TABLE product_music_stations (
  id INT NOT NULL AUTO_INCREMENT,
  product_id INT NOT NULL,
  station_id VARCHAR(255) NOT NULL,
  station_name VARCHAR(255) NOT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

ALTER TABLE product_music_artists ADD FOREIGN KEY fk_product_artists(product_id) REFERENCES products(id) ON DELETE CASCADE ON UPDATE CASCADE;
ALTER TABLE product_music_stations ADD FOREIGN KEY fk_product_stations(product_id) REFERENCES products(id) ON DELETE CASCADE ON UPDATE CASCADE;