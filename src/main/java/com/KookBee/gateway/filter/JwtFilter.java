package com.KookBee.gateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import io.jsonwebtoken.*;

import javax.xml.bind.DatatypeConverter;
// 얜 그냥 jwt가 값이 있는지, 유효한지 검증하는 기능이다.
@Component
public class JwtFilter extends AbstractGatewayFilterFactory<Object> {
    // application.yml 파일에서 jwt.secret 값을 가져와서 변수에 저장한다
    @Value("${jwt.ACCESS_SECRET_KEY}")
    String secret;

    // AbstractGatewayFilterFactory 추상 클래스의 apply 메소드를 Override하여 JwtFilter를 생성한다
    @Override
    public GatewayFilter apply(Object config) {
        // 람다식을 사용하여 요청 처리를 수행한다
        return (((exchange, chain) -> {
            // 요청에서 헤더를 가져와서 인증 토큰이 있는지 확인한다
            ServerHttpRequest request = exchange.getRequest();
            if(!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)
                    ||request.getHeaders().get(HttpHeaders.AUTHORIZATION).size() == 0) {
                // 토큰이 없으면 에러 메시지를 리턴한다
                return onError(exchange,"token is null",HttpStatus.BAD_REQUEST);
            }
            // 인증 토큰이 유효한지 확인한다
            if(!isValidateToken(request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0))) {
                // 토큰이 유효하지 않으면 에러 메시지를 리턴한다
                return  onError(exchange, "Token Error", HttpStatus.UNAUTHORIZED);
            }
            // 인증 토큰이 유효하면 다음 요청 처리를 위해 chain.filter(exchange)을 리턴한다
            return chain.filter(exchange);
        } ));
    }

    // JWT 토큰이 유효한지 검증하는 메소드
    public boolean isValidateToken(String jwt) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(
                            DatatypeConverter.parseBase64Binary(secret)
                    )
                    .build()
                    .parseClaimsJws(jwt);
            // 토큰이 유효하면 id 값을 가지고 있는지 확인한다
            if(claimsJws.getBody().isEmpty() || claimsJws.getBody().get("id")==null) {
                return false;
            }
        } catch (Exception err) {
            // 예외가 발생하면 토큰이 유효하지 않은 것으로 처리한다
            return false;
        }
        // 모든 검증이 완료되면 true 값을 리턴한다
        return true;
    }

    // 에러 메시지를 출력하고 HTTP 응답 코드를 설정하는 메소드
    public Mono<Void> onError(ServerWebExchange exchange, String  err, HttpStatus status) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        System.out.println(err);
        // HTTP 응답을 완료하고 종료한다
        return response.setComplete();

    }

}
