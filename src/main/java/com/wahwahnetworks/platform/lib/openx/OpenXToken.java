package com.wahwahnetworks.platform.lib.openx;

/**
 * Created by jhaygood on 3/4/15.
 */
public class OpenXToken {
    private String oauthToken;
    private String oauthVerifier;

    public OpenXToken(String token, String verifier){
        this.oauthToken = token;
        this.oauthVerifier = verifier;
    }

    public OpenXToken(String loginResponse){
        String loginResponseWithoutOOB = loginResponse.replace("oob?","");
        oauthToken = loginResponseWithoutOOB.substring(loginResponseWithoutOOB.indexOf("=") + 1,loginResponseWithoutOOB.indexOf("&"));
        oauthVerifier = loginResponseWithoutOOB.substring(loginResponseWithoutOOB.indexOf("&") + "oauth_verifier=".length() + 1);
    }

    public String getOauthVerifier(){
        return oauthVerifier;
    }

    public String getOauthToken(){
        return oauthToken;
    }
}