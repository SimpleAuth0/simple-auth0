package io.github.simpleauth0.security.configuration;

import io.github.simpleauth0.core.handler.ExceptionMappingAuthenticationEntryPoint;
import io.github.simpleauth0.core.handler.HttpForbiddenAccessDeniedHandler;
import io.github.simpleauth0.security.configurers.CompositeLoginAuthenticationConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @author: ReLive27
 * @date: 2025/3/18 22:37
 */
@Configuration(proxyBeanMethods = false)
public class AuthnSecurityConfiguration {

    @Bean
    SecurityFilterChain authenticationSecurityFilterChain(HttpSecurity http) throws Exception {
        CompositeLoginAuthenticationConfigurer configurer = new CompositeLoginAuthenticationConfigurer();
        http.requestMatcher(configurer.getEndpointsMatcher()).authorizeHttpRequests(authorizeHttpRequest -> {
            authorizeHttpRequest.anyRequest().authenticated();
        }).apply(configurer)
                .usernameLogin(Customizer.withDefaults())
                .mailLogin()
                .and()
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(new ExceptionMappingAuthenticationEntryPoint())
                                .accessDeniedHandler(new HttpForbiddenAccessDeniedHandler()))
                .csrf(csrf -> csrf.ignoringRequestMatchers(configurer.getEndpointsMatcher()))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        return http.build();
    }

}
