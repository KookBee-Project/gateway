package com.KookBee.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
/*
글로벌 필터는 서버로부터 받은 요청과 응답에 대한 로그를 기록한다. 걍 로그 기록이다.
 */
@Component
public class GlobalFilter extends AbstractGatewayFilterFactory<FilterConfig> {
    public GlobalFilter() {
        super(FilterConfig.class);
    }

    @Override
    public GatewayFilter apply(FilterConfig config) {

        return (((exchange, chain) -> { //(들어오는 물, 흐르는 물) // 키워드 : 비동기, 네티. 논블록킹
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
            if (config.isPreLogger()) {
                System.out.println(request.getId() + "pre : " + request.getPath() + ", " + request.getMethod());
            }
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if (config.isPostLogger()) {
                    System.out.println(request.getId() + "post : " + response.getStatusCode());
                }
            }));
        }));
    }
}
