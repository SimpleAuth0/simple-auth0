package io.github.simpleauth0.security.configurers;

import io.github.simpleauth0.core.utils.SecurityConfigUtils;
import io.github.simpleauth0.security.AuthenticationTypeRepository;
import io.github.simpleauth0.security.CompositeLoginAuthenticationFilter;
import io.github.simpleauth0.security.convert.DelegatingAuthenticationConverter;
import io.github.simpleauth0.security.convert.UsernamePasswordAuthenticationConvert;
import io.github.simpleauth0.security.handler.CompositeLoginAuthenticationFailureHandler;
import io.github.simpleauth0.security.handler.CompositeLoginAuthenticationSuccessHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.AuthenticationConverter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: ReLive27
 * @date: 2025/5/25 20:46
 */
public class CompositeLoginAuthenticationConfigurer extends AbstractHttpConfigurer<CompositeLoginAuthenticationConfigurer, HttpSecurity> {
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/login",
            "POST");
    private RequestMatcher endpointsMatcher = DEFAULT_ANT_PATH_REQUEST_MATCHER;
    private AuthenticationSuccessHandler successHandler;
    private AuthenticationFailureHandler failureHandler;
    private List<AuthenticationConverter> authenticationConverterList = new ArrayList<>();
    private List<AuthenticationProvider> authenticationProviders = new ArrayList<>();
    private final Map<Class<? extends AbstractAuthenticationConfigurer>, AbstractAuthenticationConfigurer> configurers = createConfigurers();


    public CompositeLoginAuthenticationConfigurer endpointsMatcher(RequestMatcher endpointsMatcher) {
        Assert.notNull(endpointsMatcher, "endpointsMatcher cannot be null");
        this.endpointsMatcher = endpointsMatcher;
        return this;
    }

    public CompositeLoginAuthenticationConfigurer successHandler(AuthenticationSuccessHandler successHandler) {
        Assert.notNull(successHandler, "successHandler cannot be null");
        this.successHandler = successHandler;
        return this;
    }

    public CompositeLoginAuthenticationConfigurer failureHandler(AuthenticationFailureHandler failureHandler) {
        Assert.notNull(failureHandler, "failureHandler cannot be null");
        this.failureHandler = failureHandler;
        return this;
    }

    public CompositeLoginAuthenticationConfigurer usernameLogin(boolean enable, Customizer<UsernameAuthenticationConfigurer> usernameAuthenticationConfigurerCustomizer) {
        if (enable) {
            usernameAuthenticationConfigurerCustomizer.customize(getConfigurer(UsernameAuthenticationConfigurer.class));
        }
        return this;
    }

    public CompositeLoginAuthenticationConfigurer usernameLogin(Customizer<UsernameAuthenticationConfigurer> usernameAuthenticationConfigurerCustomizer) {
        usernameLogin(true, usernameAuthenticationConfigurerCustomizer);
        return this;
    }

    public CompositeLoginAuthenticationConfigurer smsLogin() {

        return this;
    }

    public CompositeLoginAuthenticationConfigurer mailLogin() {
        return this;
    }

    @Override
    public void init(HttpSecurity httpSecurity) throws Exception {
        this.configurers.values().forEach(configurer -> {
            configurer.init(httpSecurity);
        });
        Assert.notEmpty(this.authenticationProviders, "No authentication type specified. Please configure at least one supported authentication type (e.g., password, SMS code, email code).");
        this.authenticationProviders.stream()
                .forEach(authenticationProvider -> httpSecurity.authenticationProvider(this.postProcess(authenticationProvider)));
        super.init(httpSecurity);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        CompositeLoginAuthenticationFilter loginAuthenticationFilter = new CompositeLoginAuthenticationFilter(this.endpointsMatcher, authenticationManager);
        if (successHandler == null) {
            this.successHandler = new CompositeLoginAuthenticationSuccessHandler();
        }
        loginAuthenticationFilter.setAuthenticationSuccessHandler(this.successHandler);

        if (this.failureHandler == null) {
            this.failureHandler = new CompositeLoginAuthenticationFailureHandler();
        }
        loginAuthenticationFilter.setAuthenticationFailureHandler(this.failureHandler);

        loginAuthenticationFilter.setAuthenticationConverter(new DelegatingAuthenticationConverter(this.authenticationConverterList));

        http.addFilterBefore(this.postProcess(loginAuthenticationFilter), AbstractPreAuthenticatedProcessingFilter.class);
    }

    public RequestMatcher getEndpointsMatcher() {
        return (request) -> this.endpointsMatcher.matches(request);
    }

    private Map<Class<? extends AbstractAuthenticationConfigurer>, AbstractAuthenticationConfigurer> createConfigurers() {
        Map<Class<? extends AbstractAuthenticationConfigurer>, AbstractAuthenticationConfigurer> configurers = new LinkedHashMap<>();
        configurers.put(UsernameAuthenticationConfigurer.class, new UsernameAuthenticationConfigurer());
        return configurers;
    }

    private <T> T getConfigurer(Class<T> type) {
        return (T) this.configurers.get(type);
    }


    class UsernameAuthenticationConfigurer extends AbstractAuthenticationConfigurer {
        private AuthenticationConverter authenticationConverter;
        private AuthenticationProvider authenticationProvider;


        public UsernameAuthenticationConfigurer authenticationConverter(AuthenticationConverter authenticationConverter) {
            Assert.notNull(authenticationConverter, "authenticationConverter cannot be null");
            this.authenticationConverter = authenticationConverter;
            return this;
        }

        public UsernameAuthenticationConfigurer authenticationProvider(AuthenticationProvider authenticationProvider) {
            Assert.notNull(authenticationProvider, "authenticationProvider cannot be null");
            this.authenticationProvider = authenticationProvider;
            return this;
        }

        @Override
        protected void init(HttpSecurity http) {
            if (this.authenticationProvider == null) {
                UserDetailsService userDetailsService = SecurityConfigUtils.getBean(http, UserDetailsService.class);
                Assert.notNull(userDetailsService, "No implementation of UserDetailsService found. Please provide a UserDetailsService Bean to load user authentication data.");
                DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
                daoAuthenticationProvider.setUserDetailsService(userDetailsService);

                UserDetailsPasswordService userDetailsPasswordService = SecurityConfigUtils.getBean(http, UserDetailsPasswordService.class);
                if (userDetailsPasswordService != null) {
                    daoAuthenticationProvider.setUserDetailsPasswordService(userDetailsPasswordService);
                }
                this.authenticationProvider = daoAuthenticationProvider;
            }

            authenticationProviders.add(this.authenticationProvider);

            if (this.authenticationConverter == null) {
                AuthenticationTypeRepository authenticationTypeRepository = SecurityConfigUtils.getBean(http, AuthenticationTypeRepository.class);
                Assert.notNull(authenticationTypeRepository, "No implementation of AuthenticationTypeRepository found. Please implement AuthenticationTypeRepository to provide supported authentication types.");
                this.authenticationConverter = new UsernamePasswordAuthenticationConvert(authenticationTypeRepository);
            }
            authenticationConverterList.add(this.authenticationConverter);
        }

    }

    abstract class AbstractAuthenticationConfigurer {
        protected abstract void init(HttpSecurity http);
    }
}
