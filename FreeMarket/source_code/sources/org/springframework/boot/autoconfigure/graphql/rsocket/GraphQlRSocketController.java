package org.springframework.boot.autoconfigure.graphql.rsocket;

import java.util.Map;
import org.springframework.graphql.server.GraphQlRSocketHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/spring-boot-autoconfigure-2.7.12.jar:org/springframework/boot/autoconfigure/graphql/rsocket/GraphQlRSocketController.class */
class GraphQlRSocketController {
    private final GraphQlRSocketHandler handler;

    GraphQlRSocketController(GraphQlRSocketHandler handler) {
        this.handler = handler;
    }

    @MessageMapping({"${spring.graphql.rsocket.mapping}"})
    Mono<Map<String, Object>> handle(Map<String, Object> payload) {
        return this.handler.handle(payload);
    }

    @MessageMapping({"${spring.graphql.rsocket.mapping}"})
    Flux<Map<String, Object>> handleSubscription(Map<String, Object> payload) {
        return this.handler.handleSubscription(payload);
    }
}
