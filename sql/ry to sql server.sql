-- ----------------------------
-- 1、部门表
-- ----------------------------
IF OBJECT_ID('sys_dept', 'U') IS NOT NULL
DROP TABLE sys_dept;

create table sys_dept
(
    dept_id     bigint identity (200, 1)
        primary key,
    parent_id   bigint       default 0,
    ancestors   nvarchar(50) default '',
    dept_name   nvarchar(30) default '',
    order_num   int          default 0,
    leader      nvarchar(20) default NULL,
    phone       nvarchar(11) default NULL,
    email       nvarchar(50) default NULL,
    status      char         default '0',
    del_flag    char         default '0',
    create_by   nvarchar(64) default '',
    create_time datetime,
    update_by   nvarchar(64) default '',
    update_time datetime
)

exec sp_addextendedproperty 'MS_Description', N'部门表', 'SCHEMA', 'dbo', 'TABLE', 'sys_dept'
exec sp_addextendedproperty 'MS_Description', N'部门ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_dept', 'COLUMN', 'dept_id'
exec sp_addextendedproperty 'MS_Description', N'父部门ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_dept', 'COLUMN', 'parent_id'
exec sp_addextendedproperty 'MS_Description', N'祖级列表', 'SCHEMA', 'dbo', 'TABLE', 'sys_dept', 'COLUMN', 'ancestors'
exec sp_addextendedproperty 'MS_Description', N'部门名称', 'SCHEMA', 'dbo', 'TABLE', 'sys_dept', 'COLUMN', 'dept_name'
exec sp_addextendedproperty 'MS_Description', N'排序', 'SCHEMA', 'dbo', 'TABLE', 'sys_dept', 'COLUMN', 'order_num'
exec sp_addextendedproperty 'MS_Description', N'负责人', 'SCHEMA', 'dbo', 'TABLE', 'sys_dept', 'COLUMN', 'leader'
exec sp_addextendedproperty 'MS_Description', N'联系电话', 'SCHEMA', 'dbo', 'TABLE', 'sys_dept', 'COLUMN', 'phone'
exec sp_addextendedproperty 'MS_Description', N'邮箱', 'SCHEMA', 'dbo', 'TABLE', 'sys_dept', 'COLUMN', 'email'
exec sp_addextendedproperty 'MS_Description', N'状态（0正常 1停用）', 'SCHEMA', 'dbo', 'TABLE', 'sys_dept', 'COLUMN',
     'status'
exec sp_addextendedproperty 'MS_Description', N'删除标志（0代表存在 2代表删除）', 'SCHEMA', 'dbo', 'TABLE', 'sys_dept',
     'COLUMN', 'del_flag'
exec sp_addextendedproperty 'MS_Description', N'创建者', 'SCHEMA', 'dbo', 'TABLE', 'sys_dept', 'COLUMN', 'create_by'
exec sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_dept', 'COLUMN', 'create_time'
exec sp_addextendedproperty 'MS_Description', N'更新者', 'SCHEMA', 'dbo', 'TABLE', 'sys_dept', 'COLUMN', 'update_by'
exec sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_dept', 'COLUMN', 'update_time'

-- 开始自增列传入值，默认是不允许自增列插入显示值的
SET IDENTITY_INSERT sys_dept ON;

