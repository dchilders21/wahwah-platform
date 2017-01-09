package com.wahwahnetworks.platform.lib;

import org.apache.commons.codec.binary.Base64;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;

/**
 * Created by Justin on 5/19/2014.
 */
public class BasicAuthRestTemplate extends RestTemplate {

    public BasicAuthRestTemplate(final String username, final String password){

        super();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory(){
            @Override
            protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
                super.prepareConnection(connection,httpMethod);

                String auth = username + ":" + password;
                byte[] encodedAuth = Base64.encodeBase64(auth.getBytes());
                connection.setRequestProperty("Authorization","Basic " + new String(encodedAuth));
            }

        };

       // Proxy proxy= new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8888));
       // requestFactory.setProxy(proxy);

        setRequestFactory(requestFactory);

    }
}
