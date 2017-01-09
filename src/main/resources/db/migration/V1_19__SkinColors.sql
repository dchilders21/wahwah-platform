-- null is default
ALTER TABLE product_standalone_ad ADD skin_type ENUM('light','dark','custom');
ALTER TABLE product_toolbar ADD skin_type ENUM('light','dark','custom');
ALTER TABLE product_mediaportal ADD skin_type ENUM('light','dark','custom');
ALTER TABLE sites DROP skin_color;