package com.wahwahnetworks.platform.lib.google.adx;

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow;
import com.google.api.client.auth.oauth2.AuthorizationCodeResponseUrl;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.servlet.auth.oauth2.AbstractAuthorizationCodeCallbackServlet;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.adexchangeseller.AdExchangeSellerScopes;
import com.wahwahnetworks.platform.lib.google.RedPandaDataStoreFactory;
import com.wahwahnetworks.platform.lib.google.RedPandaGoogleCredentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

/**
 * Created by jhaygood on 4/11/16.
 */
public class RedPandaGoogleAdExchangeCallbackServlet extends AbstractAuthorizationCodeCallbackServlet {

    @Autowired
    private RedPandaDataStoreFactory dataStoreFactory;

    @Override
    public void init() throws ServletException {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext (this);
        super.init();
    }

    @Override
    protected AuthorizationCodeFlow initializeFlow() throws ServletException, IOException {
        return new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(),
                JacksonFactory.getDefaultInstance(),
                RedPandaGoogleCredentials.GOOGLE_CLIENT_ID,
                RedPandaGoogleCredentials.GOOGLE_CLIENT_SECRET,
                Collections.singleton(AdExchangeSellerScopes.ADEXCHANGE_SELLER_READONLY)
        ).setDataStoreFactory(dataStoreFactory).setAccessType("offline").build();
    }

    @Override
    protected String getRedirectUri(HttpServletRequest req) throws ServletException, IOException {
        GenericUrl url = new GenericUrl(req.getRequestURL().toString());
        url.setRawPath("/google/adx/callback");
        return url.build();
    }

    @Override
    protected String getUserId(HttpServletRequest req) throws ServletException, IOException {

        Boolean isLoggedIn = (Boolean) req.getSession().getAttribute("logged_in");
        Integer userId = (Integer) req.getSession().getAttribute("user_id");

        if(isLoggedIn == null || isLoggedIn == false || userId == null){
            throw new ServletException("User not logged in");
        }

        return req.getSession().getAttribute("user_id").toString() + "-AdExchange";
    }

    @Override
    protected void onSuccess(HttpServletRequest req, HttpServletResponse resp, Credential credential) throws ServletException, IOException {
        resp.sendRedirect("/#/demand-sources/google-callback/adx?token=" + credential.getRefreshToken());
    }

    @Override
    protected void onError(HttpServletRequest req, HttpServletResponse resp, AuthorizationCodeResponseUrl errorResponse) throws ServletException, IOException {
        resp.sendRedirect("/#/demand-sources/google-callback/adx?error=true");
    }

}
