CREATE TABLE `oauth_code` (
  `code` varchar(255) DEFAULT NULL,
  `authentication_id` varchar(255) NOT NULL,
  `authentication` blob,
  `authorization_code` blob,
  PRIMARY KEY (`authentication_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `oauth_client_details` (
  `client_id` varchar(255) NOT NULL,
  `resource_ids` varchar(255) DEFAULT NULL,
  `client_secret` varchar(255) DEFAULT NULL,
  `scope` varchar(255) DEFAULT NULL,
  `authorized_grant_types` varchar(255) DEFAULT NULL,
  `web_server_redirect_uri` varchar(255) DEFAULT NULL,
  `authorities` varchar(255) DEFAULT NULL,
  `access_token_validity` int(11) DEFAULT NULL,
  `refresh_token_validity` int(11) DEFAULT NULL,
  `additional_information` varchar(4096) DEFAULT NULL,
  `autoapprove` varchar(4096) DEFAULT NULL,
  PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
insert into `oauth_client_details` ( `authorities`, `authorized_grant_types`, `web_server_redirect_uri`, `scope`, `additional_information`, `autoapprove`, `resource_ids`, `refresh_token_validity`, `client_secret`, `client_id`, `access_token_validity`) values ( 'ROLE_ADMIN,ROLE_USER', 'password,refresh_token', null, 'read,write', null, null, null, null, 'nLCnwdIhizWbykHyuZM6TpQDd7KwK9IXDK8LGsa7SOW', 'ArtemisWeb', '7200');