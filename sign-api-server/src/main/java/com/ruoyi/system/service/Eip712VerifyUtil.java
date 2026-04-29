package com.ruoyi.system.service;


import com.ruoyi.system.domain.WithdrawRequest;
import lombok.extern.slf4j.Slf4j;
import org.web3j.crypto.ECDSASignature;
import org.web3j.crypto.Hash;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

/**
 * EIP-712 签名验签工具类
 * 用于验证提现请求的签名是否正确
 */
@Slf4j
public class Eip712VerifyUtil {

    /**
     * 合约里写死的 EIP712 Domain
     */
    private static final String DOMAIN_NAME = "ExchangeGradeWithdraw";
    private static final String DOMAIN_VERSION = "1";

    /**
     * EIP712Domain(string name,string version,uint256 chainId,address verifyingContract)
     */
    private static final byte[] DOMAIN_TYPE_HASH = keccak(
            "EIP712Domain(string name,string version,uint256 chainId,address verifyingContract)"
                    .getBytes(StandardCharsets.UTF_8)
    );

    /**
     * WithdrawRequest(uint256 orderId,address user,uint256 amount,uint8 redemption,uint256 deadline,uint256 bizId)
     */
    private static final byte[] WITHDRAW_TYPE_HASH = keccak(
            "WithdrawRequest(uint256 orderId,address user,uint256 amount,uint8 redemption,uint256 deadline,uint256 bizId)"
                    .getBytes(StandardCharsets.UTF_8)
    );

    /**
     * 默认链 ID（BSC主网）
     */
    private static final BigInteger DEFAULT_CHAIN_ID = BigInteger.valueOf(56);

    /**
     * 验证签名是否匹配
     *
     * @param verifyingContract   合约地址
     * @param expectedSignerAddress 预期的签名者地址
     * @param signatureHex        签名（十六进制字符串）
     * @param req                 提现请求
     * @return true=匹配, false=不匹配
     */
    public static boolean verify(String verifyingContract, 
                                  String expectedSignerAddress, 
                                  String signatureHex, 
                                  WithdrawRequest req) {
        return verify(verifyingContract, expectedSignerAddress, signatureHex, req, DEFAULT_CHAIN_ID);
    }

