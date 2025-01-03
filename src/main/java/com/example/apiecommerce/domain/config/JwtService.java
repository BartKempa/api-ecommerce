package com.example.apiecommerce.domain.config;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
class JwtService {
    private static  final int EXP_TIME_SEC = 7 * 24 * 60 * 60;
    private final JWSAlgorithm jwsAlgorithm = JWSAlgorithm.HS256;
    private final JWSSigner signer;
    private final JWSVerifier verifier;

    public JwtService(@Value("${jws.sharedKey}")String sharedKey) {
        try {
            signer = new MACSigner(sharedKey.getBytes());
            verifier = new MACVerifier(sharedKey.getBytes());
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    String createSignedJwt(String username, List<String> authorities){
        JWSHeader header = new JWSHeader(jwsAlgorithm);
        LocalDateTime nowPlus7Days = LocalDateTime.now().plusSeconds(EXP_TIME_SEC);
        Date expirationDate = Date.from(nowPlus7Days.atZone(ZoneId.systemDefault()).toInstant());
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(username)
                .expirationTime(expirationDate)
                .claim("authorities", authorities)
                .build();
        SignedJWT signedJWT = new SignedJWT(header, claimsSet);
        try {
            signedJWT.sign(signer);
        } catch (JOSEException e) {
            throw new JwtAuthenticationException("Signing JWT failed");
        }
        return signedJWT.serialize();
    }


}
