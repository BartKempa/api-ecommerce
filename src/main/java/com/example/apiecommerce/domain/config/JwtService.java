package com.example.apiecommerce.domain.config;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class JwtService {
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
    public String createSignedJwt(String username, List<String> authorities){
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

    void verifySignature(SignedJWT signedJWT){
        try {
            boolean verified = signedJWT.verify(verifier);
            if (!verified) {
                throw new JwtAuthenticationException("JWT signature verification failed for token %s".formatted(signedJWT.serialize()));
            }
        } catch (JOSEException e) {
            throw new JwtAuthenticationException("JWT signature verification failed for token %s".formatted(signedJWT.serialize()));
        }
    }

    void verifyExpirationDate(SignedJWT signedJWT){
        try {
            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
            LocalDateTime expirationDateTime = jwtClaimsSet
                    .getDateClaim("exp")
                    .toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
            if (LocalDateTime.now().isAfter(expirationDateTime)){
                throw new JwtAuthenticationException("Token expired af %s".formatted(expirationDateTime));
            }
        } catch (ParseException e) {
            throw new JwtAuthenticationException("Token does not have exp claim");
        }
    }

    Authentication createAuthentication(SignedJWT signedJWT){
        List<String> authorities;
        String subject;
        try {
            JWTClaimsSet jwtClaimsSet = signedJWT.getJWTClaimsSet();
            subject = jwtClaimsSet.getSubject();
            authorities = jwtClaimsSet.getStringListClaim("authorities");
        } catch (ParseException e) {
            throw new JwtAuthenticationException("Missing claims subject or authorities");
        }
        List<SimpleGrantedAuthority> grantedAuthorities = authorities.stream().map(SimpleGrantedAuthority::new).toList();
        return new UsernamePasswordAuthenticationToken(subject, null, grantedAuthorities);
    }
}
