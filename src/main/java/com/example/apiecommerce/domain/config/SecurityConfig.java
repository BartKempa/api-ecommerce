package com.example.apiecommerce.domain.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
public class SecurityConfig {
    private static final String USER_ROLE = "USER";
    private static final String ADMIN_ROLE = "ADMIN";

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector){
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MvcRequestMatcher.Builder mvc) throws Exception {
        http.authorizeHttpRequests(request -> request
                        .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/v1/**")).hasRole(ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.PUT, "/api/v1/**")).hasRole(ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.PATCH, "/api/v1/**")).hasRole(ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.DELETE, "/api/v1/**")).hasRole(ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/v1/**")).hasAnyRole(USER_ROLE, ADMIN_ROLE)
                        .anyRequest().permitAll())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(csrfCustomizer  -> csrfCustomizer .disable());
        return http.build();

    }

    @Bean
    public PasswordEncoder passwordEncoder(){

            return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
