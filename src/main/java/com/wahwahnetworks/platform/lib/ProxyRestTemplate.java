package com.wahwahnetworks.platform.lib;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.Proxy;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;

/**
 * Created by Brian.Bober 2/11/16
 */
public class ProxyRestTemplate extends RestTemplate {

    public ProxyRestTemplate(){

        super();
/*
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

        // Temporary uncomment this to proxy to Fiddler, Charles, etc
        Proxy proxy= new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8888));
        requestFactory.setProxy(proxy);

        setRequestFactory(requestFactory);*/
    }
}
