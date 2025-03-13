package com.law.law_qa_system.configurations;

import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder;
import org.apache.hc.core5.http.ssl.TLS;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

@Configuration
public class AppConfig {
    @Bean
    public RestTemplate restTemplate() {
        try {
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial((chain, authType) -> true)
                    .build();

            SSLConnectionSocketFactory sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
                    .setSslContext(sslContext)
                    .setTlsVersions(TLS.V_1_2, TLS.V_1_3)
                    .setHostnameVerifier(new DefaultHostnameVerifier())
                    .build();

            PoolingHttpClientConnectionManager connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
                    .setSSLSocketFactory(sslSocketFactory)
                    .build();

            CloseableHttpClient httpClient = HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .build();

            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(httpClient);
            System.out.println("RestTemplate load success");
            return new RestTemplate(factory);
        } catch (Exception e) {
            System.err.println("Error configuring RestTemplate: " + e.getMessage());
            return new RestTemplate();
        }
    }
}
