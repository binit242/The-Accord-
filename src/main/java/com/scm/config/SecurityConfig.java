package com.scm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.scm.services.impl.SecurityCustomUserDetailService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private SecurityCustomUserDetailService userDetailService;
    
    @Autowired
    private OAuthAuthenicationSuccessHandler handler;
    
    @Autowired
    private AuthFailtureHandler authFailtureHandler;

    @Value("${app.oauth.enabled:false}")
    private boolean oauthEnabled;

    @Value("${app.oauth.google.client-id:}")
    private String googleClientId;

    @Value("${app.oauth.google.client-secret:}")
    private String googleClientSecret;

    @Value("${app.oauth.github.client-id:}")
    private String githubClientId;

    @Value("${app.oauth.github.client-secret:}")
    private String githubClientSecret;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userDetailService);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        return daoAuthenticationProvider;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            ObjectProvider<ClientRegistrationRepository> clientRegistrationRepositoryProvider) throws Exception {
        httpSecurity
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(authorize -> {
                authorize.requestMatchers("/do-register", "/api/**").permitAll();
                authorize.requestMatchers("/user/**").authenticated();
                authorize.anyRequest().permitAll();
            })
            .formLogin(formLogin -> {
                formLogin.loginPage("/login");
                formLogin.loginProcessingUrl("/authenticate");
                formLogin.successForwardUrl("/user/profile");
                formLogin.usernameParameter("email");
                formLogin.passwordParameter("password");
                formLogin.failureHandler(authFailtureHandler);
            });

        if (oauthEnabled && clientRegistrationRepositoryProvider.getIfAvailable() != null) {
            httpSecurity.oauth2Login(oauth -> {
                    oauth.loginPage("/login");
                    oauth.successHandler(handler);
                });
        }

        httpSecurity.logout(logoutForm -> {
                logoutForm.logoutUrl("/do-logout");
                logoutForm.logoutSuccessUrl("/login?logout=true");
            });

        return httpSecurity.build();
    }

    @Bean
    @ConditionalOnProperty(name = "app.oauth.enabled", havingValue = "true")
    public ClientRegistrationRepository clientRegistrationRepository() {
        List<ClientRegistration> registrations = new ArrayList<>();

        if (hasText(googleClientId) && hasText(googleClientSecret)) {
            registrations.add(CommonOAuth2Provider.GOOGLE
                    .getBuilder("google")
                    .clientId(googleClientId)
                    .clientSecret(googleClientSecret)
                    .scope("email", "profile")
                    .build());
        }

        if (hasText(githubClientId) && hasText(githubClientSecret)) {
            registrations.add(CommonOAuth2Provider.GITHUB
                    .getBuilder("github")
                    .clientId(githubClientId)
                    .clientSecret(githubClientSecret)
                    .scope("read:user", "user:email")
                    .build());
        }

        if (registrations.isEmpty()) {
            throw new IllegalStateException("OAuth is enabled, but no Google or GitHub client credentials are configured.");
        }

        return new InMemoryClientRegistrationRepository(registrations);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
