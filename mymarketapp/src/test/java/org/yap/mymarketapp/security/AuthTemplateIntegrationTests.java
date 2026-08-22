package org.yap.mymarketapp.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.ExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.yap.mymarketapp.dtos.CartResponse;
import org.yap.mymarketapp.dtos.ItemDto;
import org.yap.mymarketapp.dtos.PagingDto;
import org.yap.mymarketapp.dtos.SearchRequest;
import org.yap.mymarketapp.dtos.SearchResponse;
import org.yap.mymarketapp.dtos.SortFieldEnum;
import org.yap.mymarketapp.services.CartService;
import org.yap.mymarketapp.services.ItemService;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthTemplateIntegrationTests {

    @DynamicPropertySource
    static void isolatedDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:h2:mem:///memdb_auth;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=FALSE;CASE_INSENSITIVE_IDENTIFIERS=TRUE");
    }

    @LocalServerPort
    private int port;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private CartService cartService;

    private WebTestClient webTestClient;

    private String sessionCookie;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
        sessionCookie = null;
    }

    @Test
    void authenticatedPages_shouldRenderUsernameAndLogoutForm_withWorkingLogout() {
        login();

        var itemsHtml = getPageHtml("/items");
        assertThat(itemsHtml).contains("Пользователь: user");
        assertThat(itemsHtml).contains("Выйти");
        assertThat(itemsHtml).containsPattern("name=\"_csrf\"");
        assertThat(itemsHtml).contains("Заказы");
        assertThat(itemsHtml).contains("Корзина");
        assertThat(itemsHtml).doesNotContain("href=\"/login\"");
        assertThat(itemsHtml).contains("value=\"MINUS\"");

        var itemHtml = getPageHtml("/items/1");
        assertThat(itemHtml).contains("Пользователь: user");
        assertThat(itemHtml).contains("Выйти");
        assertThat(itemHtml).contains("Заказы");
        assertThat(itemHtml).doesNotContain("href=\"/login\"");
        assertThat(itemHtml).contains("value=\"PLUS\"");

        var cartHtml = getPageHtml("/cart/items");
        assertThat(cartHtml).contains("Пользователь: user");
        assertThat(cartHtml).contains("Выйти");

        var ordersHtml = getPageHtml("/orders");
        assertThat(ordersHtml).contains("Пользователь: user");
        assertThat(ordersHtml).contains("Выйти");

        var orderHtml = getPageHtml("/orders/1");
        assertThat(orderHtml).contains("Пользователь: user");
        assertThat(orderHtml).contains("Выйти");

        var logoutCsrf = extractCsrf(ordersHtml);
        webTestClient.post().uri("/logout")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.COOKIE, sessionCookie)
                .bodyValue("_csrf=" + logoutCsrf)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueMatches("Location", ".*login.*");

        webTestClient.get().uri("/orders")
                .header(HttpHeaders.COOKIE, sessionCookie)
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    void anonymousPages_shouldHideCartActionsAndShowLoginButton() {
        stubItemService();

        for (var uri : List.of("/items", "/items/1")) {
            var html = getBody(webTestClient.get().uri(uri).exchange().expectStatus().isOk());

            assertThat(html).as(uri).isNotNull();
            assertThat(html).doesNotContain("Пользователь:");
            assertThat(html).doesNotContain("Выйти");
            assertThat(html).doesNotContain("Заказы");
            assertThat(html).doesNotContain("Корзина");
            assertThat(html).as(uri).contains("Войти");
            assertThat(html).as(uri).contains("href=\"/login\"");
            assertThat(html).as(uri).doesNotContain("value=\"MINUS\"");
            assertThat(html).as(uri).doesNotContain("value=\"PLUS\"");
        }
    }

    private void login() {
        var loginPageResult = webTestClient.get().uri("/login")
                .exchange()
                .expectStatus().isOk()
                .returnResult(String.class);
        trackSession(loginPageResult);

        var csrf = extractCsrf(bodyOf(loginPageResult));

        var loginPost = webTestClient.post().uri("/login")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.COOKIE, sessionCookie)
                .bodyValue("username=user&password=password&_csrf=" + csrf)
                .exchange()
                .expectStatus().is3xxRedirection()
                .returnResult(Void.class);
        trackSession(loginPost);
    }

    private String getPageHtml(String uri) {
        stubItemService();
        return getBody(webTestClient.get().uri(uri).header(HttpHeaders.COOKIE, sessionCookie)
                .exchange().expectStatus().isOk());
    }

    private String getBody(WebTestClient.ResponseSpec spec) {
        return bodyOf(spec.returnResult(String.class));
    }

    private String bodyOf(ExchangeResult result) {
        return new String(result.getResponseBodyContent(), StandardCharsets.UTF_8);
    }

    private void trackSession(ExchangeResult result) {
        var setCookies = result.getResponseHeaders().get(HttpHeaders.SET_COOKIE);
        if (setCookies != null && !setCookies.isEmpty()) {
            sessionCookie = setCookies.get(0).split(";")[0];
        }
    }

    private void stubItemService() {
        var paging = new PagingDto(0, 5, false, false);
        var itemDto = new ItemDto(1L, "Item1", "Desc1", "/img.png", 100L, 0);
        when(itemService.getAll(any(SearchRequest.class)))
                .thenReturn(Mono.just(new SearchResponse("", SortFieldEnum.NO, paging, List.of(List.of(itemDto)))));
        when(itemService.getById(1L, 1)).thenReturn(Mono.just(itemDto));
        when(cartService.getItemsInCart()).thenReturn(Mono.just(new CartResponse(List.of(itemDto), 100L, 400L)));
    }

    private String extractCsrf(String html) {
        assertThat(html).isNotNull();
        Matcher matcher = Pattern.compile("name=\"_csrf\"[^>]*value=\"([^\"]+)\"").matcher(html);
        assertThat(matcher.find()).as("csrf token should be present in: " + html).isTrue();
        return matcher.group(1);
    }
}