package com.dutchpay.howmuch.config;

import com.dutchpay.howmuch.config.jwt.JwtAccessDeniedHandler;
import com.dutchpay.howmuch.config.jwt.JwtAuthenticationEntryPoint;
import com.dutchpay.howmuch.config.jwt.JwtAuthenticationFilter;
import com.dutchpay.howmuch.config.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig {
    private final CorsConfigurationSource corsConfigurationSource;
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final RedisTemplate<String, String> redisTemplate;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .httpBasic(httpBasic -> httpBasic.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/admin/**").hasRole("Role_ADMIN")
                        .requestMatchers(HttpMethod.POST, "/member/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/member/**").authenticated()
                        .requestMatchers(HttpMethod.PATCH, "/member/**").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/member/**").authenticated()
                        .anyRequest().permitAll())
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
