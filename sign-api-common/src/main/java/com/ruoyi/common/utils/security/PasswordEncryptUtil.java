package com.ruoyi.common.utils.security;

import com.ruoyi.common.utils.Aes256Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 登录密码与支付密码的存储与校验。
 * <p>
 * 新数据使用 BCrypt 单向哈希；旧数据仍支持 AES（登录）或 AES/裸 MD5（支付）校验。
 * 本系统历史支付密码为 AES({@code MD5(MD5(明文)) + uid})，校验与迁移时一并兼容。
 * </p>
 */
@Slf4j
@Component
public class PasswordEncryptUtil {

    private static final int BCRYPT_STRENGTH = 10;

    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder(BCRYPT_STRENGTH);

    private final String loginPasswordEncryptKey = System.getenv("LOGIN_PASSWORD_ENCRYPT_KEY");

    private final String paymentPasswordEncryptKey = System.getenv("PAYMENT_PASSWORD_ENCRYPT_KEY");

    /**
     * 新登录密码入库：BCrypt(MD5(明文))，与登录接口传入的 MD5 摘要一致。
     */
    public String hashLoginPassword(String plainPassword) {
        if (plainPassword == null) {
            return null;
        }
        String md5Hex = Md5Utils.encrypt(plainPassword);
        return md5Hex == null ? null : bcrypt.encode(md5Hex);
    }

    /**
     * 在已验证旧格式登录后，将库中记录升级为 BCrypt（入参为客户端传来的 MD5(明文)，与 {@link #hashLoginPassword} 一致）。
     */
    public String hashLoginPasswordFromClientMd5(String md5HexFromClient) {
        if (md5HexFromClient == null) {
            return null;
        }
        return bcrypt.encode(md5HexFromClient);
    }

    /**
     * 登录校验：客户端传 MD5(明文)；库中为 BCrypt 摘要或历史 AES 密文。
     */
    public boolean verifyLoginPassword(String md5HexFromClient, String stored) {
        if (StringUtils.isBlank(stored) || md5HexFromClient == null) {
            return false;
        }
        if (isBcryptHash(stored)) {
            return bcrypt.matches(md5HexFromClient, stored);
        }
        if (StringUtils.isBlank(loginPasswordEncryptKey)) {
            log.warn("LOGIN_PASSWORD_ENCRYPT_KEY 未配置，无法校验历史 AES 登录密码");
            return false;
        }
        try {
            String plain = Aes256Utils.decrypt(loginPasswordEncryptKey, stored);
            return md5HexFromClient.equals(Md5Utils.encrypt(plain));
        } catch (Exception e) {
            log.debug("历史 AES 登录密码解密失败: {}", e.getMessage());
            return false;
        }
    }

    public boolean isLoginPasswordBcrypt(String stored) {
        return isBcryptHash(stored);
    }

    /**
     * 新支付密码入库：BCrypt(MD5(明文) + uid)。
     */
    public String hashPaymentPassword(String plainPassword, String uid) {
        if (plainPassword == null || uid == null) {
            return null;
        }
        String md5Password = Md5Utils.encrypt(plainPassword);
        if (md5Password == null) {
            return null;
        }
        return bcrypt.encode(md5Password + uid);
    }

    /**
     * 验证支付密码：支持 BCrypt、历史 AES（本系统为 MD5(MD5)+uid）、裸 MD5。
     */
    public boolean verifyPaymentPassword(String plainPassword, String encryptedPassword, String uid) {
        if (plainPassword == null || encryptedPassword == null || uid == null) {
            return false;
        }
        String singleMd = Md5Utils.encrypt(plainPassword);
        String doubleMd = singleMd == null ? null : Md5Utils.encrypt(singleMd);
        if (isBcryptHash(encryptedPassword)) {
            if (singleMd != null && bcrypt.matches(singleMd + uid, encryptedPassword)) {
                return true;
            }
            return doubleMd != null && bcrypt.matches(doubleMd + uid, encryptedPassword);
        }
        String decryptedMd5 = decryptPaymentPasswordAes(encryptedPassword, uid);
        if (decryptedMd5 != null) {
            if (singleMd != null && singleMd.equals(decryptedMd5)) {
                return true;
            }
            return doubleMd != null && doubleMd.equals(decryptedMd5);
        }
        try {
            if (singleMd != null && singleMd.equals(encryptedPassword)) {
                return true;
            }
            return doubleMd != null && doubleMd.equals(encryptedPassword);
        } catch (Exception e) {
            log.error("验证支付密码失败, uid {}", uid, e);
            return false;
        }
    }

    private String decryptPaymentPasswordAes(String encryptedPassword, String uid) {
        if (StringUtils.isBlank(paymentPasswordEncryptKey)) {
            return null;
        }
        try {
            String decrypted = Aes256Utils.decrypt(paymentPasswordEncryptKey, encryptedPassword);
            if (decrypted == null) {
                return null;
            }
            if (!decrypted.endsWith(uid)) {
                log.warn("支付密码解密后验证失败，账号不匹配");
                return null;
            }
            return decrypted.substring(0, decrypted.length() - uid.length());
        } catch (Exception e) {
            log.debug("解密支付密码失败（可能为旧 MD5 格式）, uid {}", uid);
            return null;
        }
    }

    /**
     * 批量迁移：将可逆 AES 或裸 MD5 转为 BCrypt(MD5+uid)（AES 内为单 MD5 或双 MD5 前缀均保留原前缀再 +uid 编码）。
     */
    public String migratePaymentPasswordToBcrypt(String oldStored, String uid) {
        if (StringUtils.isBlank(oldStored) || StringUtils.isBlank(uid)) {
            return null;
        }
        if (isBcryptHash(oldStored)) {
            return oldStored;
        }
        String md5Password = decryptPaymentPasswordAes(oldStored, uid);
        if (md5Password == null && oldStored.length() == 32) {
            md5Password = oldStored;
        }
        if (StringUtils.isBlank(md5Password)) {
            return null;
        }
        return bcrypt.encode(md5Password + uid);
    }

    public static boolean isBcryptHash(String value) {
        return value != null && value.length() >= 4 && value.charAt(0) == '$' && value.charAt(1) == '2';
    }
}
