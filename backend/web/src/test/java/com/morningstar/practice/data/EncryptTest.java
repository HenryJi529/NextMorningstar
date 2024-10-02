package com.morningstar.practice.data;

import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.DES;
import org.junit.jupiter.api.Test;

public class EncryptTest {
    @Test
    public void testDES() {
        // 1. 生成随机密钥（8字节，64位）
        byte[] key = SecureUtil.generateKey("DES").getEncoded();
        System.out.println("随机密钥: " + HexUtil.encodeHexStr(key));

        // 2. 使用自定义密钥
        String secretKey = "12345678"; // DES密钥必须是8位
        DES des = SecureUtil.des(secretKey.getBytes());

        // 3. 加密
        String plainText = "Hello World! 你好，世界！";
        System.out.println("加密结果(Base64): " + des.encryptBase64(plainText));
        System.out.println("加密结果(Hex): " + des.encryptHex(plainText));

        // 4. 解密
        assert plainText.equals(des.decryptStr(des.encryptBase64(plainText)));
        assert plainText.equals(des.decryptStr(des.encryptHex(plainText)));
    }

    @Test
    public void testAES() {
        // 1. 随机生成密钥
        byte[] key = SecureUtil.generateKey("AES").getEncoded();
        System.out.println("随机密钥: " + HexUtil.encodeHexStr(key));

        // 2. 使用自定义密钥
        String secretKey = "1234567812345678"; // AES密钥必须是16位
        AES aes = SecureUtil.aes(secretKey.getBytes());

        // 3. 加密
        String plainText = "Hello World! 你好，世界！";
        System.out.println("加密结果(Base64): " + aes.encryptBase64(plainText));
        System.out.println("加密结果(Hex): " + aes.encryptHex(plainText));

        // 4. 解密
        assert plainText.equals(aes.decryptStr(aes.encryptBase64(plainText)));
    }

    @Test
    public void testRSA(){
        // 生成 RSA 密钥对
        RSA rsa = SecureUtil.rsa();

        // 获取公钥、私钥
        String publicKey = rsa.getPublicKeyBase64();
        String privateKey = rsa.getPrivateKeyBase64();

        System.out.println(publicKey);
        System.out.println(privateKey);

        String plainText = "Hello World! 你好，世界！";
        // 公钥加密私钥解密
        byte[] encrypted = new RSA(null, publicKey).encrypt(plainText, KeyType.PublicKey);
        byte[] decrypted = rsa.decrypt(encrypted, KeyType.PrivateKey);
        assert plainText.equals(new String(decrypted));
    }
}
