package de.larsgrefer.sass.embedded.spring;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "logging.level.web=debug",
        "logging.level.org.springframework.web.servlet.resource=trace"
})
class ExampleAppTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void cssMinified() {
        ResponseEntity<String> testEntity = restTemplate.getForEntity("/test.css", String.class);

        assertThat(testEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        String body = testEntity.getBody();
        assertThat(body).isNotEmpty();

        assertThat(testEntity.getHeaders().getContentType().toString()).isEqualTo("text/css");
    }

    @Test
    void sassCompiledMinified() {
        ResponseEntity<String> testEntity = restTemplate.getForEntity("/test2.css", String.class);

        assertThat(testEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        String body = testEntity.getBody();
        assertThat(body).isNotEmpty();

        assertThat(testEntity.getHeaders().getContentType().toString()).isEqualTo("text/css");
    }

    @Test
    void sassCompiledMinifiedSourceMap() {
        ResponseEntity<String> testEntity = restTemplate.getForEntity("/test2.css.map", String.class);

        assertThat(testEntity.getStatusCode()).isEqualTo(HttpStatus.OK);

        String body = testEntity.getBody();
        assertThat(body).isNotEmpty();

        assertThat(body).startsWith("{");
        assertThat(body).contains("version");
    }

}