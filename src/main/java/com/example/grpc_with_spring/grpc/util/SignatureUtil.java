package com.example.grpc_with_spring.grpc.util;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Formatter;

public class SignatureUtil {


    public String generateOauthSignature(String privateKey, String clientId, String isoTime) throws Exception {
        String realPrivateKey = privateKey.replaceAll("-----END PRIVATE KEY-----", "").replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("\n", "");

        byte[] b1 = Base64.getDecoder().decode(realPrivateKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1);
        KeyFactory kf = KeyFactory.getInstance("RSA");

        Signature privateSignature = Signature.getInstance("SHA256withRSA");
        privateSignature.initSign(kf.generatePrivate(spec));
        privateSignature.update((clientId + "|" + isoTime).getBytes("UTF-8"));
        byte[] s = privateSignature.sign();
        return Base64.getEncoder().encodeToString(s);
        //return toHexString(s);
    }

    public boolean validateOauthSignature(String publicKey, String clientId, String isoTime, String signature) throws Exception {
        String realPublicKey = publicKey.replaceAll("-----END PUBLIC KEY-----", "").replaceAll("-----BEGIN PUBLIC KEY-----", "")
                .replaceAll("\n", "");
        byte[] b1 = Base64.getDecoder().decode(realPublicKey);
        X509EncodedKeySpec ks = new X509EncodedKeySpec(b1);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pub = kf.generatePublic(ks);

        Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initVerify(pub);
        sign.update((clientId + "|" + isoTime).getBytes(StandardCharsets.UTF_8));
        return sign.verify(Base64.getDecoder().decode(signature));
        // return sign.verify(fromHexString(signature));
    }

    public String generateServiceSignature(String clientSecret, String method, String url, String authToken, String isoTime, String body) throws Exception {
        String hexEncode = hash256(body);
        String stringToSign = method + ":" + url + ":" + authToken + ":" + hexEncode + ":" + isoTime;
        System.out.println("ini string to sign ->" + stringToSign);

        return Base64.getEncoder().encodeToString(calculateHMACSHA512(stringToSign, clientSecret));
        //return toHexString(calculateHMACSHA512(stringToSign, clientSecret));
    }

    public boolean validateServiceSignature(String clientSecret, String method, String url, String authToken, String isoTime, String body, String signature) throws Exception {

        String signatureStr = this.generateServiceSignature(clientSecret, method, url, authToken, isoTime, body);
        return signature.equals(signatureStr);
    }

    private String hash256(String input) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

        String hexStr = "";
        for (int i = 0; i < hash.length; i++) {
            hexStr += Integer.toString((hash[i] & 0xff) + 0x100, 16).substring(1);
        }
        return hexStr.toLowerCase().replace("-", "");
    }

    public byte[] calculateHMACSHA512(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA512");
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(secretKeySpec);
        return mac.doFinal(data.getBytes());
    }

    private static String toHexString(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String output = formatter.toString();
        formatter.close();
        return output;
    }

    private byte[] fromHexString(String hexString){
        hexString = hexString.toUpperCase();
        int strLeng = hexString.length() / 2;
        byte[] arrayChar = new byte[strLeng];
        for (int za = 0; za < strLeng; za++) {
            String tmp = hexString.substring((za * 2), (za * 2) + 2);
            int hsl = Integer.parseInt(tmp, 16);
            arrayChar[za] = (byte)hsl;
        }
        return arrayChar;
    }
}
