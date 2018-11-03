package com.worksplit.auth.server.authconfig;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.Map;

public class TokenRequestFactory extends DefaultOAuth2RequestFactory {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenStore tokenStore;


    public TokenRequestFactory(ClientDetailsService clientDetailsService){
        super(clientDetailsService);
    }

    @Override
    public TokenRequest createTokenRequest(Map<String, String> requestParameters, ClientDetails authenticatedClient) {
        if(requestParameters.get("grant_type").equals("refresh_token")){
            OAuth2Authentication oAuth2Authentication =  tokenStore.readAuthenticationForRefreshToken(tokenStore.readRefreshToken(requestParameters.get("refresh_token")));
            SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(oAuth2Authentication.getName(),null,
                    userDetailsService.loadUserByUsername(oAuth2Authentication.getName()).getAuthorities()));


        }
        return  super.createTokenRequest(requestParameters,authenticatedClient);
    }
}