-- 初始化-部门表数据
INSERT INTO sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, leader, phone, email, status, del_flag, create_by, create_time, update_by, update_time)
VALUES
    (100, 0, '0', '若依科技', 0, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', GETDATE(), '', NULL),
    (101, 100, '0,100', '深圳总公司', 1, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', GETDATE(), '', NULL),
    (102, 100, '0,100', '长沙分公司', 2, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', GETDATE(), '', NULL),
    (103, 101, '0,100,101', '研发部门', 1, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', GETDATE(), '', NULL),
    (104, 101, '0,100,101', '市场部门', 2, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', GETDATE(), '', NULL),
    (105, 101, '0,100,101', '测试部门', 3, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', GETDATE(), '', NULL),
    (106, 101, '0,100,101', '财务部门', 4, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', GETDATE(), '', NULL),
    (107, 101, '0,100,101', '运维部门', 5, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', GETDATE(), '', NULL),
    (108, 102, '0,100,102', '市场部门', 1, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', GETDATE(), '', NULL),
    (109, 102, '0,100,102', '财务部门', 2, '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', GETDATE(), '', NULL);

-- 插入后需要关闭，因为IDENTITY_INSERT是会话级别，整个会话期间它都会保持开启状态，直到显式地将其关闭。
-- 因此如果在一个会话中为一个表启用了IDENTITY_INSERT，然后尝试为另一个表启用它，就会导致错误。
SET IDENTITY_INSERT sys_dept OFF;

-- ----------------------------
-- 2、用户信息表
-- ----------------------------
IF OBJECT_ID('sys_user', 'U') IS NOT NULL
DROP TABLE sys_user;

create table sys_user
(
    user_id     bigint identity (100, 1)
        primary key,
    dept_id     bigint        default NULL,
    user_name   nvarchar(30) not null,
    nick_name   nvarchar(30) not null,
    user_type   nvarchar(2)   default '00',
    email       nvarchar(50)  default '',
    phonenumber nvarchar(11)  default '',
    sex         char          default '0',
    avatar      nvarchar(100) default '',
    password    nvarchar(100) default '',
    status      char          default '0',
    del_flag    char          default '0',
    login_ip    nvarchar(128) default '',
    login_date  datetime      default NULL,
    create_by   nvarchar(64)  default '',
    create_time datetime,
    update_by   nvarchar(64)  default '',
    update_time datetime,
    remark      nvarchar(500) default NULL
)

exec sp_addextendedproperty 'MS_Description', N'用户信息表', 'SCHEMA', 'dbo', 'TABLE', 'sys_user'
exec sp_addextendedproperty 'MS_Description', N'用户ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'user_id'
exec sp_addextendedproperty 'MS_Description', N'部门ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'dept_id'
exec sp_addextendedproperty 'MS_Description', N'用户账号', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'user_name'
exec sp_addextendedproperty 'MS_Description', N'用户昵称', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'nick_name'
exec sp_addextendedproperty 'MS_Description', N'用户类型（00系统用户）', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN',
     'user_type'
exec sp_addextendedproperty 'MS_Description', N'用户邮箱', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'email'
exec sp_addextendedproperty 'MS_Description', N'手机号码', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'phonenumber'
exec sp_addextendedproperty 'MS_Description', N'用户性别（0男 1女 2未知）', 'SCHEMA', 'dbo', 'TABLE', 'sys_user',
     'COLUMN', 'sex'
exec sp_addextendedproperty 'MS_Description', N'头像地址', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'avatar'
exec sp_addextendedproperty 'MS_Description', N'密码', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'password'
exec sp_addextendedproperty 'MS_Description', N'账号状态（0正常 1停用）', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN',
     'status'
exec sp_addextendedproperty 'MS_Description', N'删除标志（0代表存在 2代表删除）', 'SCHEMA', 'dbo', 'TABLE', 'sys_user',
     'COLUMN', 'del_flag'
exec sp_addextendedproperty 'MS_Description', N'最后登录IP', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'login_ip'
exec sp_addextendedproperty 'MS_Description', N'最后登录时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN',
     'login_date'
exec sp_addextendedproperty 'MS_Description', N'创建者', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'create_by'
exec sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'create_time'
exec sp_addextendedproperty 'MS_Description', N'更新者', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'update_by'
exec sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'update_time'
exec sp_addextendedproperty 'MS_Description', N'备注', 'SCHEMA', 'dbo', 'TABLE', 'sys_user', 'COLUMN', 'remark'

-- 启用 IDENTITY_INSERT
SET IDENTITY_INSERT sys_user ON;

-- 初始化-用户信息表数据
INSERT INTO sys_user (user_id, dept_id, user_name, nick_name, user_type, email, phonenumber, sex, avatar, password, status, del_flag, login_ip, login_date, create_by, create_time, update_by, update_time, remark)
VALUES
    (1, 103, 'admin', '若依', '00', 'ry@163.com', '15888888888', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', GETDATE(), 'admin', GETDATE(), '', NULL, '管理员'),
    (2, 105, 'ry', '若依', '00', 'ry@qq.com', '15666666666', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', GETDATE(), 'admin', GETDATE(), '', NULL, '测试员');

-- 禁用 IDENTITY_INSERT
SET IDENTITY_INSERT sys_dept OFF;

-- ----------------------------
-- 3、岗位信息表
-- ----------------------------
-- 删除并重新创建 sys_post 表
IF OBJECT_ID('sys_post', 'U') IS NOT NULL
DROP TABLE sys_post;

create table sys_post
(
    post_id     bigint identity
        primary key,
    post_code   nvarchar(64) not null,
    post_name   nvarchar(50) not null,
    post_sort   int          not null,
    status      char         not null,
    create_by   nvarchar(64)  default '',
    create_time datetime,
    update_by   nvarchar(64)  default '',
    update_time datetime,
    remark      nvarchar(500) default NULL
)

exec sp_addextendedproperty 'MS_Description', N'岗位信息表', 'SCHEMA', 'dbo', 'TABLE', 'sys_post'
exec sp_addextendedproperty 'MS_Description', N'岗位ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_post', 'COLUMN', 'post_id'
exec sp_addextendedproperty 'MS_Description', N'岗位编码', 'SCHEMA', 'dbo', 'TABLE', 'sys_post', 'COLUMN', 'post_code'
exec sp_addextendedproperty 'MS_Description', N'岗位名称', 'SCHEMA', 'dbo', 'TABLE', 'sys_post', 'COLUMN', 'post_name'
exec sp_addextendedproperty 'MS_Description', N'显示顺序', 'SCHEMA', 'dbo', 'TABLE', 'sys_post', 'COLUMN', 'post_sort'
exec sp_addextendedproperty 'MS_Description', N'状态（0正常 1停用）', 'SCHEMA', 'dbo', 'TABLE', 'sys_post', 'COLUMN',
     'status'
exec sp_addextendedproperty 'MS_Description', N'创建者', 'SCHEMA', 'dbo', 'TABLE', 'sys_post', 'COLUMN', 'create_by'
exec sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_post', 'COLUMN', 'create_time'
exec sp_addextendedproperty 'MS_Description', N'更新者', 'SCHEMA', 'dbo', 'TABLE', 'sys_post', 'COLUMN', 'update_by'
exec sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_post', 'COLUMN', 'update_time'
exec sp_addextendedproperty 'MS_Description', N'备注', 'SCHEMA', 'dbo', 'TABLE', 'sys_post', 'COLUMN', 'remark'

-- 启用 IDENTITY_INSERT 并插入 sys_post 数据
SET IDENTITY_INSERT sys_post ON;

INSERT INTO sys_post (post_id, post_code, post_name, post_sort, status, create_by, create_time, update_by, update_time, remark)
VALUES
    (1, 'ceo', '董事长', 1, '0', 'admin', GETDATE(), '', NULL, ''),
    (2, 'se', '项目经理', 2, '0', 'admin', GETDATE(), '', NULL, ''),
    (3, 'hr', '人力资源', 3, '0', 'admin', GETDATE(), '', NULL, ''),
    (4, 'user', '普通员工', 4, '0', 'admin', GETDATE(), '', NULL, '');

-- 禁用 IDENTITY_INSERT
SET IDENTITY_INSERT sys_post OFF;

-- ----------------------------
-- 4、角色信息表
-- ----------------------------
-- 删除并重新创建 sys_role 表
IF OBJECT_ID('sys_role', 'U') IS NOT NULL
DROP TABLE sys_role;

create table sys_role
(
    role_id             bigint identity (100, 1)
        primary key,
    role_name           nvarchar(30)  not null,
    role_key            nvarchar(100) not null,
    role_sort           int           not null,
    data_scope          char          default '1',
    menu_check_strictly tinyint       default 1,
    dept_check_strictly tinyint       default 1,
    status              char          not null,
    del_flag            char          default '0',
    create_by           nvarchar(64)  default '',
    create_time         datetime,
    update_by           nvarchar(64)  default '',
    update_time         datetime,
    remark              nvarchar(500) default NULL
)

exec sp_addextendedproperty 'MS_Description', N'角色信息表', 'SCHEMA', 'dbo', 'TABLE', 'sys_role'
exec sp_addextendedproperty 'MS_Description', N'角色ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_role', 'COLUMN', 'role_id'
exec sp_addextendedproperty 'MS_Description', N'角色名称', 'SCHEMA', 'dbo', 'TABLE', 'sys_role', 'COLUMN', 'role_name'
exec sp_addextendedproperty 'MS_Description', N'角色权限字符串', 'SCHEMA', 'dbo', 'TABLE', 'sys_role', 'COLUMN',
     'role_key'
exec sp_addextendedproperty 'MS_Description', N'显示顺序', 'SCHEMA', 'dbo', 'TABLE', 'sys_role', 'COLUMN', 'role_sort'
exec sp_addextendedproperty 'MS_Description',
     N'数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）', 'SCHEMA', 'dbo', 'TABLE',
     'sys_role', 'COLUMN', 'data_scope'
exec sp_addextendedproperty 'MS_Description', N'菜单树选择项是否关联显示', 'SCHEMA', 'dbo', 'TABLE', 'sys_role',
     'COLUMN', 'menu_check_strictly'
exec sp_addextendedproperty 'MS_Description', N'部门树选择项是否关联显示', 'SCHEMA', 'dbo', 'TABLE', 'sys_role',
     'COLUMN', 'dept_check_strictly'
exec sp_addextendedproperty 'MS_Description', N'角色状态（0正常 1停用）', 'SCHEMA', 'dbo', 'TABLE', 'sys_role', 'COLUMN',
     'status'
exec sp_addextendedproperty 'MS_Description', N'删除标志（0代表存在 2代表删除）', 'SCHEMA', 'dbo', 'TABLE', 'sys_role',
     'COLUMN', 'del_flag'
exec sp_addextendedproperty 'MS_Description', N'创建者', 'SCHEMA', 'dbo', 'TABLE', 'sys_role', 'COLUMN', 'create_by'
exec sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_role', 'COLUMN', 'create_time'
exec sp_addextendedproperty 'MS_Description', N'更新者', 'SCHEMA', 'dbo', 'TABLE', 'sys_role', 'COLUMN', 'update_by'
exec sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_role', 'COLUMN', 'update_time'
exec sp_addextendedproperty 'MS_Description', N'备注', 'SCHEMA', 'dbo', 'TABLE', 'sys_role', 'COLUMN', 'remark'

-- 启用 IDENTITY_INSERT 并插入 sys_role 数据
SET IDENTITY_INSERT sys_role ON;

INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, update_by, update_time, remark)
VALUES
    (1, '超级管理员', 'admin', 1, 1, 1, 1, '0', '0', 'admin', GETDATE(), '', NULL, '超级管理员'),
    (2, '普通角色', 'common', 2, 2, 1, 1, '0', '0', 'admin', GETDATE(), '', NULL, '普通角色');

-- 禁用 IDENTITY_INSERT
SET IDENTITY_INSERT sys_role OFF;

-- ----------------------------
-- 5、菜单权限表
-- ----------------------------
-- 删除并重新创建 sys_menu 表
IF OBJECT_ID('sys_menu', 'U') IS NOT NULL
DROP TABLE sys_menu;

create table sys_menu
(
    menu_id     bigint identity (2000, 1)
        primary key,
    menu_name   nvarchar(50) not null,
    parent_id   bigint        default 0,
    order_num   int           default 0,
    path        nvarchar(200) default '',
    component   nvarchar(255) default NULL,
    query       nvarchar(255) default NULL,
    route_name  nvarchar(50)  default '',
    is_frame    int           default 1,
    is_cache    int           default 0,
    menu_type   char          default '',
    visible     char          default '0',
    status      char          default '0',
    perms       nvarchar(100) default NULL,
    icon        nvarchar(100) default '#',
    create_by   nvarchar(64)  default '',
    create_time datetime,
    update_by   nvarchar(64)  default '',
    update_time datetime,
    remark      nvarchar(500) default ''
)

exec sp_addextendedproperty 'MS_Description', N'菜单权限表', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu'
exec sp_addextendedproperty 'MS_Description', N'菜单ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu', 'COLUMN', 'menu_id'
exec sp_addextendedproperty 'MS_Description', N'菜单名称', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu', 'COLUMN', 'menu_name'
exec sp_addextendedproperty 'MS_Description', N'父菜单ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu', 'COLUMN', 'parent_id'
exec sp_addextendedproperty 'MS_Description', N'显示顺序', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu', 'COLUMN', 'order_num'
exec sp_addextendedproperty 'MS_Description', N'路由地址', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu', 'COLUMN', 'path'
exec sp_addextendedproperty 'MS_Description', N'组件路径', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu', 'COLUMN', 'component'
exec sp_addextendedproperty 'MS_Description', N'路由参数', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu', 'COLUMN', 'query'
exec sp_addextendedproperty 'MS_Description', N'路由名称', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu', 'COLUMN', 'route_name'
exec sp_addextendedproperty 'MS_Description', N'是否为外链（0是 1否）', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu', 'COLUMN',
     'is_frame'
exec sp_addextendedproperty 'MS_Description', N'是否缓存（0缓存 1不缓存）', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu',
     'COLUMN', 'is_cache'
exec sp_addextendedproperty 'MS_Description', N'菜单类型（M目录 C菜单 F按钮）', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu',
     'COLUMN', 'menu_type'
exec sp_addextendedproperty 'MS_Description', N'菜单状态（0显示 1隐藏）', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu', 'COLUMN',
     'visible'
exec sp_addextendedproperty 'MS_Description', N'菜单状态（0正常 1停用）', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu', 'COLUMN',
     'status'
exec sp_addextendedproperty 'MS_Description', N'权限标识', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu', 'COLUMN', 'perms'
exec sp_addextendedproperty 'MS_Description', N'菜单图标', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu', 'COLUMN', 'icon'
exec sp_addextendedproperty 'MS_Description', N'创建者', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu', 'COLUMN', 'create_by'
exec sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu', 'COLUMN', 'create_time'
exec sp_addextendedproperty 'MS_Description', N'更新者', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu', 'COLUMN', 'update_by'
exec sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu', 'COLUMN', 'update_time'
exec sp_addextendedproperty 'MS_Description', N'备注', 'SCHEMA', 'dbo', 'TABLE', 'sys_menu', 'COLUMN', 'remark'

-- 启用 IDENTITY_INSERT 并插入 sys_menu 数据
SET IDENTITY_INSERT sys_menu ON;

-- 一级菜单
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
    (1, '系统管理', 0, 1, 'system', NULL, '', '', 1, 0, 'M', '0', '0', '', 'system', 'admin', GETDATE(), '', NULL, '系统管理目录'),
    (2, '系统监控', 0, 2, 'monitor', NULL, '', '', 1, 0, 'M', '0', '0', '', 'monitor', 'admin', GETDATE(), '', NULL, '系统监控目录'),
    (3, '系统工具', 0, 3, 'tool', NULL, '', '', 1, 0, 'M', '0', '0', '', 'tool', 'admin', GETDATE(), '', NULL, '系统工具目录'),
    (4, '若依官网', 0, 4, 'http://ruoyi.vip', NULL, '', '', 0, 0, 'M', '0', '0', '', 'guide', 'admin', GETDATE(), '', NULL, '若依官网地址');
-- 二级菜单
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
    (100, '用户管理', 1, 1, 'user', 'system/user/index', '', '', 1, 0, 'C', '0', '0', 'system:user:list', 'user', 'admin', GETDATE(), '', NULL, '用户管理菜单'),
    (101, '角色管理', 1, 2, 'role', 'system/role/index', '', '', 1, 0, 'C', '0', '0', 'system:role:list', 'peoples', 'admin', GETDATE(), '', NULL, '角色管理菜单'),
    (102, '菜单管理', 1, 3, 'menu', 'system/menu/index', '', '', 1, 0, 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', GETDATE(), '', NULL, '菜单管理菜单'),
    (103, '部门管理', 1, 4, 'dept', 'system/dept/index', '', '', 1, 0, 'C', '0', '0', 'system:dept:list', 'tree', 'admin', GETDATE(), '', NULL, '部门管理菜单'),
    (104, '岗位管理', 1, 5, 'post', 'system/post/index', '', '', 1, 0, 'C', '0', '0', 'system:post:list', 'post', 'admin', GETDATE(), '', NULL, '岗位管理菜单'),
    (105, '字典管理', 1, 6, 'dict', 'system/dict/index', '', '', 1, 0, 'C', '0', '0', 'system:dict:list', 'dict', 'admin', GETDATE(), '', NULL, '字典管理菜单'),
    (106, '参数设置', 1, 7, 'config', 'system/config/index', '', '', 1, 0, 'C', '0', '0', 'system:config:list', 'edit', 'admin', GETDATE(), '', NULL, '参数设置菜单'),
    (107, '通知公告', 1, 8, 'notice', 'system/notice/index', '', '', 1, 0, 'C', '0', '0', 'system:notice:list', 'message', 'admin', GETDATE(), '', NULL, '通知公告菜单'),
    (108, '日志管理', 1, 9, 'log', '', '', '', 1, 0, 'M', '0', '0', '', 'log', 'admin', GETDATE(), '', NULL, '日志管理菜单'),
    (109, '在线用户', 2, 1, 'online', 'monitor/online/index', '', '', 1, 0, 'C', '0', '0', 'monitor:online:list', 'online', 'admin', GETDATE(), '', NULL, '在线用户菜单'),
    (110, '定时任务', 2, 2, 'job', 'monitor/job/index', '', '', 1, 0, 'C', '0', '0', 'monitor:job:list', 'job', 'admin', GETDATE(), '', NULL, '定时任务菜单'),
    (111, '数据监控', 2, 3, 'druid', 'monitor/druid/index', '', '', 1, 0, 'C', '0', '0', 'monitor:druid:list', 'druid', 'admin', GETDATE(), '', NULL, '数据监控菜单'),
    (112, '服务监控', 2, 4, 'server', 'monitor/server/index', '', '', 1, 0, 'C', '0', '0', 'monitor:server:list', 'server', 'admin', GETDATE(), '', NULL, '服务监控菜单'),
    (113, '缓存监控', 2, 5, 'cache', 'monitor/cache/index', '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', GETDATE(), '', NULL, '缓存监控菜单'),
    (114, '缓存列表', 2, 6, 'cacheList', 'monitor/cache/list', '', '', 1, 0, 'C', '0', '0', 'monitor:cache:list', 'redis-list', 'admin', GETDATE(), '', NULL, '缓存列表菜单'),
    (115, '表单构建', 3, 1, 'build', 'tool/build/index', '', '', 1, 0, 'C', '0', '0', 'tool:build:list', 'build', 'admin', GETDATE(), '', NULL, '表单构建菜单'),
    (116, '代码生成', 3, 2, 'gen', 'tool/gen/index', '', '', 1, 0, 'C', '0', '0', 'tool:gen:list', 'code', 'admin', GETDATE(), '', NULL, '代码生成菜单'),
    (117, '系统接口', 3, 3, 'swagger', 'tool/swagger/index', '', '', 1, 0, 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', GETDATE(), '', NULL, '系统接口菜单');
-- 三级菜单
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
    (500, '操作日志', 108, 1, 'operlog', 'monitor/operlog/index', '', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', GETDATE(), '', NULL, '操作日志菜单'),
    (501, '登录日志', 108, 2, 'logininfor', 'monitor/logininfor/index', '', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', GETDATE(), '', NULL, '登录日志菜单');
-- 用户管理按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
    (1000, '用户查询', 100, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:query', '#', 'admin', GETDATE(), '', NULL, ''),
    (1001, '用户新增', 100, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:add', '#', 'admin', GETDATE(), '', NULL, ''),
    (1002, '用户修改', 100, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:edit', '#', 'admin', GETDATE(), '', NULL, ''),
    (1003, '用户删除', 100, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:remove', '#', 'admin', GETDATE(), '', NULL, ''),
    (1004, '用户导出', 100, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:export', '#', 'admin', GETDATE(), '', NULL, ''),
    (1005, '用户导入', 100, 6, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:import', '#', 'admin', GETDATE(), '', NULL, ''),
    (1006, '重置密码', 100, 7, '', '', '', '', 1, 0, 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', GETDATE(), '', NULL, '');
-- 角色管理按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
    (1007, '角色查询', 101, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:query', '#', 'admin', GETDATE(), '', NULL, ''),
    (1008, '角色新增', 101, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:add', '#', 'admin', GETDATE(), '', NULL, ''),
    (1009, '角色修改', 101, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:edit', '#', 'admin', GETDATE(), '', NULL, ''),
    (1010, '角色删除', 101, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:remove', '#', 'admin', GETDATE(), '', NULL, ''),
    (1011, '角色导出', 101, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:role:export', '#', 'admin', GETDATE(), '', NULL, '');
-- 菜单管理按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
    (1012, '菜单查询', 102, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:query', '#', 'admin', GETDATE(), '', NULL, ''),
    (1013, '菜单新增', 102, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:add', '#', 'admin', GETDATE(), '', NULL, ''),
    (1014, '菜单修改', 102, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:edit', '#', 'admin', GETDATE(), '', NULL, ''),
    (1015, '菜单删除', 102, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:menu:remove', '#', 'admin', GETDATE(), '', NULL, '');
-- 部门管理按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
    (1016, '部门查询', 103, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:query', '#', 'admin', GETDATE(), '', NULL, ''),
    (1017, '部门新增', 103, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:add', '#', 'admin', GETDATE(), '', NULL, ''),
    (1018, '部门修改', 103, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:edit', '#', 'admin', GETDATE(), '', NULL, ''),
    (1019, '部门删除', 103, 4,  '', '', '', '', 1, 0, 'F', '0', '0', 'system:dept:remove', '#', 'admin', GETDATE(), '', null, '');
-- 岗位管理按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
    (1020, '岗位查询', 104, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:query', '#', 'admin', GETDATE(), '', NULL, ''),
    (1021, '岗位新增', 104, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:add', '#', 'admin', GETDATE(), '', NULL, ''),
    (1022, '岗位修改', 104, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:edit', '#', 'admin', GETDATE(), '', NULL, ''),
    (1023, '岗位删除', 104, 4, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:remove', '#', 'admin', GETDATE(), '', NULL, ''),
    (1024, '岗位导出', 104, 5, '', '', '', '', 1, 0, 'F', '0', '0', 'system:post:export', '#', 'admin', GETDATE(), '', NULL, '');
-- 字典管理按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
    (1025, '字典查询', 105, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:query', '#', 'admin', GETDATE(), '', NULL, ''),
    (1026, '字典新增', 105, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:add', '#', 'admin', GETDATE(), '', NULL, ''),
    (1027, '字典修改', 105, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:edit', '#', 'admin', GETDATE(), '', NULL, ''),
    (1028, '字典删除', 105, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:remove', '#', 'admin', GETDATE(), '', NULL, ''),
    (1029, '字典导出', 105, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:dict:export', '#', 'admin', GETDATE(), '', NULL, '');
-- 参数设置按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
    (1030, '参数查询', 106, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:query', '#', 'admin', GETDATE(), '', NULL, ''),
    (1031, '参数新增', 106, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:add', '#', 'admin', GETDATE(), '', NULL, ''),
    (1032, '参数修改', 106, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:edit', '#', 'admin', GETDATE(), '', NULL, ''),
    (1033, '参数删除', 106, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:remove', '#', 'admin', GETDATE(), '', NULL, ''),
    (1034, '参数导出', 106, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:config:export', '#', 'admin', GETDATE(), '', NULL, '');
-- 通知公告按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
    (1035, '公告查询', 107, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:query', '#', 'admin', GETDATE(), '', NULL, ''),
    (1036, '公告新增', 107, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:add', '#', 'admin', GETDATE(), '', NULL, ''),
    (1037, '公告修改', 107, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:edit', '#', 'admin', GETDATE(), '', NULL, ''),
    (1038, '公告删除', 107, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'system:notice:remove', '#', 'admin', GETDATE(), '', NULL, '');
-- 操作日志按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
    (1039, '操作查询', 500, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', GETDATE(), '', NULL, ''),
    (1040, '操作删除', 500, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', GETDATE(), '', NULL, ''),
    (1041, '日志导出', 500, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', GETDATE(), '', NULL, '');
-- 登录日志按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
    (1042, '登录查询', 501, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', GETDATE(), '', NULL, ''),
    (1043, '登录删除', 501, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', GETDATE(), '', NULL, ''),
    (1044, '日志导出', 501, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', GETDATE(), '', NULL, ''),
    (1045, '账户解锁', 501, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:logininfor:unlock', '#', 'admin', GETDATE(), '', NULL, '');
-- 在线用户按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
    (1046, '在线查询', 109, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:query', '#', 'admin', GETDATE(), '', NULL, ''),
    (1047, '批量强退', 109, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', GETDATE(), '', NULL, ''),
    (1048, '单条强退', 109, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', GETDATE(), '', NULL, '');
-- 定时任务按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
    (1049, '任务查询', 110, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:query', '#', 'admin', GETDATE(), '', NULL, ''),
    (1050, '任务新增', 110, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:add', '#', 'admin', GETDATE(), '', NULL, ''),
    (1051, '任务修改', 110, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:edit', '#', 'admin', GETDATE(), '', NULL, ''),
    (1052, '任务删除', 110, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:remove', '#', 'admin', GETDATE(), '', NULL, ''),
    (1053, '状态修改', 110, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', GETDATE(), '', NULL, ''),
    (1054, '任务导出', 110, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'monitor:job:export', '#', 'admin', GETDATE(), '', NULL, '');
-- 代码生成按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES
    (1055, '生成查询', 116, 1, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:query', '#', 'admin', GETDATE(), '', NULL, ''),
    (1056, '生成修改', 116, 2, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:edit', '#', 'admin', GETDATE(), '', NULL, ''),
    (1057, '生成删除', 116, 3, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:remove', '#', 'admin', GETDATE(), '', NULL, ''),
    (1058, '导入代码', 116, 4, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:import', '#', 'admin', GETDATE(), '', NULL, ''),
    (1059, '预览代码', 116, 5, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:preview', '#', 'admin', GETDATE(), '', NULL, ''),
    (1060, '生成代码', 116, 6, '#', '', '', '', 1, 0, 'F', '0', '0', 'tool:gen:code', '#', 'admin', GETDATE(), '', NULL, '');

-- 禁用 IDENTITY_INSERT
SET IDENTITY_INSERT sys_menu OFF;

-- ----------------------------
-- 6、用户和角色关联表  用户N-1角色
-- ----------------------------
IF OBJECT_ID('sys_user_role', 'U') IS NOT NULL
DROP TABLE sys_user_role;

create table sys_user_role
(
    user_id bigint not null,
    role_id bigint not null,
    primary key (user_id, role_id)
)

exec sp_addextendedproperty 'MS_Description', N'用户和角色关联表', 'SCHEMA', 'dbo', 'TABLE', 'sys_user_role'
exec sp_addextendedproperty 'MS_Description', N'用户ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_user_role', 'COLUMN', 'user_id'
exec sp_addextendedproperty 'MS_Description', N'角色ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_user_role', 'COLUMN', 'role_id'

-- 初始化-用户和角色关联表数据
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO sys_user_role (user_id, role_id) VALUES (2, 2);

-- ----------------------------
-- 7、角色和菜单关联表  角色1-N菜单
-- ----------------------------
-- 删除并重新创建 sys_role_menu 表
IF OBJECT_ID('sys_role_menu', 'U') IS NOT NULL
DROP TABLE sys_role_menu;

create table sys_role_menu
(
    role_id bigint not null,
    menu_id bigint not null,
    primary key (role_id, menu_id)
)

exec sp_addextendedproperty 'MS_Description', N'角色和菜单关联表', 'SCHEMA', 'dbo', 'TABLE', 'sys_role_menu'
exec sp_addextendedproperty 'MS_Description', N'角色ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_role_menu', 'COLUMN', 'role_id'
exec sp_addextendedproperty 'MS_Description', N'菜单ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_role_menu', 'COLUMN', 'menu_id'

-- 插入初始数据到 sys_role_menu 表
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 2);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 3);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 4);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 100);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 101);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 102);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 103);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 104);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 105);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 106);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 107);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 108);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 109);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 110);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 111);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 112);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 113);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 114);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 115);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 116);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 117);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 500);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 501);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1000);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1001);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1002);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1003);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1004);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1005);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1006);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1007);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1008);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1009);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1010);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1011);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1012);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1013);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1014);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1015);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1016);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1017);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1018);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1019);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1020);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1021);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1022);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1023);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1024);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1025);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1026);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1027);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1028);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1029);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1030);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1031);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1032);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1033);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1034);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1035);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1036);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1037);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1038);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1039);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1040);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1041);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1042);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1043);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1044);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1045);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1046);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1047);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1048);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1049);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1050);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1051);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1052);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1053);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1054);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1055);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1056);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1057);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1058);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1059);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (2, 1060);

