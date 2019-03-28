
INSERT INTO sys_language VALUES (1, 'en_us', '英文');
INSERT INTO sys_language VALUES (2, 'zh_cn', '中文');


INSERT INTO sys_tenant(id,tenant_name,tenant_short_name,license_limit,status,enabled,deleted,created_by,last_updated_by,country_code,show_custom_logo,enable_new_control,tenant_code)
VALUES(0,'默认租户','默认租户', '5','1001','1','0', '1','1', 'CNY', '1','1','admin');


INSERT INTO sys_user(id,user_oid, login,password_hash, activated, LANGUAGE,created_by,last_updated_by,deleted,status,password_attempt,lock_status,tenant_id, data_source)
VALUES(1,'683edfba-4e52-489e-8ce4-6e820d5478b2','admin','{bcrypt}$2a$10$3JtUoBrFBy.OcfrbiR.ZgOecrDO0v2tSGaGgwF5/3Z1WOWgcEmRG2',1, 'zh_cn',0, 0, 0,1001,0, 2001, 0,'web');


INSERT INTO sys_role(id,role_code,role_name,tenant_id,enabled, deleted,created_by,last_updated_by) VALUES (1, 'admin', '系统管理员', 0, 1, 0, 1, 1);

INSERT INTO sys_user_role(id, role_id, user_id, enabled, created_by, last_updated_by)VALUES(1, 1, 1, 1, 1, 1);

