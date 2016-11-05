/*
 * Copyright (c) 2004-2012 The YAWL Foundation. All rights reserved.
 * The YAWL Foundation is a collaboration of individuals and
 * organisations who are committed to improving workflow technology.
 *
 * This file is part of YAWL. YAWL is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation.
 *
 * YAWL is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with YAWL. If not, see <http://www.gnu.org/licenses/>.
 */
package org.yawlfoundation.yawl.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.yawlfoundation.yawl.oauth.UniqueAuthenticationKeyGenerator;


/**
 * The application configuration for the authentication service.
 *
 * @author Philipp R. Thomas
 * @date 2016-12-15
 */
@Configuration
@Import(value = {SecurityConfig.class})
@PropertySources(value = {@PropertySource("classpath:/application.properties")})
@EnableAuthorizationServer
@EnableWebMvc()
@ComponentScan(basePackages = { "org.yawlfoundation.yawl.oauth.controller",
                                "org.yawlfoundation.yawl.resourcing.exception"})
public class ApplicationConfig {

    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Value("${oauth.client.yawl-resource-service.username:yawl-resource-service}")
        private String resourceServiceUsername;

        @Value("${oauth.client.yawl-resource-service.password:yawl-resource-service}")
        private String resourceServicePassword;

        @Value("${oauth.client.yawl-dashboard-backend.username:yawl-dashboard-backend}")
        private String dashboardServiceUsername;

        @Value("${oauth.client.yawl-dashboard-backend.password:yawl-dashboard-backend}")
        private String dashboardServicePassword;


        @Autowired
        @Qualifier("authenticationManagerBean")
        private AuthenticationManager authenticationManager;

        @Autowired
        @Qualifier("userDetailsServiceBean")
        private UserDetailsService userDetailsService;

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
            clients.inMemory()
                    .withClient("yawl-web-admin")
                        .authorizedGrantTypes("password", "refresh_token")
                        .authorities("ROLE_CLIENT")
                        .scopes("read", "write", "trust")
                        .accessTokenValiditySeconds(300)
                    .and()
                    .withClient(resourceServiceUsername)
                        .secret(resourceServicePassword)
                        .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                        .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                        .scopes("read", "write", "trust")
                        .accessTokenValiditySeconds(300)
                    .and()
                    .withClient(dashboardServiceUsername)
                        .secret(dashboardServicePassword)
                        .authorizedGrantTypes("password", "authorization_code", "refresh_token", "implicit")
                        .authorities("ROLE_CLIENT", "ROLE_TRUSTED_CLIENT")
                        .scopes("read", "write", "trust")
                        .accessTokenValiditySeconds(300);

        }

        @Bean
        public TokenStore tokenStore() {
            InMemoryTokenStore tokenStore = new InMemoryTokenStore();
            tokenStore.setAuthenticationKeyGenerator(new UniqueAuthenticationKeyGenerator());
            return tokenStore;
        }

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
            endpoints.tokenStore(tokenStore())
                     .userDetailsService(userDetailsService)
                     .authenticationManager(authenticationManager);
        }

        @Override
        public void configure(AuthorizationServerSecurityConfigurer server) throws Exception {
            server
                    .tokenKeyAccess("isAnonymous() || hasAuthority('ROLE_CLIENT')")
                    .checkTokenAccess("hasAuthority('ROLE_CLIENT')");
        }
    }


    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigIn() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
