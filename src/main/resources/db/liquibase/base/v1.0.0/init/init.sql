INSERT INTO sys_role(id,role_code,role_name,tenant_id,enabled, deleted,created_by,last_updated_by) VALUES (0, 'platform-admin', '平台管理员', 0, 1, 0, 1, 1);

INSERT INTO sys_user_role(id, role_id, user_id, enabled, created_by, last_updated_by)VALUES(0, 0, 1, 1, 1, 1);

insert into sys_role_function (id, role_id, function_id, version_number, created_by,  last_updated_by)
values ('1111204059755884546', '0', '1104612994363777025', '1',  '1',  '1');

insert into sys_role_function (id, role_id, function_id, version_number,  created_by,  last_updated_by)
values ('1111204059793633281', '0', '1102821763599208450', '1', '1',  '1');

insert into sys_role_function (id, role_id, function_id, version_number, created_by, last_updated_by)
values ('1111204059718135810', '0', '1103104406271668226', '1',  '1', '1');