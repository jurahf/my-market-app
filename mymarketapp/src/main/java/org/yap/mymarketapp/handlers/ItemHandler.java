package org.yap.mymarketapp.handlers;

import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.util.UriComponentsBuilder;
import org.yap.mymarketapp.dtos.CartActionEnum;
import org.yap.mymarketapp.dtos.SearchRequest;
import org.yap.mymarketapp.dtos.SortFieldEnum;
import org.yap.mymarketapp.services.CartService;
import org.yap.mymarketapp.services.ItemService;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@Component
public class ItemHandler {

    private final ItemService service;
    private final CartService cartService;
    private final TemplateModelHelper templateModel;

    public ItemHandler(ItemService service, CartService cartService, TemplateModelHelper templateModel) {
        this.service = service;
        this.cartService = cartService;
        this.templateModel = templateModel;
    }

    public Mono<ServerResponse> searchItems(ServerRequest request) {
        var search = request.queryParam("search").orElse(null);
        var sort = request.queryParam("sort")
                .map(SortFieldEnum::valueOf)
                .orElse(SortFieldEnum.NO);
        var pageNumber = request.queryParam("pageNumber")
                .map(Integer::parseInt)
                .orElse(0);
        var pageSize = request.queryParam("pageSize")
                .map(Integer::parseInt)
                .orElse(5);

        return service.getAll(new SearchRequest(search, sort, pageNumber, pageSize))
                .flatMap(response -> templateModel.withSecurity(request, Map.of(
                        "items", response.items(),
                        "search", Optional.ofNullable(response.search()).orElse(""),
                        "sort", response.sort().toString(),
                        "paging", response.paging())))
                .flatMap(model -> ServerResponse.ok().render("items", model));
    }

    public Mono<ServerResponse> itemsToCart(ServerRequest request) {
        return request.formData()
                .flatMap(formData -> {
                    try {
                        var id = Long.parseLong(firstValue(request, formData, "id"));
                        var search = firstValue(request, formData, "search");
                        var sort = firstValue(request, formData, "sort") != null
                                ? SortFieldEnum.valueOf(firstValue(request, formData, "sort"))
                                : null;
                        var pageNumber = firstValue(request, formData, "pageNumber") != null
                                ? Integer.parseInt(firstValue(request, formData, "pageNumber"))
                                : null;
                        var pageSize = firstValue(request, formData, "pageSize") != null
                                ? Integer.parseInt(firstValue(request, formData, "pageSize"))
                                : null;
                        var action = CartActionEnum.valueOf(firstValue(request, formData, "action"));

                        return cartService.toCart(id, action)
                                .then(Mono.fromSupplier(() -> {
                                    var builder = UriComponentsBuilder.fromPath("/items");
                                    if (search != null) builder.queryParam("search", search);
                                    if (sort != null) builder.queryParam("sort", sort);
                                    if (pageNumber != null) builder.queryParam("pageNumber", pageNumber);
                                    if (pageSize != null) builder.queryParam("pageSize", pageSize);
                                    return builder.build().toUri();
                                }))
                                .flatMap(uri -> ServerResponse.seeOther(uri).build());
                    } catch (Exception e) {
                        return Mono.error(new IllegalArgumentException("Invalid form data", e));
                    }
                });
    }

    public Mono<ServerResponse> itemsToCartWithPath(ServerRequest request) {
        return request.formData()
                .flatMap(formData -> {
                    var id = Long.parseLong(request.pathVariable("id"));
                    var action =  CartActionEnum.valueOf(firstValue(request, formData, "action"));

                    return cartService.toCart(id, action)
                            .then(ServerResponse.seeOther(URI.create("/items/" + id)).build());
                });
    }

    private String firstValue(ServerRequest request, MultiValueMap<String, String> formData, String name) {
        return request.queryParam(name)
                .or(() -> Optional.ofNullable(formData.getFirst(name)))
                .orElse(null);
    }

    public Mono<ServerResponse> getItem(ServerRequest request) {
        var id = Long.parseLong(request.pathVariable("id"));
        return service.getById(id)
                .flatMap(item -> templateModel.withSecurity(request, Map.of("item", item)))
                .flatMap(model -> ServerResponse.ok().render("item", model));
    }
}
