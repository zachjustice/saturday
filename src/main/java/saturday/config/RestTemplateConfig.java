package saturday.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import saturday.interceptors.LoggingClientHttpRequestInterceptor;

import java.util.Collections;
import java.util.function.Supplier;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {

        Supplier<ClientHttpRequestFactory> clientHttpRequestFactorySupplier = () ->
                new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory());

        builder.requestFactory(clientHttpRequestFactorySupplier);
        builder.interceptors(Collections.singletonList(new LoggingClientHttpRequestInterceptor()));
        return builder.build();
    }
}
