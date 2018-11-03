package com.worksplit.auth.server.authconfig;

import com.worksplit.auth.server.userconfig.User;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class is responsible to enhance the existing oauth access token to jwt token
 * This is also responsible for addition the additional information about the user
 * inside the jwt .
 *
 */
public class AppJwtAccessTokenConverter extends JwtAccessTokenConverter {

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        User user = (User)authentication.getPrincipal();
        Map<String , Object>  info =  new LinkedHashMap<>(accessToken.getAdditionalInformation());
        info.put("email" , user.getEmail());
        info.put("username" , user.getUsername());
        DefaultOAuth2AccessToken defaultOAuth2AccessToken = new DefaultOAuth2AccessToken(accessToken);
        defaultOAuth2AccessToken.setAdditionalInformation(info);
        return super.enhance(defaultOAuth2AccessToken , authentication);

    }
}
