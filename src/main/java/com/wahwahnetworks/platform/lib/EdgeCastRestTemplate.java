package com.wahwahnetworks.platform.lib;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * Created by jhaygood on 2/21/15.
 */
public class EdgeCastRestTemplate extends RestTemplate {

    public EdgeCastRestTemplate(String token){
        super();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory(){
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                super.prepareConnection(connection,httpMethod);
                connection.setRequestProperty("Authorization","TOK:" + token);
            }

        };

        //Proxy proxy= new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8888));
        //requestFactory.setProxy(proxy);

        setRequestFactory(requestFactory);
    }
}