-- ----------------------------
-- 8、角色和部门关联表  角色1-N部门
-- ----------------------------
-- 删除并重新创建 sys_role_dept 表
IF OBJECT_ID('sys_role_dept', 'U') IS NOT NULL
DROP TABLE sys_role_dept;

create table sys_role_dept
(
    role_id bigint not null,
    dept_id bigint not null,
    primary key (role_id, dept_id)
)

exec sp_addextendedproperty 'MS_Description', N'角色和部门关联表', 'SCHEMA', 'dbo', 'TABLE', 'sys_role_dept'
exec sp_addextendedproperty 'MS_Description', N'角色ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_role_dept', 'COLUMN', 'role_id'
exec sp_addextendedproperty 'MS_Description', N'部门ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_role_dept', 'COLUMN', 'dept_id'

-- 插入初始数据到 sys_role_dept 表
INSERT INTO sys_role_dept (role_id, dept_id) VALUES (2, 100);
INSERT INTO sys_role_dept (role_id, dept_id) VALUES (2, 101);
INSERT INTO sys_role_dept (role_id, dept_id) VALUES (2, 105);

-- ----------------------------
-- 9、用户与岗位关联表  用户1-N岗位
-- ----------------------------
-- 删除并重新创建 sys_user_post 表
IF OBJECT_ID('sys_user_post', 'U') IS NOT NULL
DROP TABLE sys_user_post;

