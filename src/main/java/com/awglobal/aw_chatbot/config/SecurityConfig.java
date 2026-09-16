package com.awglobal.aw_chatbot.config;

import jakarta.servlet.DispatcherType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            JwtAuthenticationConverter jwtAuthenticationConverter)
            throws Exception {
        return http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .authorizeHttpRequests(auth -> auth
                        // Allow Spring's internal error dispatch.
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/chatbots")
                        .hasRole("CHATBOT_READ")

                        .requestMatchers(HttpMethod.POST, "/api/chatbots")
                        .hasRole("CHATBOT_WRITE")

                        .requestMatchers(HttpMethod.POST, "/api/chat")
                        .hasRole("CHATBOT_USE")

                        .anyRequest().denyAll()
                )

                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .jwtAuthenticationConverter(
                                        jwtAuthenticationConverter
                                )
                        )
                )

                .build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtAuthenticationConverter();
        var scopeConverter = new JwtGrantedAuthoritiesConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            // Preserve Spring's usual SCOPE_* authorities.
            var scopeAuthorities = scopeConverter.convert(jwt);
            if (scopeAuthorities != null) {
                authorities.addAll(scopeAuthorities);
            }

            // Convert Keycloak realm roles to Spring ROLE_* authorities.
            Object realmAccess = jwt.getClaims().get("realm_access");

            if (realmAccess instanceof Map<?, ?> realm
                    && realm.get("roles") instanceof Collection<?> roles) {

                for (Object role : roles) {
                    if (role instanceof String roleName) {
                        authorities.add(
                                new SimpleGrantedAuthority("ROLE_" + roleName)
                        );
                    }
                }
            }

            return authorities;
        });

        return converter;
    }
}
