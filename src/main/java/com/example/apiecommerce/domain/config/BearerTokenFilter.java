package com.example.apiecommerce.domain.config;

import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;
import java.text.ParseException;

public class BearerTokenFilter extends HttpFilter {
    private final Logger logger = LoggerFactory.getLogger(BearerTokenFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
    private final AuthenticationFailureHandler authenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler();
    private final JwtService jwtService;

    public BearerTokenFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader == null || authorizationHeader.isEmpty()){
            logger.debug("Missing authorization header or empty Bearer token");
            chain.doFilter(request, response);
        } else {
            String compactJwt = authorizationHeader.substring(BEARER_PREFIX.length());
            SignedJWT signedJWT;
            try {
                signedJWT = SignedJWT.parse(compactJwt);
                jwtService.verifySignature(signedJWT);
                jwtService.verifyExpirationDate(signedJWT);
                setSecurityContext(signedJWT);
                chain.doFilter(request, response);
            } catch (ParseException e) {
                JwtAuthenticationException jwtAuthenticationException  = new JwtAuthenticationException("Bearer token could not be parsed");
                logger.debug(e.getMessage());
                authenticationFailureHandler.onAuthenticationFailure(request, response, jwtAuthenticationException);
            } catch (JwtAuthenticationException e){
                logger.debug(e.getMessage());
                authenticationFailureHandler.onAuthenticationFailure(request, response, e);
            }
        }
    }

    private void setSecurityContext(SignedJWT signedJwt) {
        Authentication authentication = jwtService.createAuthentication(signedJwt);
        SecurityContext securityContext = securityContextHolderStrategy.getContext();
        securityContext.setAuthentication(authentication);
    }
}
