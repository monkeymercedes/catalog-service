package com.polarbookshop.catalogservice.network;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("integration")
public class ConfigServiceReachableTests {

    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    @Value("${spring.cloud.config.uri}")
    String configServiceUrl;

    @Test
    void pingConfigServer() {
        Exception expectedException = new Exception();
        try {
            var res = REST_TEMPLATE.exchange(configServiceUrl, HttpMethod.GET, null, String.class);
        } catch (Exception e) {
            expectedException = e;
        }
        assertThat(expectedException.getClass().getSimpleName()).isEqualTo(HttpClientErrorException.NotFound.class.getSimpleName());
    }


}
