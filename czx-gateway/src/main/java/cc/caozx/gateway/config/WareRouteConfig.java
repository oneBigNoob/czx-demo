package cc.caozx.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WareRouteConfig {
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes().route(router -> router
                .path("/api/ware/**")
                .filters(f -> f.stripPrefix(2))
                .uri("lb://ware-server")
        ).build();
    }
}
