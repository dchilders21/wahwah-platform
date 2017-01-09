-- Keeping widget_id was intentional even though standalone doesn't have a radio.
-- some plusses are that it keeps things consistent and allows us to convert it to another product
ALTER TABLE product_standalone_ad ADD widget_id INTEGER NOT NULL;
UPDATE product_standalone_ad SET widget_id=1;