-- ============================================
-- BSC提现对账记录管理菜单SQL
-- 父级菜单ID: 3214
-- ============================================

-- 1. 插入子菜单（菜单类型 C）- BSC提现对账记录列表
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `url`, `menu_type`, `visible`, `is_refresh`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('BSC提现对账', 3214, 1, '/project/withdrawReconcile', 'C', '0', '1', 'project:withdrawReconcile:view', 'fa fa-exchange', 'admin', sysdate(), '', NULL, 'BSC提现对账记录列表菜单');

-- 获取刚插入的菜单ID
SELECT @withdrawReconcileMenuId := LAST_INSERT_ID();

-- 2. 插入按钮权限（按钮类型 F）
-- 查询按钮
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `url`, `menu_type`, `visible`, `is_refresh`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('对账记录查询', @withdrawReconcileMenuId, 1, '#', 'F', '0', '1', 'project:withdrawReconcile:list', '#', 'admin', sysdate(), '', NULL, '');

-- 导出按钮
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `url`, `menu_type`, `visible`, `is_refresh`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES ('对账记录导出', @withdrawReconcileMenuId, 2, '#', 'F', '0', '1', 'project:withdrawReconcile:export', '#', 'admin', sysdate(), '', NULL, '');

-- ============================================
-- 说明：
-- 1. menu_type: M=目录, C=菜单, F=按钮
-- 2. visible: 0=显示, 1=隐藏
-- 3. is_refresh: 0=刷新, 1=不刷新
-- 4. order_num: 显示顺序，数字越小越靠前
-- 5. parent_id: 3214（请根据实际情况确认父级菜单ID）
-- 6. perms: 权限标识，与Controller中的@RequiresPermissions对应
-- ============================================

-- ============================================
-- 可选：将菜单分配给管理员角色（假设管理员角色ID为1）
-- 如果不需要自动分配，可以注释掉下面的SQL
-- ============================================

-- 将子菜单分配给管理员角色
-- INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) VALUES (1, @withdrawReconcileMenuId);

-- 将所有按钮权限分配给管理员角色
-- INSERT INTO `sys_role_menu` (`role_id`, `menu_id`) 
-- SELECT 1, menu_id FROM sys_menu WHERE parent_id = @withdrawReconcileMenuId AND menu_type = 'F';

-- ============================================
-- 验证SQL：查看是否插入成功
-- ============================================
-- SELECT * FROM sys_menu WHERE menu_name LIKE '%对账%' ORDER BY parent_id, order_num;
