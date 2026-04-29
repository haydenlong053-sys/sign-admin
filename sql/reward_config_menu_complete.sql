-- ============================================
-- 奖励配置管理菜单SQL（完整版 - 含权限分配）
-- 执行此SQL后，管理员用户登录后即可看到"奖励配置"菜单
-- ============================================

-- 1. 插入主菜单（目录类型 M）
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, is_refresh, perms, icon, create_by, create_time, update_by, update_time, remark)
values('奖励配置', 0, 5, '#', 'M', '0', '1', '', 'fa fa-gift', 'admin', sysdate(), '', null, '奖励配置管理目录');

SELECT @rewardConfigParentId := LAST_INSERT_ID();

-- 2. 插入子菜单（菜单类型 C）
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, is_refresh, perms, icon, create_by, create_time, update_by, update_time, remark)
values('奖励配置列表', @rewardConfigParentId, 1, '/project/rewardConfig', 'C', '0', '1', 'project:rewardConfig:view', '#', 'admin', sysdate(), '', null, '奖励配置列表菜单');

SELECT @rewardConfigMenuId := LAST_INSERT_ID();

-- 3. 插入按钮权限（按钮类型 F）
insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, is_refresh, perms, icon, create_by, create_time, update_by, update_time, remark)
values('奖励配置查询', @rewardConfigMenuId, 1, '#', 'F', '0', '1', 'project:rewardConfig:list', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, is_refresh, perms, icon, create_by, create_time, update_by, update_time, remark)
values('奖励配置新增', @rewardConfigMenuId, 2, '#', 'F', '0', '1', 'project:rewardConfig:add', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, is_refresh, perms, icon, create_by, create_time, update_by, update_time, remark)
values('奖励配置修改', @rewardConfigMenuId, 3, '#', 'F', '0', '1', 'project:rewardConfig:edit', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, is_refresh, perms, icon, create_by, create_time, update_by, update_time, remark)
values('奖励配置删除', @rewardConfigMenuId, 4, '#', 'F', '0', '1', 'project:rewardConfig:remove', '#', 'admin', sysdate(), '', null, '');

insert into sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, is_refresh, perms, icon, create_by, create_time, update_by, update_time, remark)
values('奖励配置导出', @rewardConfigMenuId, 5, '#', 'F', '0', '1', 'project:rewardConfig:export', '#', 'admin', sysdate(), '', null, '');

-- ============================================
-- 4. 将菜单权限分配给超级管理员角色（role_id=1）
-- ============================================

-- 分配主菜单
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, @rewardConfigParentId);

-- 分配子菜单
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, @rewardConfigMenuId);

-- 分配所有按钮权限
INSERT INTO sys_role_menu (role_id, menu_id) 
SELECT 1, menu_id FROM sys_menu WHERE parent_id = @rewardConfigMenuId AND menu_type = 'F';

-- ============================================
-- 验证：查看插入的菜单
-- ============================================
SELECT 
    m.menu_id,
    m.menu_name,
    m.parent_id,
    m.menu_type,
    m.url,
    m.perms,
    m.visible,
    m.order_num,
    m.icon
FROM sys_menu m
WHERE m.menu_name LIKE '%奖励配置%'
ORDER BY m.parent_id, m.order_num;

-- ============================================
-- 完成！重启项目或重新登录即可看到菜单
-- ============================================
