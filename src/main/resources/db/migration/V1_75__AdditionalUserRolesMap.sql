INSERT INTO user_roles (user_id,role_name)
SELECT user_id,'ADVERTISING_ADMIN' AS role_name FROM user_roles WHERE role_name = 'SUPER_USER' ORDER BY user_id;

INSERT INTO user_roles (user_id,role_name)
SELECT user_id,'PUBLISHER_USER' AS role_name FROM user_roles WHERE role_name = 'PUBLISHER_ADMIN' ORDER BY user_id;

