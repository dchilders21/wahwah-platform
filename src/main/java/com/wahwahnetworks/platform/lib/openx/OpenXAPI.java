package com.wahwahnetworks.platform.lib.openx;

import org.scribe.builder.api.DefaultApi10a;
import org.scribe.model.Token;

/**
 * Created by jhaygood on 3/4/15.
 */
public class OpenXAPI extends DefaultApi10a {

    private String OX_SSO_REQUESTTOKEN_URL = "https://sso.openx.com/api/index/initiate";
    private String OX_SSO_ACCESSTOKEN_URL = "https://sso.openx.com/api/index/token";
    private String OX_SSO_REALM = "wahwah";
    private String OX_SSO_AUTHORIZE_URL = "https://sso.openx.com/login/login";
    private String OX_SSO_LOGIN_URL = "https://sso.openx.com/login/process";

    @Override
    public String getRequestTokenEndpoint() {
        return OX_SSO_REQUESTTOKEN_URL;
    }

    @Override
    public String getAccessTokenEndpoint() {
        return OX_SSO_ACCESSTOKEN_URL;
    }

    @Override
    public String getAuthorizationUrl(Token requestToken) {
        return OX_SSO_AUTHORIZE_URL;
    }
}