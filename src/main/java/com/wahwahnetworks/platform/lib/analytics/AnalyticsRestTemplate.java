package com.wahwahnetworks.platform.lib.analytics;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by jhaygood on 2/20/16.
 */
public class AnalyticsRestTemplate extends RestTemplate {
    public AnalyticsRestTemplate(final String bearerToken){

        super();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory(){
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                super.prepareConnection(connection,httpMethod);
                connection.setRequestProperty("Authorization","Bearer " + bearerToken);
            }

        };

        // Proxy proxy= new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8888));
        // requestFactory.setProxy(proxy);

        setRequestFactory(requestFactory);

    }
}
