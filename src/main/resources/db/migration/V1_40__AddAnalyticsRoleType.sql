ALTER TABLE user_roles CHANGE role_name role_name ENUM('USER','SUPER_USER','PUBLISHER_ADMIN','SITE_ADMIN','PRODUCT_ADMIN','PLAYER_ADMIN','TOOLBAR_PUBLISHER','ANALYTICS');

INSERT INTO user_roles (user_id,role_name)
SELECT user_id,'ANALYTICS' AS role_name FROM user_roles WHERE role_name = 'SUPER_USER' ORDER BY user_id;