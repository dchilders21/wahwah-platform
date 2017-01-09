package com.wahwahnetworks.platform.lib.openx;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.Proxy;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

/**
 * Created by jhaygood on 3/4/15. Updated by bbober 1/8/16
 */
public class OpenXRestTemplate extends RestTemplate {

    public OpenXRestTemplate(String accessToken){

        super();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory(){
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                super.prepareConnection(connection,httpMethod);
                connection.setRequestProperty("Cookie", "openx3_access_token=" + accessToken);
                if (httpMethod.equals("PUT"))
                {
                    connection.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01"); // Why is openx so specific?
                }
                else
                {
                    connection.setRequestProperty("Accept", "text/html"); // Why does openx need this?
                    connection.setRequestProperty("Content-Type", "application/json");
                }
            }

        };

        // Temporary uncomment this to proxy to Fiddler, Charles, etc
        //Proxy proxy= new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8888));
        //requestFactory.setProxy(proxy);

        setRequestFactory(requestFactory);
    }
}
