-- remove obsolete or unecessary values
ALTER TABLE product_standalone_ad DROP lang;
ALTER TABLE product_toolbar DROP lang;
ALTER TABLE product_mediaportal DROP lang;
ALTER TABLE product_standalone_ad DROP skin_color;
ALTER TABLE product_standalone_ad DROP skin_type;
ALTER TABLE product_standalone_ad DROP environment;
ALTER TABLE product_toolbar DROP environment;
ALTER TABLE product_mediaportal DROP environment;