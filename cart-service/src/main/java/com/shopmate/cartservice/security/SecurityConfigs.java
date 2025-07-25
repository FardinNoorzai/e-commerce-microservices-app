package com.shopmate.cartservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigs {

    @Autowired
    JwtFilter jwtFilter;
    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http.
                csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((authorizationManagerRequestMatcherRegistry ->{
                    authorizationManagerRequestMatcherRegistry.anyRequest().authenticated();
                }))
                .build();
    }

}
