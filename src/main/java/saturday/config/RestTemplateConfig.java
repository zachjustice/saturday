package saturday.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import saturday.interceptors.LoggingClientHttpRequestInterceptor;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new LoggingClientHttpRequestInterceptor());

        builder.requestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
        builder.interceptors(interceptors);
        return builder.build();
    }
}
