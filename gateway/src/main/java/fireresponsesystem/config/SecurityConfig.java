package fireresponsesystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.beans.factory.annotation.Value;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("${keycloak.auth-server-url}")
    private String keycloakBaseUrl;
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public SecurityWebFilterChain securityFilterChain(ServerHttpSecurity http) {
        return http
            .csrf(csrf -> csrf.disable())
            .authorizeExchange(auth -> auth
                .pathMatchers("/signup", "/login").permitAll()
                .anyExchange().authenticated()
            )
            .exceptionHandling(eh -> eh
                .authenticationEntryPoint((exchange, ex) ->
                    Mono.fromRunnable(() -> {
                        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    })
                )
            )
            .oauth2Login(oauth2 -> {})
            .oauth2ResourceServer(oauth2 -> oauth2.jwt())
            .build();
    }

    @Bean
    public ReactiveJwtDecoder jwtDecoder() {
        return NimbusReactiveJwtDecoder.withJwkSetUri(keycloakBaseUrl+"/realms/my-realm/protocol/openid-connect/certs").build();
    }
}

