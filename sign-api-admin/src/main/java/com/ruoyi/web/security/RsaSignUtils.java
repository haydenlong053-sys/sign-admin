package com.ruoyi.web.security;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * <p>
 *
 * </p>
 *
 * @author ll
 * @since 2025-10-23 9:42
 */
public class RsaSignUtils {

    public static PublicKey loadPublicKey(String base64) throws Exception {
        byte[] bytes = Base64.getDecoder().decode(base64);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }

    public static boolean verify(String data, String sign, PublicKey publicKey) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(data.getBytes(StandardCharsets.UTF_8));
        return sig.verify(Base64.getDecoder().decode(sign));
    }
}