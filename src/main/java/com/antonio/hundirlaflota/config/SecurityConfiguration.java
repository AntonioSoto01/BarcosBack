package com.antonio.hundirlaflota.config;


import com.antonio.hundirlaflota.config.jwt.JwtAuthenticationFilter;
import com.antonio.hundirlaflota.config.jwt.JwtTokenProvider;
import com.antonio.hundirlaflota.config.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.antonio.hundirlaflota.config.oauth2.OAuth2AuthenticationFailureHandler;
import com.antonio.hundirlaflota.config.oauth2.OAuth2AuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
    @Value("${frontend.url}")
    private String frontendUrl;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSucessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final JwtTokenProvider jwtTokenProvider;
    private final HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/**", "/home", "/login", "/api/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), BasicAuthenticationFilter.class)
                .sessionManagement(customizer -> customizer.
                        sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptionHandling -> exceptionHandling.
                        authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .csrf(AbstractHttpConfigurer::disable)
                .logout(l -> l
                        .logoutUrl("/api/logout")
                        .logoutSuccessUrl(frontendUrl + "/").permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(config -> config
                                .authorizationRequestRepository(cookieAuthorizationRequestRepository))
                        .successHandler(oAuth2AuthenticationSucessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler)

                );
        return http.build();
    }

}
