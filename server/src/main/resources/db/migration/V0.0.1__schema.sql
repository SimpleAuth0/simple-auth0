-- =========================
-- 用户表
-- =========================
DROP TABLE IF EXISTS `auth0_user`;
CREATE TABLE `auth0_user`
(
    `id`                  bigint       NOT NULL AUTO_INCREMENT,
    `username`            varchar(64)  NOT NULL COMMENT '用户名',
    `password`            varchar(255) NOT NULL COMMENT '口令',
    `nickname`            varchar(64)           DEFAULT NULL COMMENT '昵称',
    `phone`               varchar(15)           DEFAULT NULL COMMENT '手机号',
    `email`               varchar(320)          DEFAULT NULL COMMENT '邮箱',
    `avatar`              varchar(512)          DEFAULT NULL COMMENT '头像URL',
    `set_password_method` varchar(20)  NOT NULL DEFAULT 'ADMIN_SET' COMMENT 'ADMIN_SET:管理员设置;INVITE_EMAIL:邮箱设置;INVITE_OFFLINE:离线邀请',
    `store_type`          varchar(50)  NOT NULL DEFAULT 'DEFAULT' COMMENT '存储类型',
    `status`              tinyint(1) NOT NULL DEFAULT '0' COMMENT '1:正常;2:过期;3:等待密码重置;4:等待初始密码设置',
    `account_non_locked`  tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:已锁定;1:未锁定;',
    `enabled`             tinyint(1) NOT NULL DEFAULT '0' COMMENT '0:禁用;1:启用;',
    `description`         varchar(255)          DEFAULT NULL COMMENT '描述',
    `create_time`         datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`         datetime              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================
-- 用户会话表
-- =========================
DROP TABLE IF EXISTS `auth0_user_sessions`;
CREATE TABLE `auth0_user_sessions`
(
    `id`               bigint       NOT NULL AUTO_INCREMENT,
    `user_id`          bigint       NOT NULL COMMENT '用户ID',
    `session_token`    varchar(128) NOT NULL COMMENT '会话令牌',
    `ip_address`       varchar(45)           DEFAULT NULL COMMENT 'IP地址',
    `device_os`        varchar(64)           DEFAULT NULL COMMENT '设备系统:Linux/Mac OS/Windows',
    `last_access_time` datetime              DEFAULT NULL COMMENT '最近访问时间',
    `login_time`       datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    `create_time`      datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      datetime              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session_token` (`session_token`),
    KEY                `idx_user_id` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================
-- 用户组表
-- =========================
DROP TABLE IF EXISTS `auth0_group`;
CREATE TABLE `auth0_group`
(
    `id`          bigint      NOT NULL AUTO_INCREMENT,
    `name`        varchar(32) NOT NULL COMMENT '组名称',
    `parent_id`   bigint               DEFAULT NULL COMMENT '父组ID',
    `description` varchar(255)         DEFAULT NULL COMMENT '描述',
    `create_time` datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime             DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_group_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================
-- 用户-组关系表
-- =========================
DROP TABLE IF EXISTS `auth0_user_group`;
CREATE TABLE `auth0_user_group`
(
    `id`          bigint   NOT NULL AUTO_INCREMENT,
    `user_id`     bigint   NOT NULL COMMENT '用户ID',
    `group_id`    bigint   NOT NULL COMMENT '组ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime          DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_group` (`user_id`, `group_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================
-- 角色表
-- =========================
DROP TABLE IF EXISTS `auth0_role`;
CREATE TABLE `auth0_role`
(
    `id`            bigint      NOT NULL AUTO_INCREMENT,
    `role_name`     varchar(32) NOT NULL COMMENT '角色名称',
    `role_code`     varchar(32) NOT NULL COMMENT '角色标识',
    `role_audience` tinyint     NOT NULL COMMENT '角色受众,1:应用;2:组织',
    `status`        tinyint     NOT NULL DEFAULT '0' COMMENT '状态:0正常',
    `description`   varchar(255)         DEFAULT NULL COMMENT '描述',
    `create_time`   datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime             DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================
-- 用户-角色关系表
-- =========================
DROP TABLE IF EXISTS `auth0_user_role`;
CREATE TABLE `auth0_user_role`
(
    `id`          bigint   NOT NULL AUTO_INCREMENT,
    `user_id`     bigint   NOT NULL COMMENT '用户ID',
    `role_id`     bigint   NOT NULL COMMENT '角色ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime          DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================
-- 权限表
-- =========================
DROP TABLE IF EXISTS `auth0_permission`;
CREATE TABLE `auth0_permission`
(
    `id`              bigint       NOT NULL AUTO_INCREMENT,
    `name`            varchar(32)  NOT NULL COMMENT '权限名称',
    `parent_id`       bigint                DEFAULT NULL COMMENT '父ID',
    `permission_code` varchar(128) NOT NULL COMMENT '权限编码',
    `resource_type`   varchar(32)  NOT NULL COMMENT '权限类型:API,MENU,DATA,DOC',
    `resource`        varchar(255)          DEFAULT NULL COMMENT '资源标识',
    `action`          varchar(64)           DEFAULT NULL COMMENT '操作动作，如 read, write, delete',
    `description`     varchar(255)          DEFAULT NULL COMMENT '描述',
    `create_time`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     datetime              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`permission_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================
-- 角色-权限关系表
-- =========================
DROP TABLE IF EXISTS `auth0_role_permission`;
CREATE TABLE `auth0_role_permission`
(
    `id`            bigint   NOT NULL AUTO_INCREMENT,
    `role_id`       bigint   NOT NULL COMMENT '角色ID',
    `permission_id` bigint   NOT NULL COMMENT '权限ID',
    `create_time`   datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime          DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================
-- 应用表
-- =========================
DROP TABLE IF EXISTS `auth0_app`;
CREATE TABLE `auth0_app`
(
    `id`            bigint       NOT NULL AUTO_INCREMENT,
    `name`          varchar(32)  NOT NULL COMMENT '应用名称',
    `logo`          varchar(512) NOT NULL COMMENT 'LOGO URL',
    `app_type`      varchar(32)  NOT NULL DEFAULT 'WEB' COMMENT '应用类型:WEB/SPA/SBA/M2M',
    `app_category`  varchar(32)           DEFAULT NULL COMMENT '应用类别:CLIENT/IDP',
    `protocol_type` varchar(32)           DEFAULT NULL COMMENT '协议类型:OAUTH2;OIDC;SAML;CAS;LDAP',
    `description`   varchar(255)          DEFAULT NULL COMMENT '描述',
    `status`        tinyint      NOT NULL DEFAULT '1' COMMENT '状态:0禁用1启用',
    `create_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   datetime              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_app_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- 应用元数据
-- =========================
DROP TABLE IF EXISTS `auth0_app_metadata`;
CREATE TABLE `auth0_app_metadata`
(
    `id`              bigint      NOT NULL AUTO_INCREMENT,
    `template_name`   varchar(64) NOT NULL COMMENT '模板名称',
    `metadata_config` json                 DEFAULT NULL COMMENT '应用配置(JSON)',
    `description`     varchar(255)         DEFAULT NULL COMMENT '描述',
    `create_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     datetime             DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_template_name` (`template_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- OAuth2 注册客户端
-- =========================
DROP TABLE IF EXISTS `auth0_oauth2_registered_client`;
CREATE TABLE `auth0_oauth2_registered_client`
(
    `id`                        varchar(100)  NOT NULL COMMENT '主键',
    `client_id`                 varchar(100)  NOT NULL COMMENT '客户端ID',
    `client_id_issued_at`       timestamp     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '颁发时间',
    `client_secret`             varchar(200)           DEFAULT NULL COMMENT '密钥',
    `client_secret_expires_at`  timestamp              DEFAULT NULL COMMENT '密钥过期时间',
    `client_name`               varchar(200)  NOT NULL COMMENT '客户端名称',
    `client_auth_methods`       varchar(1000) NOT NULL COMMENT '认证方式',
    `authorization_grant_types` varchar(1000) NOT NULL COMMENT '授权类型',
    `redirect_uris`             varchar(1000)          DEFAULT NULL COMMENT '回调URI',
    `scopes`                    varchar(1000) NOT NULL COMMENT '作用域',
    `client_settings`           varchar(2000) NOT NULL COMMENT '客户端配置',
    `token_settings`            varchar(2000) NOT NULL COMMENT '令牌配置',
    `create_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     datetime             DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_client_id` (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- OAuth2 授权信息
-- =========================
DROP TABLE IF EXISTS `auth0_oauth2_authorization`;
CREATE TABLE `auth0_oauth2_authorization`
(
    `id`                            varchar(100) NOT NULL COMMENT '授权记录ID',
    `registered_client_id`          varchar(100) NOT NULL COMMENT '客户端ID，关联 auth0_oauth2_registered_client.client_id',
    `principal_name`                varchar(200) NOT NULL COMMENT '授权主体（用户名或用户标识）',
    `authorization_grant_type`      varchar(100) NOT NULL COMMENT '授权模式，如 authorization_code、client_credentials',
    `authorized_scopes`             varchar(1000) DEFAULT NULL COMMENT '授权范围（scope 列表，以空格或JSON存储）',
    `attributes`                    blob          DEFAULT NULL COMMENT '附加属性（序列化存储）',
    `state`                         varchar(500)  DEFAULT NULL COMMENT 'OAuth2 状态参数，用于防止 CSRF 攻击',
    `authorization_code_value`      blob          DEFAULT NULL COMMENT '授权码值（加密存储）',
    `authorization_code_issued_at`  timestamp     DEFAULT NULL COMMENT '授权码签发时间',
    `authorization_code_expires_at` timestamp     DEFAULT NULL COMMENT '授权码过期时间',
    `authorization_code_metadata`   blob          DEFAULT NULL COMMENT '授权码元数据（序列化存储）',
    `access_token_value`            blob          DEFAULT NULL COMMENT '访问令牌值（JWT/opaque token，加密存储）',
    `access_token_issued_at`        timestamp     DEFAULT NULL COMMENT '访问令牌签发时间',
    `access_token_expires_at`       timestamp     DEFAULT NULL COMMENT '访问令牌过期时间',
    `access_token_metadata`         blob          DEFAULT NULL COMMENT '访问令牌元数据（序列化存储）',
    `access_token_type`             varchar(100)  DEFAULT NULL COMMENT '令牌类型，如 Bearer',
    `access_token_scopes`           varchar(1000) DEFAULT NULL COMMENT '访问令牌作用域',
    `oidc_id_token_value`           blob          DEFAULT NULL COMMENT 'ID Token 值（JWT 格式，加密存储）',
    `oidc_id_token_issued_at`       timestamp     DEFAULT NULL COMMENT 'ID Token 签发时间',
    `oidc_id_token_expires_at`      timestamp     DEFAULT NULL COMMENT 'ID Token 过期时间',
    `oidc_id_token_metadata`        blob          DEFAULT NULL COMMENT 'ID Token 元数据',
    `refresh_token_value`           blob          DEFAULT NULL COMMENT '刷新令牌值（加密存储）',
    `refresh_token_issued_at`       timestamp     DEFAULT NULL COMMENT '刷新令牌签发时间',
    `refresh_token_expires_at`      timestamp     DEFAULT NULL COMMENT '刷新令牌过期时间',
    `refresh_token_metadata`        blob          DEFAULT NULL COMMENT '刷新令牌元数据（序列化存储）',

    PRIMARY KEY (`id`),
    KEY                             `idx_registered_client` (`registered_client_id`),
    KEY                             `idx_principal` (`principal_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


-- =========================
-- OAuth2 授权同意
-- =========================
DROP TABLE IF EXISTS `auth0_oauth2_authorization_consent`;
CREATE TABLE `auth0_oauth2_authorization_consent`
(
    `registered_client_id` varchar(100)  NOT NULL,
    `principal_name`       varchar(200)  NOT NULL,
    `authorities`          varchar(1000) NOT NULL COMMENT '同意的权限',
    `create_time`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     datetime             DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`registered_client_id`, `principal_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =========================
-- 系统日志
-- =========================
DROP TABLE IF EXISTS `auth0_log`;
CREATE TABLE `auth0_log`
(
    `id`           bigint       NOT NULL AUTO_INCREMENT,
    `event_type`   varchar(32)  NOT NULL COMMENT '事件类型',
    `event_detail` varchar(255) NOT NULL COMMENT '事件详情',
    `user_id`      bigint                DEFAULT NULL COMMENT '用户ID',
    `role_id`      bigint                DEFAULT NULL COMMENT '角色ID',
    `ip_address`   varchar(45)  NOT NULL COMMENT 'IP地址',
    `device_type`  varchar(64)           DEFAULT NULL COMMENT '设备类型',
    `device_os`    varchar(64)           DEFAULT NULL COMMENT '设备系统:Linux/Mac OS/Windows',
    `extend_info`   json                  DEFAULT NULL COMMENT '扩展信息(JSON，包含请求参数、响应体、请求方式等)',
    `success`      tinyint      NOT NULL DEFAULT '1' COMMENT '结果:0失败;1成功',
    `create_time`  datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`  datetime              DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY            `idx_event_type` (`event_type`),
    KEY            `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