create table sys_user_post
(
    user_id bigint not null,
    post_id bigint not null,
    primary key (user_id, post_id)
)

exec sp_addextendedproperty 'MS_Description', N'用户与岗位关联表', 'SCHEMA', 'dbo', 'TABLE', 'sys_user_post'
exec sp_addextendedproperty 'MS_Description', N'用户ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_user_post', 'COLUMN', 'user_id'
exec sp_addextendedproperty 'MS_Description', N'岗位ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_user_post', 'COLUMN', 'post_id'

-- 插入初始数据到 sys_user_post 表
INSERT INTO sys_user_post (user_id, post_id) VALUES (1, 1);
INSERT INTO sys_user_post (user_id, post_id) VALUES (2, 2);

-- ----------------------------
-- 10、操作日志记录
-- ----------------------------
-- 删除并重新创建 sys_oper_log 表
IF OBJECT_ID('sys_oper_log', 'U') IS NOT NULL
DROP TABLE sys_oper_log;

create table sys_oper_log
(
    oper_id        bigint identity (100, 1)
        primary key,
    title          nvarchar(50)   default '',
    business_type  int            default 0,
    method         nvarchar(200)  default '',
    request_method nvarchar(10)   default '',
    operator_type  int            default 0,
    oper_name      nvarchar(50)   default '',
    dept_name      nvarchar(50)   default '',
    oper_url       nvarchar(255)  default '',
    oper_ip        nvarchar(128)  default '',
    oper_location  nvarchar(255)  default '',
    oper_param     nvarchar(2000) default '',
    json_result    nvarchar(2000) default '',
    status         int            default 0,
    error_msg      nvarchar(2000) default '',
    oper_time      datetime,
    cost_time      bigint         default 0
)

exec sp_addextendedproperty 'MS_Description', N'操作日志记录', 'SCHEMA', 'dbo', 'TABLE', 'sys_oper_log'
exec sp_addextendedproperty 'MS_Description', N'日志主键', 'SCHEMA', 'dbo', 'TABLE', 'sys_oper_log', 'COLUMN', 'oper_id'
exec sp_addextendedproperty 'MS_Description', N'模块标题', 'SCHEMA', 'dbo', 'TABLE', 'sys_oper_log', 'COLUMN', 'title'
exec sp_addextendedproperty 'MS_Description', N'业务类型（0其它 1新增 2修改 3删除）', 'SCHEMA', 'dbo', 'TABLE',
     'sys_oper_log', 'COLUMN', 'business_type'
