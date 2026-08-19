package org.yap.mymarketapp.handlers;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
public class TemplateModelHelper {

    public Mono<Map<String, Object>> withSecurity(ServerRequest request, Map<String, Object> base) {
        return securityAttributes(request).map(extra -> {
            var model = new HashMap<>(base);
            model.putAll(extra);
            return model;
        });
    }

    private Mono<Map<String, Object>> securityAttributes(ServerRequest request) {
        var username = ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .filter(this::isAuthenticated)
                .map(Authentication::getName);

        var csrf = csrfToken(request);

        return username.flatMap(name -> csrf.map(token -> {
            Map<String, Object> attributes = new HashMap<>();
            attributes.put("username", name);
            attributes.put("csrf", token);
            return attributes;
        })).defaultIfEmpty(Map.of());
    }

    private boolean isAuthenticated(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);
    }

    private Mono<CsrfToken> csrfToken(ServerRequest request) {
        return Mono.defer(() -> {
            Mono<CsrfToken> token = request.exchange().getAttribute(CsrfToken.class.getName());
            return token != null ? token : Mono.empty();
        });
    }
}