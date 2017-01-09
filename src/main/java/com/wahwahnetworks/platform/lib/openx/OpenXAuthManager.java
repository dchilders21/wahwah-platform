package com.wahwahnetworks.platform.lib.openx;

import org.scribe.exceptions.OAuthException;
import org.scribe.model.*;
import org.scribe.oauth.OAuthService;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * Created by jhaygood on 3/4/15.
 */
public class OpenXAuthManager {

    private String OAUTH_CONSUMERKEY;
    private String OAUTH_CONSUMERSECRET;
    private String OAUTH_REALM;

    private String OPENX_USERNAME;
    private String OPENX_PASSWORD;

    private OAuthConfig config;
    private OpenXAPI api;
    private OAuthService service;

    private String accessToken;

    public OpenXAuthManager(String consumerKey, String consumerSecret, String username, String password, String realm){
        OAUTH_CONSUMERKEY = consumerKey;
        OAUTH_CONSUMERSECRET = consumerSecret;

        OPENX_USERNAME = username;
        OPENX_PASSWORD = password;

        OAUTH_REALM = realm;

        config = new OAuthConfig(OAUTH_CONSUMERKEY,OAUTH_CONSUMERSECRET,OAuthConstants.OUT_OF_BAND,SignatureType.Header,null,null);
        api = new OpenXAPI();
        service = api.createService(config);
    }

    public String getAccessToken(){

        if(accessToken == null) {
            boolean success = false;

            while(!success) {
                try {
                    Token requestToken = getRequestToken(api, service, config);
                    OpenXToken tokenData = getOpenXToken(requestToken);
                    accessToken = tokenData.getOauthToken();

                    success = true;
                } catch (OAuthException exception) {
                    if (exception.getMessage().equals("Response body is incorrect. Can't extract token and secret from this: 'Invalid Request: Missing nonce'")) {
                        success = false;
                    }
                }
            }
        }

        return accessToken;
    }

    public OpenXRestTemplate getRestTemplate(){
        return new OpenXRestTemplate(getAccessToken());
    }

    private Token getRequestToken(OpenXAPI api, OAuthService service, OAuthConfig config){
        config.log("obtaining request token from " + api.getRequestTokenEndpoint());
        OAuthRequest request = new OAuthRequest(api.getRequestTokenVerb(), api.getRequestTokenEndpoint());
        request.setRealm(OAUTH_REALM);

        config.log("setting oauth_callback to " + config.getCallback());
        request.addOAuthParameter(OAuthConstants.CALLBACK, config.getCallback());
        service.signRequest(OAuthConstants.EMPTY_TOKEN,request);

        config.log("sending request...");
        Response response = request.send();
        String body = response.getBody();

        config.log("response status code: " + response.getCode());
        config.log("response body: " + body);
        return api.getRequestTokenExtractor().extract(body);
    }

    private OpenXToken getOpenXToken(Token requestToken){
        String url = "https://sso.openx.com/login/process";

        MultiValueMap<String, Object> values = new LinkedMultiValueMap<>();
        values.set("email",OPENX_USERNAME);
        values.set("password",OPENX_PASSWORD);
        values.set("oauth_token",requestToken.getToken());

        RestTemplate restTemplate = new RestTemplate();
        String tokenString = restTemplate.postForObject(url,values, String.class);
        OpenXToken token = new OpenXToken(tokenString);

        return token;
    }
}