exec sp_addextendedproperty 'MS_Description', N'方法名称', 'SCHEMA', 'dbo', 'TABLE', 'sys_oper_log', 'COLUMN', 'method'
exec sp_addextendedproperty 'MS_Description', N'请求方式', 'SCHEMA', 'dbo', 'TABLE', 'sys_oper_log', 'COLUMN',
     'request_method'
exec sp_addextendedproperty 'MS_Description', N'操作类别（0其它 1后台用户 2手机端用户）', 'SCHEMA', 'dbo', 'TABLE',
     'sys_oper_log', 'COLUMN', 'operator_type'
exec sp_addextendedproperty 'MS_Description', N'操作人员', 'SCHEMA', 'dbo', 'TABLE', 'sys_oper_log', 'COLUMN',
     'oper_name'
exec sp_addextendedproperty 'MS_Description', N'部门名称', 'SCHEMA', 'dbo', 'TABLE', 'sys_oper_log', 'COLUMN',
     'dept_name'
exec sp_addextendedproperty 'MS_Description', N'请求URL', 'SCHEMA', 'dbo', 'TABLE', 'sys_oper_log', 'COLUMN', 'oper_url'
exec sp_addextendedproperty 'MS_Description', N'主机地址', 'SCHEMA', 'dbo', 'TABLE', 'sys_oper_log', 'COLUMN', 'oper_ip'
exec sp_addextendedproperty 'MS_Description', N'操作地点', 'SCHEMA', 'dbo', 'TABLE', 'sys_oper_log', 'COLUMN',
     'oper_location'
exec sp_addextendedproperty 'MS_Description', N'请求参数', 'SCHEMA', 'dbo', 'TABLE', 'sys_oper_log', 'COLUMN',
     'oper_param'
exec sp_addextendedproperty 'MS_Description', N'返回参数', 'SCHEMA', 'dbo', 'TABLE', 'sys_oper_log', 'COLUMN',
     'json_result'
exec sp_addextendedproperty 'MS_Description', N'操作状态（0正常 1异常）', 'SCHEMA', 'dbo', 'TABLE', 'sys_oper_log',
     'COLUMN', 'status'
exec sp_addextendedproperty 'MS_Description', N'错误消息', 'SCHEMA', 'dbo', 'TABLE', 'sys_oper_log', 'COLUMN',
     'error_msg'
exec sp_addextendedproperty 'MS_Description', N'操作时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_oper_log', 'COLUMN',
     'oper_time'
exec sp_addextendedproperty 'MS_Description', N'消耗时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_oper_log', 'COLUMN',
     'cost_time'

-- 创建索引
create index idx_sys_oper_log_bt
    on sys_oper_log (business_type)
create index idx_sys_oper_log_s
    on sys_oper_log (status)
create index idx_sys_oper_log_ot
    on sys_oper_log (oper_time)

-- ----------------------------
-- 11、字典类型表
-- ----------------------------
-- 删除并重新创建 sys_dict_type 表
IF OBJECT_ID('sys_dict_type', 'U') IS NOT NULL
DROP TABLE sys_dict_type;

create table sys_dict_type
(
    dict_id     bigint identity (100, 1)
        primary key,
    dict_name   nvarchar(100) default '',
    dict_type   nvarchar(100) default ''
        constraint UQ_sys_dict_type
            unique,
    status      char          default '0',
    create_by   nvarchar(64)  default '',
    create_time datetime,
    update_by   nvarchar(64)  default '',
    update_time datetime,
    remark      nvarchar(500) default NULL
)

exec sp_addextendedproperty 'MS_Description', N'字典类型表', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_type'
exec sp_addextendedproperty 'MS_Description', N'字典主键', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_type', 'COLUMN',
     'dict_id'
exec sp_addextendedproperty 'MS_Description', N'字典名称', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_type', 'COLUMN',
     'dict_name'
exec sp_addextendedproperty 'MS_Description', N'字典类型', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_type', 'COLUMN',
     'dict_type'
exec sp_addextendedproperty 'MS_Description', N'状态（0正常 1停用）', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_type', 'COLUMN',
     'status'
exec sp_addextendedproperty 'MS_Description', N'创建者', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_type', 'COLUMN',
     'create_by'
exec sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_type', 'COLUMN',
     'create_time'
exec sp_addextendedproperty 'MS_Description', N'更新者', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_type', 'COLUMN',
     'update_by'
exec sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_type', 'COLUMN',
     'update_time'
exec sp_addextendedproperty 'MS_Description', N'备注', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_type', 'COLUMN', 'remark'

