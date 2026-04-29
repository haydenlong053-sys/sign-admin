-- MD5 密文明文对照表：用于根据密文反查明文（000001～999999）
-- 执行顺序：先建表，再任选一种方式灌数
-- 方式一：本文件内存储过程（见下方）
-- 方式二：Java 脚本生成与 Md5Utils 完全一致的密文 — 运行 com.ruoyi.web.script.Md5DecryptDataGenerator#main 生成 sql/md5_decrypt_data.sql，再执行该文件

-- 建表
DROP TABLE IF EXISTS md5_decrypt;
CREATE TABLE md5_decrypt (
  id         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
  plain_text VARCHAR(32)  NOT NULL COMMENT '明文（如 000001～999999）',
  encrypted  VARCHAR(32)  NOT NULL COMMENT 'MD5 密文（32位小写）',
  PRIMARY KEY (id),
  UNIQUE KEY uk_encrypted (encrypted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MD5密文明文对照表';

-- 方式一：用 MySQL 存储过程灌数（与 Java UTF-8 对纯数字一致，可直接用）
DELIMITER $$
DROP PROCEDURE IF EXISTS fill_md5_decrypt$$
CREATE PROCEDURE fill_md5_decrypt()
BEGIN
  DECLARE i INT DEFAULT 1;
  WHILE i <= 999999 DO
    INSERT INTO md5_decrypt (plain_text, encrypted)
    VALUES (LPAD(i, 6, '0'), LOWER(MD5(LPAD(i, 6, '0'))));
    SET i = i + 1;
  END WHILE;
END$$
DELIMITER ;

CALL fill_md5_decrypt();
DROP PROCEDURE fill_md5_decrypt;

-- 查询示例：SELECT plain_text FROM md5_decrypt WHERE encrypted = 'e10adc3949ba59abbe56e057f20f883e';
