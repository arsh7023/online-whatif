-- create user
INSERT INTO users (email, enabled, firstname, lastname, PASSWORD)
values('##user##@##hostname##', '1', 'Whatif', 'User', '$2a$10$IMlSuqhi3F..6V4zG/Y78.DCg2DmXT.B7JsvZVpwf5d4FiLiNQo4K');
-- give the user access to whatif as a user
INSERT INTO user_apps (user_app_id, app_id, user_id)
values(1, 1, 1);
INSERT INTO user_roles (user_role_id, role_id, user_id)
values(1, 2, 1);