-- 插入初始数据到 sys_dict_type 表
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES ('用户性别', 'sys_user_sex', '0', 'admin', GETDATE(), '', NULL, '用户性别列表');
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES ('菜单状态', 'sys_show_hide', '0', 'admin', GETDATE(), '', NULL, '菜单状态列表');
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES ('系统开关', 'sys_normal_disable', '0', 'admin', GETDATE(), '', NULL, '系统开关列表');
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES ('任务状态', 'sys_job_status', '0', 'admin', GETDATE(), '', NULL, '任务状态列表');
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES ('任务分组', 'sys_job_group', '0', 'admin', GETDATE(), '', NULL, '任务分组列表');
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES ('系统是否', 'sys_yes_no', '0', 'admin', GETDATE(), '', NULL, '系统是否列表');
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES ('通知类型', 'sys_notice_type', '0', 'admin', GETDATE(), '', NULL, '通知类型列表');
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES ('通知状态', 'sys_notice_status', '0', 'admin', GETDATE(), '', NULL, '通知状态列表');
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES ('操作类型', 'sys_oper_type', '0', 'admin', GETDATE(), '', NULL, '操作类型列表');
INSERT INTO sys_dict_type (dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
VALUES ('系统状态', 'sys_common_status', '0', 'admin', GETDATE(), '', NULL, '登录状态列表');

-- ----------------------------
-- 12、字典数据表
-- ----------------------------
-- 删除并重新创建 sys_dict_data 表
IF OBJECT_ID('sys_dict_data', 'U') IS NOT NULL
DROP TABLE sys_dict_data;

create table sys_dict_data
(
    dict_code   bigint identity (100, 1)
        primary key,
    dict_sort   int           default 0,
    dict_label  nvarchar(100) default '',
    dict_value  nvarchar(100) default '',
    dict_type   nvarchar(100) default '',
    css_class   nvarchar(100) default NULL,
    list_class  nvarchar(100) default NULL,
    is_default  char          default 'N',
    status      char          default '0',
    create_by   nvarchar(64)  default '',
    create_time datetime,
    update_by   nvarchar(64)  default '',
    update_time datetime,
    remark      nvarchar(500) default NULL
)

exec sp_addextendedproperty 'MS_Description', N'字典数据表', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_data'
exec sp_addextendedproperty 'MS_Description', N'字典编码', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_data', 'COLUMN',
     'dict_code'
exec sp_addextendedproperty 'MS_Description', N'字典排序', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_data', 'COLUMN',
     'dict_sort'
exec sp_addextendedproperty 'MS_Description', N'字典标签', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_data', 'COLUMN',
     'dict_label'
exec sp_addextendedproperty 'MS_Description', N'字典键值', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_data', 'COLUMN',
     'dict_value'
exec sp_addextendedproperty 'MS_Description', N'字典类型', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_data', 'COLUMN',
     'dict_type'
exec sp_addextendedproperty 'MS_Description', N'样式属性（其他样式扩展）', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_data',
     'COLUMN', 'css_class'
exec sp_addextendedproperty 'MS_Description', N'表格回显样式', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_data', 'COLUMN',
     'list_class'
exec sp_addextendedproperty 'MS_Description', N'是否默认（Y是 N否）', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_data', 'COLUMN',
     'is_default'
exec sp_addextendedproperty 'MS_Description', N'状态（0正常 1停用）', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_data', 'COLUMN',
     'status'
exec sp_addextendedproperty 'MS_Description', N'创建者', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_data', 'COLUMN',
     'create_by'
exec sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_data', 'COLUMN',
     'create_time'
exec sp_addextendedproperty 'MS_Description', N'更新者', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_data', 'COLUMN',
     'update_by'
exec sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_data', 'COLUMN',
     'update_time'
exec sp_addextendedproperty 'MS_Description', N'备注', 'SCHEMA', 'dbo', 'TABLE', 'sys_dict_data', 'COLUMN', 'remark'

-- 插入初始数据到 sys_dict_data 表
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (1, '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', GETDATE(), '', NULL, '性别男');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (2, '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', GETDATE(), '', NULL, '性别女');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (3, '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', GETDATE(), '', NULL, '性别未知');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (1, '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', GETDATE(), '', NULL, '显示菜单');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (2, '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', GETDATE(), '', NULL, '隐藏菜单');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (1, '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', GETDATE(), '', NULL, '正常状态');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (2, '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', GETDATE(), '', NULL, '停用状态');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (1, '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', GETDATE(), '', NULL, '正常状态');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (2, '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', GETDATE(), '', NULL, '停用状态');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (1, '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', GETDATE(), '', NULL, '默认分组');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (2, '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', GETDATE(), '', NULL, '系统分组');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (1, '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', GETDATE(), '', NULL, '系统默认是');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (2, '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', GETDATE(), '', NULL, '系统默认否');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (1, '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', GETDATE(), '', NULL, '通知');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (2, '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', GETDATE(), '', NULL, '公告');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (1, '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', GETDATE(), '', NULL, '正常状态');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (2, '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', GETDATE(), '', NULL, '关闭状态');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (99, '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', GETDATE(), '', NULL, '其他操作');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (1, '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', GETDATE(), '', NULL, '新增操作');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (2, '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', GETDATE(), '', NULL, '修改操作');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (3, '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', GETDATE(), '', NULL, '删除操作');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (4, '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', GETDATE(), '', NULL, '授权操作');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (5, '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', GETDATE(), '', NULL, '导出操作');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (6, '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', GETDATE(), '', NULL, '导入操作');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (7, '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', GETDATE(), '', NULL, '强退操作');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (8, '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', GETDATE(), '', NULL, '生成操作');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (9, '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', GETDATE(), '', NULL, '清空操作');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (1, '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', GETDATE(), '', NULL, '正常状态');
INSERT INTO sys_dict_data (dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
VALUES (2, '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', GETDATE(), '', NULL, '停用状态');

-- ----------------------------
-- 13、参数配置表
-- ----------------------------
-- 删除并重新创建 sys_config 表
IF OBJECT_ID('sys_config', 'U') IS NOT NULL
DROP TABLE sys_config;

create table sys_config
(
    config_id    int identity (100, 1)
        primary key,
    config_name  nvarchar(100) default '',
    config_key   nvarchar(100) default '',
    config_value nvarchar(500) default '',
    config_type  char          default 'N',
    create_by    nvarchar(64)  default '',
    create_time  datetime,
    update_by    nvarchar(64)  default '',
    update_time  datetime,
    remark       nvarchar(500) default NULL
)

exec sp_addextendedproperty 'MS_Description', N'参数配置表', 'SCHEMA', 'dbo', 'TABLE', 'sys_config'
exec sp_addextendedproperty 'MS_Description', N'参数主键', 'SCHEMA', 'dbo', 'TABLE', 'sys_config', 'COLUMN', 'config_id'
exec sp_addextendedproperty 'MS_Description', N'参数名称', 'SCHEMA', 'dbo', 'TABLE', 'sys_config', 'COLUMN',
     'config_name'
exec sp_addextendedproperty 'MS_Description', N'参数键名', 'SCHEMA', 'dbo', 'TABLE', 'sys_config', 'COLUMN',
     'config_key'
exec sp_addextendedproperty 'MS_Description', N'参数键值', 'SCHEMA', 'dbo', 'TABLE', 'sys_config', 'COLUMN',
     'config_value'
exec sp_addextendedproperty 'MS_Description', N'系统内置（Y是 N否）', 'SCHEMA', 'dbo', 'TABLE', 'sys_config', 'COLUMN',
     'config_type'
exec sp_addextendedproperty 'MS_Description', N'创建者', 'SCHEMA', 'dbo', 'TABLE', 'sys_config', 'COLUMN', 'create_by'
exec sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_config', 'COLUMN',
     'create_time'
exec sp_addextendedproperty 'MS_Description', N'更新者', 'SCHEMA', 'dbo', 'TABLE', 'sys_config', 'COLUMN', 'update_by'
exec sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_config', 'COLUMN',
     'update_time'
exec sp_addextendedproperty 'MS_Description', N'备注', 'SCHEMA', 'dbo', 'TABLE', 'sys_config', 'COLUMN', 'remark'

-- 插入初始数据到 sys_config 表
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark)
VALUES ('主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', GETDATE(), '', NULL, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark)
VALUES ('用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', GETDATE(), '', NULL, '初始化密码 123456');
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark)
VALUES ('主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', GETDATE(), '', NULL, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark)
VALUES ('账号自助-验证码开关', 'sys.account.captchaEnabled', 'true', 'Y', 'admin', GETDATE(), '', NULL, '是否开启验证码功能（true开启，false关闭）');
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark)
VALUES ('账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 'admin', GETDATE(), '', NULL, '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO sys_config (config_name, config_key, config_value, config_type, create_by, create_time, update_by, update_time, remark)
VALUES ('用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', 'admin', GETDATE(), '', NULL, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');

-- ----------------------------
-- 14、系统访问记录
-- ----------------------------
-- 删除并重新创建 sys_logininfor 表
IF OBJECT_ID('sys_logininfor', 'U') IS NOT NULL
DROP TABLE sys_logininfor;

create table sys_logininfor
(
    info_id        bigint identity (100, 1)
        primary key,
    user_name      nvarchar(50)  default '',
    ipaddr         nvarchar(128) default '',
    login_location nvarchar(255) default '',
    browser        nvarchar(50)  default '',
    os             nvarchar(50)  default '',
    status         char          default '0',
    msg            nvarchar(255) default '',
    login_time     datetime
)

exec sp_addextendedproperty 'MS_Description', N'系统访问记录', 'SCHEMA', 'dbo', 'TABLE', 'sys_logininfor'
exec sp_addextendedproperty 'MS_Description', N'访问ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_logininfor', 'COLUMN', 'info_id'
exec sp_addextendedproperty 'MS_Description', N'用户账号', 'SCHEMA', 'dbo', 'TABLE', 'sys_logininfor', 'COLUMN',
     'user_name'
exec sp_addextendedproperty 'MS_Description', N'登录IP地址', 'SCHEMA', 'dbo', 'TABLE', 'sys_logininfor', 'COLUMN',
     'ipaddr'
exec sp_addextendedproperty 'MS_Description', N'登录地点', 'SCHEMA', 'dbo', 'TABLE', 'sys_logininfor', 'COLUMN',
     'login_location'
exec sp_addextendedproperty 'MS_Description', N'浏览器类型', 'SCHEMA', 'dbo', 'TABLE', 'sys_logininfor', 'COLUMN',
     'browser'
exec sp_addextendedproperty 'MS_Description', N'操作系统', 'SCHEMA', 'dbo', 'TABLE', 'sys_logininfor', 'COLUMN', 'os'
exec sp_addextendedproperty 'MS_Description', N'登录状态（0成功 1失败）', 'SCHEMA', 'dbo', 'TABLE', 'sys_logininfor',
     'COLUMN', 'status'
exec sp_addextendedproperty 'MS_Description', N'提示消息', 'SCHEMA', 'dbo', 'TABLE', 'sys_logininfor', 'COLUMN', 'msg'
exec sp_addextendedproperty 'MS_Description', N'访问时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_logininfor', 'COLUMN',
     'login_time'

-- 创建索引
create index idx_sys_logininfor_s
    on sys_logininfor (status)
create index idx_sys_logininfor_lt
    on sys_logininfor (login_time)

-- ----------------------------
-- 15、定时任务调度表
-- ----------------------------
-- 删除并重新创建 sys_job 表
IF OBJECT_ID('sys_job', 'U') IS NOT NULL
DROP TABLE sys_job;

create table sys_job
(
    job_id          bigint identity (100, 1)
        primary key,
    job_name        nvarchar(64)  default '',
    job_group       nvarchar(64)  default 'DEFAULT',
    invoke_target   nvarchar(500) not null,
    cron_expression nvarchar(255) default '',
    misfire_policy  nvarchar(20)  default '3',
    concurrent      char          default '1',
    status          char          default '0',
    create_by       nvarchar(64)  default '',
    create_time     datetime,
    update_by       nvarchar(64)  default '',
    update_time     datetime,
    remark          nvarchar(500) default ''
)

exec sp_addextendedproperty 'MS_Description', N'定时任务调度表', 'SCHEMA', 'dbo', 'TABLE', 'sys_job'
exec sp_addextendedproperty 'MS_Description', N'任务ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_job', 'COLUMN', 'job_id'
exec sp_addextendedproperty 'MS_Description', N'任务名称', 'SCHEMA', 'dbo', 'TABLE', 'sys_job', 'COLUMN', 'job_name'
exec sp_addextendedproperty 'MS_Description', N'任务组名', 'SCHEMA', 'dbo', 'TABLE', 'sys_job', 'COLUMN', 'job_group'
exec sp_addextendedproperty 'MS_Description', N'调用目标字符串', 'SCHEMA', 'dbo', 'TABLE', 'sys_job', 'COLUMN',
     'invoke_target'
exec sp_addextendedproperty 'MS_Description', N'cron执行表达式', 'SCHEMA', 'dbo', 'TABLE', 'sys_job', 'COLUMN',
     'cron_expression'
exec sp_addextendedproperty 'MS_Description', N'计划执行错误策略（1立即执行 2执行一次 3放弃执行）', 'SCHEMA', 'dbo',
     'TABLE', 'sys_job', 'COLUMN', 'misfire_policy'
exec sp_addextendedproperty 'MS_Description', N'是否并发执行（0允许 1禁止）', 'SCHEMA', 'dbo', 'TABLE', 'sys_job',
     'COLUMN', 'concurrent'
exec sp_addextendedproperty 'MS_Description', N'状态（0正常 1暂停）', 'SCHEMA', 'dbo', 'TABLE', 'sys_job', 'COLUMN',
     'status'
exec sp_addextendedproperty 'MS_Description', N'创建者', 'SCHEMA', 'dbo', 'TABLE', 'sys_job', 'COLUMN', 'create_by'
exec sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_job', 'COLUMN', 'create_time'
exec sp_addextendedproperty 'MS_Description', N'更新者', 'SCHEMA', 'dbo', 'TABLE', 'sys_job', 'COLUMN', 'update_by'
exec sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_job', 'COLUMN', 'update_time'
exec sp_addextendedproperty 'MS_Description', N'备注信息', 'SCHEMA', 'dbo', 'TABLE', 'sys_job', 'COLUMN', 'remark'

-- 插入初始数据到 sys_job 表
INSERT INTO sys_job (job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, update_by, update_time, remark)
VALUES ('系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', 'admin', GETDATE(), '', NULL, '');
INSERT INTO sys_job (job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, update_by, update_time, remark)
VALUES ('系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(''ry'')', '0/15 * * * * ?', '3', '1', '1', 'admin', GETDATE(), '', NULL, '');
INSERT INTO sys_job (job_name, job_group, invoke_target, cron_expression, misfire_policy, concurrent, status, create_by, create_time, update_by, update_time, remark)
VALUES ('系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(''ry'', 1, 2000, 316.50, 100)', '0/20 * * * * ?', '3', '1', '1', 'admin', GETDATE(), '', NULL, '');

-- ----------------------------
-- 16、定时任务调度日志表
-- ----------------------------
-- 删除并重新创建 sys_job_log 表
IF OBJECT_ID('sys_job_log', 'U') IS NOT NULL
DROP TABLE sys_job_log;

create table sys_job_log
(
    job_log_id     bigint identity
        primary key,
    job_name       nvarchar(64)  not null,
    job_group      nvarchar(64)  not null,
    invoke_target  nvarchar(500) not null,
    job_message    nvarchar(500),
    status         char           default '0',
    exception_info nvarchar(2000) default '',
    create_time    datetime
)

exec sp_addextendedproperty 'MS_Description', N'定时任务调度日志表', 'SCHEMA', 'dbo', 'TABLE', 'sys_job_log'
exec sp_addextendedproperty 'MS_Description', N'任务日志ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_job_log', 'COLUMN',
     'job_log_id'
exec sp_addextendedproperty 'MS_Description', N'任务名称', 'SCHEMA', 'dbo', 'TABLE', 'sys_job_log', 'COLUMN', 'job_name'
exec sp_addextendedproperty 'MS_Description', N'任务组名', 'SCHEMA', 'dbo', 'TABLE', 'sys_job_log', 'COLUMN',
     'job_group'
exec sp_addextendedproperty 'MS_Description', N'调用目标字符串', 'SCHEMA', 'dbo', 'TABLE', 'sys_job_log', 'COLUMN',
     'invoke_target'
exec sp_addextendedproperty 'MS_Description', N'日志信息', 'SCHEMA', 'dbo', 'TABLE', 'sys_job_log', 'COLUMN',
     'job_message'
exec sp_addextendedproperty 'MS_Description', N'执行状态（0正常 1失败）', 'SCHEMA', 'dbo', 'TABLE', 'sys_job_log',
     'COLUMN', 'status'
exec sp_addextendedproperty 'MS_Description', N'异常信息', 'SCHEMA', 'dbo', 'TABLE', 'sys_job_log', 'COLUMN',
     'exception_info'
exec sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_job_log', 'COLUMN',
     'create_time'

-- ----------------------------
-- 17、通知公告表
-- ----------------------------
-- 删除并重新创建 sys_notice 表
IF OBJECT_ID('sys_notice', 'U') IS NOT NULL
DROP TABLE sys_notice;

create table sys_notice
(
    notice_id      int identity (10, 1)
        primary key,
    notice_title   nvarchar(50) not null,
    notice_type    char         not null,
    notice_content nvarchar(max) default NULL,
    status         char          default '0',
    create_by      nvarchar(64)  default '',
    create_time    datetime,
    update_by      nvarchar(64)  default '',
    update_time    datetime,
    remark         nvarchar(255) default NULL
)

exec sp_addextendedproperty 'MS_Description', N'通知公告表', 'SCHEMA', 'dbo', 'TABLE', 'sys_notice'
exec sp_addextendedproperty 'MS_Description', N'公告ID', 'SCHEMA', 'dbo', 'TABLE', 'sys_notice', 'COLUMN', 'notice_id'
exec sp_addextendedproperty 'MS_Description', N'公告标题', 'SCHEMA', 'dbo', 'TABLE', 'sys_notice', 'COLUMN',
     'notice_title'
exec sp_addextendedproperty 'MS_Description', N'公告类型（1通知 2公告）', 'SCHEMA', 'dbo', 'TABLE', 'sys_notice',
     'COLUMN', 'notice_type'
exec sp_addextendedproperty 'MS_Description', N'公告内容', 'SCHEMA', 'dbo', 'TABLE', 'sys_notice', 'COLUMN',
     'notice_content'
exec sp_addextendedproperty 'MS_Description', N'公告状态（0正常 1关闭）', 'SCHEMA', 'dbo', 'TABLE', 'sys_notice',
     'COLUMN', 'status'
exec sp_addextendedproperty 'MS_Description', N'创建者', 'SCHEMA', 'dbo', 'TABLE', 'sys_notice', 'COLUMN', 'create_by'
exec sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_notice', 'COLUMN',
     'create_time'
exec sp_addextendedproperty 'MS_Description', N'更新者', 'SCHEMA', 'dbo', 'TABLE', 'sys_notice', 'COLUMN', 'update_by'
exec sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', 'dbo', 'TABLE', 'sys_notice', 'COLUMN',
     'update_time'
exec sp_addextendedproperty 'MS_Description', N'备注', 'SCHEMA', 'dbo', 'TABLE', 'sys_notice', 'COLUMN', 'remark'

-- 插入初始数据到 sys_notice 表
INSERT INTO sys_notice (notice_title, notice_type, notice_content, status, create_by, create_time, update_by, update_time, remark)
VALUES ('温馨提醒：2018-07-01 若依新版本发布啦', '2', N'新版本内容', '0', 'admin', GETDATE(), '', NULL, N'管理员');
INSERT INTO sys_notice (notice_title, notice_type, notice_content, status, create_by, create_time, update_by, update_time, remark)
VALUES ('维护通知：2018-07-01 若依系统凌晨维护', '1', N'维护内容', '0', 'admin', GETDATE(), '', NULL, N'管理员');

-- ----------------------------
-- 18、代码生成业务表
-- ----------------------------
-- 删除并重新创建 gen_table 表
IF OBJECT_ID('gen_table', 'U') IS NOT NULL
DROP TABLE gen_table;

create table gen_table
(
    table_id          bigint identity
        primary key,
    table_name        nvarchar(200) default '',
    table_comment     nvarchar(500) default '',
    sub_table_name    nvarchar(64)  default NULL,
    sub_table_fk_name nvarchar(64)  default NULL,
    class_name        nvarchar(100) default '',
    tpl_category      nvarchar(200) default 'crud',
    tpl_web_type      nvarchar(30)  default '',
    package_name      nvarchar(100),
    module_name       nvarchar(30),
    business_name     nvarchar(30),
    function_name     nvarchar(50),
    function_author   nvarchar(50),
    gen_type          char          default '0',
    gen_path          nvarchar(200) default '/',
    options           nvarchar(1000),
    create_by         nvarchar(64)  default '',
    create_time       datetime,
    update_by         nvarchar(64)  default '',
    update_time       datetime,
    remark            nvarchar(500) default NULL
)

exec sp_addextendedproperty 'MS_Description', N'代码生成业务表', 'SCHEMA', 'dbo', 'TABLE', 'gen_table'
exec sp_addextendedproperty 'MS_Description', N'编号', 'SCHEMA', 'dbo', 'TABLE', 'gen_table', 'COLUMN', 'table_id'
exec sp_addextendedproperty 'MS_Description', N'表名称', 'SCHEMA', 'dbo', 'TABLE', 'gen_table', 'COLUMN', 'table_name'
exec sp_addextendedproperty 'MS_Description', N'表描述', 'SCHEMA', 'dbo', 'TABLE', 'gen_table', 'COLUMN',
     'table_comment'
exec sp_addextendedproperty 'MS_Description', N'关联子表的表名', 'SCHEMA', 'dbo', 'TABLE', 'gen_table', 'COLUMN',
     'sub_table_name'
exec sp_addextendedproperty 'MS_Description', N'子表关联的外键名', 'SCHEMA', 'dbo', 'TABLE', 'gen_table', 'COLUMN',
     'sub_table_fk_name'
exec sp_addextendedproperty 'MS_Description', N'实体类名称', 'SCHEMA', 'dbo', 'TABLE', 'gen_table', 'COLUMN',
     'class_name'
exec sp_addextendedproperty 'MS_Description', N'使用的模板（crud单表操作 tree树表操作）', 'SCHEMA', 'dbo', 'TABLE',
     'gen_table', 'COLUMN', 'tpl_category'
exec sp_addextendedproperty 'MS_Description', N'前端模板类型（element-ui模版 element-plus模版）', 'SCHEMA', 'dbo',
     'TABLE', 'gen_table', 'COLUMN', 'tpl_web_type'
exec sp_addextendedproperty 'MS_Description', N'生成包路径', 'SCHEMA', 'dbo', 'TABLE', 'gen_table', 'COLUMN',
     'package_name'
exec sp_addextendedproperty 'MS_Description', N'生成模块名', 'SCHEMA', 'dbo', 'TABLE', 'gen_table', 'COLUMN',
     'module_name'
exec sp_addextendedproperty 'MS_Description', N'生成业务名', 'SCHEMA', 'dbo', 'TABLE', 'gen_table', 'COLUMN',
     'business_name'
exec sp_addextendedproperty 'MS_Description', N'生成功能名', 'SCHEMA', 'dbo', 'TABLE', 'gen_table', 'COLUMN',
     'function_name'
exec sp_addextendedproperty 'MS_Description', N'生成功能作者', 'SCHEMA', 'dbo', 'TABLE', 'gen_table', 'COLUMN',
     'function_author'
exec sp_addextendedproperty 'MS_Description', N'生成代码方式（0zip压缩包 1自定义路径）', 'SCHEMA', 'dbo', 'TABLE',
     'gen_table', 'COLUMN', 'gen_type'
exec sp_addextendedproperty 'MS_Description', N'生成路径（不填默认项目路径）', 'SCHEMA', 'dbo', 'TABLE', 'gen_table',
     'COLUMN', 'gen_path'
exec sp_addextendedproperty 'MS_Description', N'其它生成选项', 'SCHEMA', 'dbo', 'TABLE', 'gen_table', 'COLUMN',
     'options'
exec sp_addextendedproperty 'MS_Description', N'创建者', 'SCHEMA', 'dbo', 'TABLE', 'gen_table', 'COLUMN', 'create_by'
exec sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', 'dbo', 'TABLE', 'gen_table', 'COLUMN',
     'create_time'
exec sp_addextendedproperty 'MS_Description', N'更新者', 'SCHEMA', 'dbo', 'TABLE', 'gen_table', 'COLUMN', 'update_by'
exec sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', 'dbo', 'TABLE', 'gen_table', 'COLUMN',
     'update_time'
exec sp_addextendedproperty 'MS_Description', N'备注', 'SCHEMA', 'dbo', 'TABLE', 'gen_table', 'COLUMN', 'remark'

-- ----------------------------
-- 19、代码生成业务表字段
-- ----------------------------
-- 删除并重新创建 gen_table_column 表
IF OBJECT_ID('gen_table_column', 'U') IS NOT NULL
DROP TABLE gen_table_column;

create table gen_table_column
(
    column_id      bigint identity
        primary key,
    table_id       bigint,
    column_name    nvarchar(200),
    column_comment nvarchar(500),
    column_type    nvarchar(100),
    java_type      nvarchar(500),
    java_field     nvarchar(200),
    is_pk          char,
    is_increment   char,
    is_required    char,
    is_insert      char,
    is_edit        char,
    is_list        char,
    is_query       char,
    query_type     nvarchar(200) default 'EQ',
    html_type      nvarchar(200),
    dict_type      nvarchar(200) default '',
    sort           int,
    create_by      nvarchar(64)  default '',
    create_time    datetime,
    update_by      nvarchar(64)  default '',
    update_time    datetime
)

exec sp_addextendedproperty 'MS_Description', N'代码生成业务表字段', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column'
exec sp_addextendedproperty 'MS_Description', N'编号', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column', 'COLUMN',
     'column_id'
exec sp_addextendedproperty 'MS_Description', N'归属表编号', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column', 'COLUMN',
     'table_id'
exec sp_addextendedproperty 'MS_Description', N'列名称', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column', 'COLUMN',
     'column_name'
exec sp_addextendedproperty 'MS_Description', N'列描述', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column', 'COLUMN',
     'column_comment'
exec sp_addextendedproperty 'MS_Description', N'列类型', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column', 'COLUMN',
     'column_type'
exec sp_addextendedproperty 'MS_Description', N'JAVA类型', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column', 'COLUMN',
     'java_type'
exec sp_addextendedproperty 'MS_Description', N'JAVA字段名', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column', 'COLUMN',
     'java_field'
exec sp_addextendedproperty 'MS_Description', N'是否主键（1是）', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column', 'COLUMN',
     'is_pk'
exec sp_addextendedproperty 'MS_Description', N'是否自增（1是）', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column', 'COLUMN',
     'is_increment'
exec sp_addextendedproperty 'MS_Description', N'是否必填（1是）', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column', 'COLUMN',
     'is_required'
exec sp_addextendedproperty 'MS_Description', N'是否为插入字段（1是）', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column',
     'COLUMN', 'is_insert'
exec sp_addextendedproperty 'MS_Description', N'是否编辑字段（1是）', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column',
     'COLUMN', 'is_edit'
exec sp_addextendedproperty 'MS_Description', N'是否列表字段（1是）', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column',
     'COLUMN', 'is_list'
exec sp_addextendedproperty 'MS_Description', N'是否查询字段（1是）', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column',
     'COLUMN', 'is_query'
exec sp_addextendedproperty 'MS_Description', N'查询方式（等于、不等于、大于、小于、范围）', 'SCHEMA', 'dbo', 'TABLE',
     'gen_table_column', 'COLUMN', 'query_type'
exec sp_addextendedproperty 'MS_Description', N'显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）', 'SCHEMA', 'dbo',
     'TABLE', 'gen_table_column', 'COLUMN', 'html_type'
exec sp_addextendedproperty 'MS_Description', N'字典类型', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column', 'COLUMN',
     'dict_type'
exec sp_addextendedproperty 'MS_Description', N'排序', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column', 'COLUMN', 'sort'
exec sp_addextendedproperty 'MS_Description', N'创建者', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column', 'COLUMN',
     'create_by'
exec sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column', 'COLUMN',
     'create_time'
exec sp_addextendedproperty 'MS_Description', N'更新者', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column', 'COLUMN',
     'update_by'
exec sp_addextendedproperty 'MS_Description', N'更新时间', 'SCHEMA', 'dbo', 'TABLE', 'gen_table_column', 'COLUMN',
     'update_time'

-- ----------------------------
-- 20、报表配置表
-- ----------------------------
-- 删除并重新创建 statement_cfg 表
IF OBJECT_ID('statement_cfg', 'U') IS NOT NULL
    DROP TABLE statement_cfg;

CREATE TABLE statement_cfg (
                               cfg_id          INT IDENTITY(1,1) NOT NULL ,
                               cfg_code        NVARCHAR(50) NOT NULL ,
                               cfg_description NVARCHAR(100) NULL ,
                               corp_code       NVARCHAR(10) NOT NULL ,
                               statement_code  NVARCHAR(50) NOT NULL ,
                               statement_name  NVARCHAR(50) NOT NULL ,
                               cfg_content     NVARCHAR(MAX) NOT NULL ,
                               cfg_type        TINYINT NOT NULL ,
                               create_time     DATETIME NOT NULL ,
                               update_time     DATETIME NOT NULL ,
                               PRIMARY KEY (cfg_id),
                               UNIQUE (cfg_code)
);

exec sp_addextendedproperty 'MS_Description', N'报表生成的相关配置表', 'SCHEMA', 'dbo', 'TABLE', 'statement_cfg'
exec sp_addextendedproperty 'MS_Description', N'配置id', 'SCHEMA', 'dbo', 'TABLE', 'statement_cfg', 'COLUMN', 'cfg_id'
exec sp_addextendedproperty 'MS_Description', N'配置的编码, 适配redis的key, 格式为 cfg:报表编码:公司编码:配置类型[:其他信息]', 'SCHEMA', 'dbo', 'TABLE', 'statement_cfg', 'COLUMN', 'cfg_code'
exec sp_addextendedproperty 'MS_Description', N'配置的描述', 'SCHEMA', 'dbo', 'TABLE', 'statement_cfg', 'COLUMN', 'cfg_description'
exec sp_addextendedproperty 'MS_Description', N'配置所属的公司编码', 'SCHEMA', 'dbo', 'TABLE', 'statement_cfg', 'COLUMN', 'corp_code'
exec sp_addextendedproperty 'MS_Description', N'配置对应的报表的编码', 'SCHEMA', 'dbo', 'TABLE', 'statement_cfg', 'COLUMN', 'statement_code'
exec sp_addextendedproperty 'MS_Description', N'配置对应的报表的名称', 'SCHEMA', 'dbo', 'TABLE', 'statement_cfg', 'COLUMN', 'statement_name'
exec sp_addextendedproperty 'MS_Description', N'配置的内容', 'SCHEMA', 'dbo', 'TABLE', 'statement_cfg', 'COLUMN', 'cfg_content'
exec sp_addextendedproperty 'MS_Description', N'配置类型, 0: 行列头索引配置, 1: 工作表写入配置, 2: 查询配置, 3: 行列头映射数据集配置', 'SCHEMA', 'dbo', 'TABLE', 'statement_cfg', 'COLUMN', 'cfg_type'
exec sp_addextendedproperty 'MS_Description', N'创建时间', 'SCHEMA', 'dbo', 'TABLE', 'statement_cfg', 'COLUMN', 'create_time'
exec sp_addextendedproperty 'MS_Description', N'修改时间', 'SCHEMA', 'dbo', 'TABLE', 'statement_cfg', 'COLUMN', 'update_time'
































