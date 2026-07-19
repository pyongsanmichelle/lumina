package com.example.api.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

/**
 * APIサーバーのセキュリティ設定クラス。
 *
 * <p>本サービスはステートレスなOAuth2リソースサーバーとして構成する。
 * BFFからToken Relayで渡されるJWT（Keycloak発行）を検証し、
 * 検証に成功したリクエストのみ業務エンドポイントへのアクセスを許可する。
 * 認証されていないアクセスに対してはリダイレクトせず 401 を返す。
 */
@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ステートレスAPIかつBearerトークン認証のためCSRF保護は不要
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                // 監視用ヘルスチェックのみ認証不要
                .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                // それ以外はすべてJWT検証済みであることを要求
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
            .exceptionHandling(ex ->
                ex.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        return http.build();
    }
}
