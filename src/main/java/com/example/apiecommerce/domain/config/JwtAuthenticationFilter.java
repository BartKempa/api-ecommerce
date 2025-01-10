package com.example.apiecommerce.domain.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;

class JwtAuthenticationFilter extends HttpFilter {
    private final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final RequestMatcher DEFAULT_AUTH_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/api/v1/auth/login", "POST", false);
    private final AuthenticationManager authenticationManager;
    private final AuthenticationFailureHandler failureHandler = new SimpleUrlAuthenticationFailureHandler();
    private final AuthenticationSuccessHandler successHandler;


    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.successHandler = new JwtAuthenticationSuccessHandler(jwtService);
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (!DEFAULT_AUTH_PATH_REQUEST_MATCHER.matches(request)){
            filterChain.doFilter(request, response);
        } else {
            try {
                Authentication authenticationResult = attemptAuthentication(request);
                logger.debug("Authentication success for user %s".formatted(authenticationResult.getName()));
                this.successHandler.onAuthenticationSuccess(request, response, authenticationResult);
            } catch (AuthenticationException e){
                logger.debug("Authentication failed " + e.getMessage());
                this.failureHandler.onAuthenticationFailure(request, response, e);
            }
        }
    }

    private Authentication attemptAuthentication(HttpServletRequest request) throws AuthenticationException, IOException {
        JwtAuthenticationToken jwtAuthenticationToken = new ObjectMapper().readValue(request.getInputStream(), JwtAuthenticationToken.class);
        logger.debug("Authentication %s with password %s".formatted(jwtAuthenticationToken.username, jwtAuthenticationToken.password));
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(jwtAuthenticationToken.username, jwtAuthenticationToken.password);
        return authenticationManager.authenticate(usernamePasswordAuthenticationToken);
    }

    private record JwtAuthenticationToken(String username, String password){}
}
