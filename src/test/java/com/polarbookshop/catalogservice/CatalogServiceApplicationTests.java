package com.polarbookshop.catalogservice;

import com.polarbookshop.catalogservice.domain.Book;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("integration")
class CatalogServiceApplicationTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(CatalogServiceApplicationTests.class);
    private static final RestTemplate REST_TEMPLATE = new RestTemplate();

    @Value("${spring.cloud.config.uri}")
    String configServiceUrl;

    @Autowired
    WebTestClient webTestClient;

    @Test
    void whenPostRequestThenBookCreated() {
        LOGGER.info("[whenPostRequestThenBookCreated]");
        Exception expectedException = new Exception();
        try {
            LOGGER.info("Pinging config server...");
            var res = REST_TEMPLATE.exchange(configServiceUrl, HttpMethod.GET, null, String.class);
        } catch (Exception e) {
            expectedException = e;
            LOGGER.info("Got expected exception: ", e);
        }
        assertThat(expectedException.getClass().getSimpleName()).isEqualTo(HttpClientErrorException.NotFound.class.getSimpleName());

        LOGGER.info("Running actual test...");
        var expectedBook = Book.of("1231231231", "Title", "Author", 9.90, "Polarsophia");

        webTestClient
                .post()
                .uri("/books")
                .bodyValue(expectedBook)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class).value(actualBook -> {
                    LOGGER.info("Checking book values");
                    assertThat(actualBook).isNotNull();
                    assertThat(actualBook.isbn())
                            .isEqualTo(expectedBook.isbn());
                });
    }

}
