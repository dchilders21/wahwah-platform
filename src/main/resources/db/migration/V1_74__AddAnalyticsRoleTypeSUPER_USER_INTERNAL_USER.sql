INSERT INTO user_roles (user_id,role_name)
SELECT user_id,'INTERNAL_USER' AS role_name FROM user_roles WHERE role_name = 'SUPER_USER' ORDER BY user_id;