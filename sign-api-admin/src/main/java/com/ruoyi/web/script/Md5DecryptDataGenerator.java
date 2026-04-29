package com.ruoyi.web.script;

import com.ruoyi.common.utils.security.Md5Utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * 生成 md5_decrypt 表数据：000001～999999 的 INSERT 语句（与 Md5Utils.encrypt 一致）
 * 使用方式：运行 main，会在项目根目录生成 sql/md5_decrypt_data.sql，再执行该 SQL 灌数。
 * 需先执行 sql/md5_decrypt.sql 建表。
 *
 * @author HayDen
 */
public class Md5DecryptDataGenerator {

    public static void main(String[] args) throws IOException {
        String outPath = "sql/md5_decrypt_data.sql";
        try (BufferedWriter w = Files.newBufferedWriter(Paths.get(outPath), StandardCharsets.UTF_8)) {
            w.write("-- MD5 密文明文数据（Java Md5Utils.encrypt 生成），需先建表再执行本文件");
            w.newLine();
            w.write("-- 建议：先清空 TRUNCATE TABLE md5_decrypt; 再执行下面 INSERT");
            w.newLine();
            final int batch = 5000;
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i <= 999999; i++) {
                String plain = String.format("%06d", i);
                String encrypted = Md5Utils.encrypt(plain);
                if (sb.length() > 0) sb.append(",");
                sb.append("('").append(plain).append("','").append(encrypted).append("')");
                if (i % batch == 0) {
                    w.write("INSERT INTO md5_decrypt (plain_text, encrypted) VALUES ");
                    w.write(sb.toString());
                    w.write(";");
                    w.newLine();
                    sb.setLength(0);
                }
            }
            if (sb.length() > 0) {
                w.write("INSERT INTO md5_decrypt (plain_text, encrypted) VALUES ");
                w.write(sb.toString());
                w.write(";");
                w.newLine();
            }
        }
        System.out.println("Generated " + outPath + " (000001～999999). Run it after creating table md5_decrypt.");
    }
}
