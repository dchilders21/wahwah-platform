CREATE TABLE line_item_targeting (
	id INT NOT NULL AUTO_INCREMENT,
	name VARCHAR(255) NOT NULL,
	type ENUM("site","publisher","product"),
	line_item_id INT NOT NULL,
	publisher_id INT,
	site_id INT,
	prouct_id INT,
	displayType ENUM("first_look", "backup"),

    PRIMARY KEY(id)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;

ALTER TABLE line_item_targeting ADD FOREIGN KEY fk_line_item(line_item_id) REFERENCES line_items(id);