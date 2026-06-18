package com.example.oauth2_fine_grained_authorization.config;

import org.keycloak.adapters.authorization.integration.jakarta.ServletPolicyEnforcerFilter;
import org.keycloak.adapters.authorization.spi.ConfigurationResolver;
import org.keycloak.adapters.authorization.spi.HttpRequest;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth ->
                        auth
                                .anyRequest()
                                .authenticated())
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwtConfigurer -> jwtConfigurer
                                .jwtAuthenticationConverter(getJwtAuthenticationConverter())))
                .addFilterAfter(servletPolicyEnforcerFilter(), BearerTokenAuthenticationFilter.class)
                .build();

    }

    @Bean
    public JwtAuthenticationConverter getJwtAuthenticationConverter() {

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            var clientRole = (Map<String, Object>) jwt.getClaims().get("resource_access");

            if(clientRole == null) return Collections.emptyList();

            var testClient = (Map<String, Object>) clientRole.get("fine-grained-authorization-client");

            if(testClient == null) return Collections.emptyList();

            var roles = (Collection<String>) testClient.get("roles");

            return roles.stream().map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        });
        return jwtAuthenticationConverter;
    }


    private ServletPolicyEnforcerFilter servletPolicyEnforcerFilter() {
        PolicyEnforcerConfig config;

        try(InputStream inputStream = SecurityConfig.class.getClassLoader().getResourceAsStream("keycloak.json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            config = objectMapper.readValue(inputStream, PolicyEnforcerConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new ServletPolicyEnforcerFilter(httpRequest -> config);
    }
}
