CREATE TABLE product_widgets (
    id INT NOT NULL AUTO_INCREMENT,
    environment ENUM('PRODUCTION','STAGING','OTHER'),
    product_id INT NOT NULL,
    PRIMARY KEY(id)
) ENGINE=InnoDB AUTO_INCREMENT=10000 DEFAULT CHARSET=utf8;