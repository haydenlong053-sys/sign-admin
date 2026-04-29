-- ============================================
-- 奖励配置管理菜单SQL
-- 执行前请确认父级菜单ID（通常为0表示根菜单，或选择一个现有目录）
-- ============================================

-- 1. 插入主菜单（目录类型 M）
-- parent_id=0 表示顶级菜单，如果需要放在某个目录下，请修改为对应的菜单ID
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, is_refresh, perms, icon, create_by, create_time, update_by, update_time, remark)
values('奖励配置', 0, 5, '#', 'M', '0', '1', '', 'fa fa-gift', 'admin', sysdate(), '', null, '奖励配置管理目录');

-- 获取刚插入的菜单ID
SELECT @rewardConfigParentId := LAST_INSERT_ID();

-- 2. 插入子菜单（菜单类型 C）- 奖励配置列表
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, is_refresh, perms, icon, create_by, create_time, update_by, update_time, remark)
values('奖励配置列表', @rewardConfigParentId, 1, '/project/rewardConfig', 'C', '0', '1', 'project:rewardConfig:view', '#', 'admin', sysdate(), '', null, '奖励配置列表菜单');

-- 获取刚插入的菜单ID
SELECT @rewardConfigMenuId := LAST_INSERT_ID();

-- 3. 插入按钮权限（按钮类型 F）
-- 查询按钮
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, is_refresh, perms, icon, create_by, create_time, update_by, update_time, remark)
values('奖励配置查询', @rewardConfigMenuId, 1, '#', 'F', '0', '1', 'project:rewardConfig:list', '#', 'admin', sysdate(), '', null, '');

-- 新增按钮
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, is_refresh, perms, icon, create_by, create_time, update_by, update_time, remark)
values('奖励配置新增', @rewardConfigMenuId, 2, '#', 'F', '0', '1', 'project:rewardConfig:add', '#', 'admin', sysdate(), '', null, '');

-- 修改按钮
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, is_refresh, perms, icon, create_by, create_time, update_by, update_time, remark)
values('奖励配置修改', @rewardConfigMenuId, 3, '#', 'F', '0', '1', 'project:rewardConfig:edit', '#', 'admin', sysdate(), '', null, '');

-- 删除按钮
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, is_refresh, perms, icon, create_by, create_time, update_by, update_time, remark)
values('奖励配置删除', @rewardConfigMenuId, 4, '#', 'F', '0', '1', 'project:rewardConfig:remove', '#', 'admin', sysdate(), '', null, '');

-- 导出按钮
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, is_refresh, perms, icon, create_by, create_time, update_by, update_time, remark)
values('奖励配置导出', @rewardConfigMenuId, 5, '#', 'F', '0', '1', 'project:rewardConfig:export', '#', 'admin', sysdate(), '', null, '');

-- ============================================
-- 说明：
-- 1. menu_type: M=目录, C=菜单, F=按钮
-- 2. visible: 0=显示, 1=隐藏
-- 3. is_refresh: 0=刷新, 1=不刷新
-- 4. order_num: 显示顺序，数字越小越靠前
-- 5. parent_id: 父菜单ID，0表示顶级菜单
-- 6. 如果你的系统已经有"项目管理"或其他目录，可以将第一个菜单的parent_id改为对应目录的ID
-- ============================================

-- ============================================
-- 可选：将菜单分配给管理员角色（假设管理员角色ID为1）
-- 如果不需要自动分配，可以注释掉下面的SQL
-- ============================================

-- 获取管理员角色ID（通常超级管理员角色ID为1）
-- SELECT @adminRoleId := role_id FROM sys_role WHERE role_key = 'admin' LIMIT 1;

-- 将主菜单分配给管理员角色
-- INSERT INTO sys_role_menu (role_id, menu_id) VALUES (@adminRoleId, @rewardConfigParentId);

-- 将子菜单分配给管理员角色
-- INSERT INTO sys_role_menu (role_id, menu_id) VALUES (@adminRoleId, @rewardConfigMenuId);

-- 将所有按钮权限分配给管理员角色
-- INSERT INTO sys_role_menu (role_id, menu_id) 
-- SELECT @adminRoleId, menu_id FROM sys_menu WHERE parent_id = @rewardConfigMenuId AND menu_type = 'F';

-- ============================================
-- 验证SQL：查看是否插入成功
-- ============================================
-- SELECT * FROM sys_menu WHERE menu_name LIKE '%奖励配置%' ORDER BY parent_id, order_num;
