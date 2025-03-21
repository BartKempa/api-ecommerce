package com.example.apiecommerce.domain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
public class SecurityConfig {
    private final JwtService jwtService;
    private static final String USER_ROLE = "USER";
    private static final String ADMIN_ROLE = "ADMIN";
    private final UserSecurity userSecurity;

    public SecurityConfig(JwtService jwtService, UserSecurity userSecurity) {
        this.jwtService = jwtService;
        this.userSecurity = userSecurity;
    }

    @Bean
    MvcRequestMatcher.Builder mvc(HandlerMappingIntrospector introspector){
        return new MvcRequestMatcher.Builder(introspector);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           MvcRequestMatcher.Builder mvc,
                                           AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        AuthenticationManager authenticationManager = authenticationManagerBuilder.getOrBuild();
        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(authenticationManager, jwtService);
        BearerTokenFilter bearerTokenFilter = new BearerTokenFilter(jwtService);
        http.authorizeHttpRequests(request -> request
                        .requestMatchers("/api/v1/auth/register").permitAll()
                        .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/v1/users/user/{id}")).hasRole(ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/v1/users/**")).hasAnyRole(ADMIN_ROLE, USER_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.DELETE, "/api/v1/users/**")).hasRole(ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.PATCH, "/api/v1/users/**")).hasRole(USER_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/v1/orders/**")).hasRole(USER_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.DELETE, "/api/v1/orders/**")).hasRole(ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.PATCH, "/api/v1/orders/**")).hasRole(ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/v1/orders/page")).hasRole(ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/v1/orders/**")).hasAnyRole(USER_ROLE, ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/v1/deliveries/**")).hasRole(ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.PATCH, "/api/v1/deliveries/**")).hasRole(ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.DELETE, "/api/v1/deliveries/**")).hasRole(ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/v1/cartItems/**")).hasAnyRole(USER_ROLE, ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/v1/cartItems/**")).hasAnyRole(USER_ROLE, ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.PATCH, "/api/v1/cartItems/**")).hasAnyRole(USER_ROLE, ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.DELETE, "/api/v1/cartItems/**")).hasAnyRole(USER_ROLE, ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/v1/carts/**")).hasAnyRole(USER_ROLE, ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.DELETE, "/api/v1/carts/**")).hasAnyRole(USER_ROLE, ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/v1/carts/**")).hasAnyRole(USER_ROLE, ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/v1/addresses/**")).hasAnyRole(USER_ROLE, ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.DELETE, "/api/v1/addresses/**")).hasAnyRole(USER_ROLE, ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/v1/addresses/**")).hasAnyRole(USER_ROLE, ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.PATCH, "/api/v1/addresses/**")).hasAnyRole(USER_ROLE, ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/v1/products/**")).hasRole(ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.DELETE, "/api/v1/products/**")).hasRole(ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.PUT, "/api/v1/products/**")).hasRole(ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/v1/products/**")).hasAnyRole(USER_ROLE, ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.POST, "/api/v1/categories/**")).hasRole(ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.DELETE, "/api/v1/categories/**")).hasRole(ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.PUT, "/api/v1/categories/**")).hasRole(ADMIN_ROLE)
                        .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/v1/categories/**")).hasAnyRole(USER_ROLE, ADMIN_ROLE)
                        //.requestMatchers(mvc.pattern(HttpMethod.GET, "/api/v1/users/{id}/addresses")).access(userSecurity)
                        .requestMatchers(mvc.pattern(HttpMethod.GET, "/api/v1/**")).hasAnyRole(USER_ROLE, ADMIN_ROLE)
                        .anyRequest().permitAll())
                .sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtAuthenticationFilter, AuthorizationFilter.class)
                .addFilterBefore(bearerTokenFilter, AuthorizationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .headers(config -> config.frameOptions(
                HeadersConfigurer.FrameOptionsConfig::sameOrigin
        ));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
            return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
