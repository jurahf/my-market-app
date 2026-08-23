package org.yap.mymarketapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.yap.mymarketapp.openapi.ApiClient;
import org.yap.mymarketapp.openapi.api.BalanceApi;

@Configuration
public class PaymentClientConfig {

    @Value("${payment.service.base-url:http://localhost:8081}")
    private String baseUrl;

    @Bean
    public AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager keycloakAuthorizedClientManager(
            ReactiveClientRegistrationRepository clientRegistrationRepository,
            ReactiveOAuth2AuthorizedClientService authorizedClientService) {
        var authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();
        var authorizedClientManager = new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                clientRegistrationRepository, authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }

    @Bean
    public ApiClient paymentApiClient(AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager keycloakAuthorizedClientManager) {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Client =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(keycloakAuthorizedClientManager);
        oauth2Client.setDefaultClientRegistrationId("paymentservice");

        WebClient webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .filter(oauth2Client)
                .build();

        return new ApiClient(webClient);
    }

    @Bean
    public BalanceApi balanceApi(ApiClient apiClient) {
        return new BalanceApi(apiClient);
    }
}