    /**
     * 验证签名是否匹配（可指定链ID）
     *
     * @param verifyingContract   合约地址
     * @param expectedSignerAddress 预期的签名者地址
     * @param signatureHex        签名（十六进制字符串）
     * @param req                 提现请求
     * @param chainId             链ID（56=BSC主网，97=BSC测试网）
     * @return true=匹配, false=不匹配
     */
    public static boolean verify(String verifyingContract,
                                 String expectedSignerAddress,
                                 String signatureHex,
                                 WithdrawRequest req,
                                 BigInteger chainId) {
        log.info("开始验签，verifyingContract={}, expectedSignerAddress={}, chainId={}",
                verifyingContract, expectedSignerAddress, chainId);
        log.debug("提现请求: orderId={}, user={}, amount={}, redemption={}, deadline={},  bizId={}",
                req.getOrderId(), req.getUser(), req.getAmount(), req.getRedemption(),
                req.getDeadline(),  req.getBizId());

        try {
            // 1. 构建 domainSeparator
            byte[] domainSeparator = buildDomainSeparator(chainId, verifyingContract);

            // 2. 构建 structHash
            byte[] structHash = buildWithdrawStructHash(req);

            // 3. 构建最终 digest
            byte[] digest = buildEip712Digest(domainSeparator, structHash);

            // 4. 从签名恢复地址
            String recoveredAddress = recoverAddressFromSignature(digest, signatureHex);
            log.info("恢复出的签名地址={}", recoveredAddress);

            // 5. 比较是否匹配
            boolean isValid = expectedSignerAddress != null && expectedSignerAddress.equalsIgnoreCase(recoveredAddress);
            log.info("验签结果: {}", isValid ? "成功" : "失败");

            return isValid;

        } catch (Exception e) {
            log.error("验签异常，verifyingContract={}, expectedSignerAddress={}, chainId={}, error={}",
                    verifyingContract, expectedSignerAddress, chainId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 验证签名并返回恢复出的地址
     *
     * @param verifyingContract 合约地址
     * @param signatureHex      签名
     * @param req               提现请求
     * @return 恢复出的签名者地址
     */
    public static String recoverAddress(String verifyingContract,
                                         String signatureHex,
                                         WithdrawRequest req) {
        return recoverAddress(verifyingContract, signatureHex, req, DEFAULT_CHAIN_ID);
    }

    /**
     * 验证签名并返回恢复出的地址（可指定链ID）
     *
     * @param verifyingContract 合约地址
     * @param signatureHex      签名
     * @param req               提现请求
     * @param chainId           链ID
     * @return 恢复出的签名者地址
     */
    public static String recoverAddress(String verifyingContract,
                                         String signatureHex,
                                         WithdrawRequest req,
                                         BigInteger chainId) {
        try {
            byte[] domainSeparator = buildDomainSeparator(
                    chainId,
                    verifyingContract
            );

            byte[] structHash = buildWithdrawStructHash(req);
            byte[] digest = buildEip712Digest(domainSeparator, structHash);

            return recoverAddressFromSignature(digest, signatureHex);

        } catch (Exception e) {
            return null;
        }
    }

    // ==================== 私有方法 ====================

    /**
     * 构建 domainSeparator
     */
    private static byte[] buildDomainSeparator(BigInteger chainId,
                                               String verifyingContract) {
        byte[] nameHash = keccak(Eip712VerifyUtil.DOMAIN_NAME.getBytes(StandardCharsets.UTF_8));
        byte[] versionHash = keccak(Eip712VerifyUtil.DOMAIN_VERSION.getBytes(StandardCharsets.UTF_8));

        byte[] encoded = concat(
                DOMAIN_TYPE_HASH,
                nameHash,
                versionHash,
                uint256ToBytes32(chainId),
                addressToBytes32(verifyingContract)
        );

        return keccak(encoded);
    }

    /**
     * 构建 WithdrawRequest structHash
     */
    private static byte[] buildWithdrawStructHash(WithdrawRequest req) {
        byte[] encoded = concat(
                WITHDRAW_TYPE_HASH,
                uint256ToBytes32(req.getOrderId()),
                addressToBytes32(req.getUser()),
                uint256ToBytes32(req.getAmount()),
                uint256ToBytes32(req.getRedemption()),
                uint256ToBytes32(req.getDeadline()),
                uint256ToBytes32(req.getBizId())
        );

        return keccak(encoded);
    }

    /**
     * 最终 digest: keccak256(0x1901 ++ domainSeparator ++ structHash)
     */
    private static byte[] buildEip712Digest(byte[] domainSeparator, byte[] structHash) {
        byte[] prefix = new byte[]{0x19, 0x01};
        return keccak(concat(prefix, domainSeparator, structHash));
    }

    /**
     * 从签名恢复地址
     */
    private static String recoverAddressFromSignature(byte[] digest, String signatureHex) {
        Sign.SignatureData signatureData = parseSignature(signatureHex);
        int recId = (signatureData.getV()[0] & 0xFF) - 27;

        if (recId < 0 || recId > 3) {
            throw new IllegalArgumentException("签名v值无效: " + (signatureData.getV()[0] & 0xFF));
        }

        BigInteger publicKey = Sign.recoverFromSignature(
                recId,
                new ECDSASignature(
                        new BigInteger(1, signatureData.getR()),
                        new BigInteger(1, signatureData.getS())
                ),
                digest
        );

        if (publicKey == null) {
            throw new IllegalStateException("无法从签名恢复公钥");
        }

        return ("0x" + publicKeyToAddress(publicKey)).toLowerCase();
    }

    /**
     * 解析 65 字节签名
     */
    private static Sign.SignatureData parseSignature(String signatureHex) {
        byte[] sig = Numeric.hexStringToByteArray(signatureHex);
        if (sig.length != 65) {
            throw new IllegalArgumentException("签名长度必须是65字节");
        }

        byte[] r = new byte[32];
        byte[] s = new byte[32];
        byte[] v = new byte[1];

        System.arraycopy(sig, 0, r, 0, 32);
        System.arraycopy(sig, 32, s, 0, 32);
        v[0] = sig[64];

        if ((v[0] & 0xFF) < 27) {
            v[0] = (byte) ((v[0] & 0xFF) + 27);
        }

        return new Sign.SignatureData(v, r, s);
    }

    /**
     * 公钥转地址
     */
    private static String publicKeyToAddress(BigInteger publicKey) {
        byte[] pubBytes = Numeric.toBytesPadded(publicKey, 64);
        byte[] hash = Hash.sha3(pubBytes);
        byte[] addrBytes = new byte[20];
        System.arraycopy(hash, 12, addrBytes, 0, 20);
        return Numeric.toHexStringNoPrefix(addrBytes);
    }

    private static byte[] keccak(byte[] input) {
        return Hash.sha3(input);
    }

    private static byte[] uint256ToBytes32(BigInteger value) {
        return Numeric.toBytesPadded(value, 32);
    }

    private static byte[] addressToBytes32(String address) {
        String clean = Numeric.cleanHexPrefix(address);
        if (clean.length() != 40) {
            throw new IllegalArgumentException("地址长度不正确: " + address);
        }

        byte[] addr = Numeric.hexStringToByteArray(clean);
        byte[] out = new byte[32];
        System.arraycopy(addr, 0, out, 12, 20);
        return out;
    }

    private static byte[] concat(byte[]... arrays) {
        int len = 0;
        for (byte[] arr : arrays) {
            len += arr.length;
        }

        byte[] out = new byte[len];
        int pos = 0;
        for (byte[] arr : arrays) {
            System.arraycopy(arr, 0, out, pos, arr.length);
            pos += arr.length;
        }
        return out;
    }
}