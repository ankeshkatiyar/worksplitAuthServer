package com.worksplit.auth.server.authconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpointAuthenticationFilter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import javax.sql.DataSource;

/**
 * This class is responsible for the actual creation of the authorization server.This will create the JWT token
 * after authenticating the user.
 */
@EnableAuthorizationServer
@Configuration
public class OAuth2ServerConfiguration extends AuthorizationServerConfigurerAdapter {

    private boolean checkUserScope = true;
    /**
     * This service will fetch the user data based on the user name.
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * This is the data source which in this case is mysql
     */
    @Autowired
    private DataSource dataSource;

    /**
     * This bean is created in web server configurer
     */

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    @Autowired
    private ClientDetailsService clientDetailsService;

    /**
     * This will return the token store which will contain the enhanced token
     * @return
     */
    @Bean
    public TokenStore tokenStore(){
        return  new JwtTokenStore(jwtTokenEnhancer());
    }

    /**
     * This is to filter out the request for the token after doing the authentication
     * @return
     */
    @Bean
    public TokenEndpointAuthenticationFilter tokenEndpointAuthenticationFilter(){
        return new TokenEndpointAuthenticationFilter(authenticationManager , requestFactory());
    }

    /**
     * To create teh actual token
     * @return
     */
    @Bean
    public OAuth2RequestFactory requestFactory() {
        return  new TokenRequestFactory(clientDetailsService);
    }

    @Bean
    public JwtAccessTokenConverter jwtTokenEnhancer() {

        JwtAccessTokenConverter jwtAccessTokenConverter = new AppJwtAccessTokenConverter();
        //reading the jwt file for the creation of the jwt token
        jwtAccessTokenConverter.setKeyPair(new KeyStoreKeyFactory(new ClassPathResource("jwt.jks") , "password".toCharArray()).getKeyPair("jwt"));
        return  jwtAccessTokenConverter;

    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
       endpoints.
               tokenStore(tokenStore()).
               tokenEnhancer(jwtTokenEnhancer()).
               authenticationManager(authenticationManager).
               userDetailsService(userDetailsService);
       if(checkUserScope){
           endpoints.requestFactory(requestFactory());
       }
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
       security.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        //This will read the client details from the database(oauth_client_details) table.
        clients.jdbc(dataSource).passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
    }
}
