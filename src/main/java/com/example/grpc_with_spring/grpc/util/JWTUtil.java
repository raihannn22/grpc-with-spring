package com.example.grpc_with_spring.grpc.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

public class JWTUtil {
    private final PrivateKey apiKey;

    public JWTUtil(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String realPrivateKey = privateKey.replaceAll("-----END PRIVATE KEY-----", "").replaceAll("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("\n", "");
        byte[] b1 = Base64.getDecoder().decode(realPrivateKey);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(b1);
        //X509EncodedKeySpec spec = new X509EncodedKeySpec(b1);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        apiKey = kf.generatePrivate(spec);
    }

    public String createTokenJWT(String id, String issuer, String subject, long ttlMillis) throws NoSuchAlgorithmException, InvalidKeySpecException {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);

        JwtBuilder builder = Jwts.builder().setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .setIssuer(issuer)
                .signWith(apiKey);

        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }

      return builder.compact();
    }

    public String validateTokenJWT(String jwt) throws SignatureException,Exception{

        Claims claims = Jwts.parser()
                .setSigningKey(apiKey)
                .parseClaimsJws(jwt).getBody();
        String id = claims.getId();
        String subject = claims.getSubject();
        String issuer = claims.getIssuer();
        Date expireDate = claims.getExpiration();
        if(expireDate.before(new Date()))
            throw new Exception("Expire");
        return id;
    }
}
