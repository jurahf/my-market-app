package org.yap.mymarketapp.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.yap.mymarketapp.openapi.ApiClient;

@Configuration
public class PaymentClientConfig {

    @Value("${payment.service.base-url:http://localhost:8081}")
    private String baseUrl;

    @Bean
    public ApiClient paymentApiClient() {
        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();

        return new ApiClient(webClient);
    }
}
