package multipart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * 멀티파트 리졸버 빈이 없을 때,
 * 컨트롤러를 향해 자카르타 Part 타입을 어떻게 리졸빙하고 바인딩 하는 지 확인합니다.
 * @author : jbinchoo
 * @since : 2022-04-29
 */
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        properties = "spring.servlet.multipart.enabled=false")
class WithoutMultipartResolverIntegrationTest {

    @LocalServerPort
    int port;

    TestRestTemplate restTemplate = new TestRestTemplate();

    Resource fileForUpload;

    @BeforeEach
    void checkMultipartResolverBean() {
        fileForUpload = new ClassPathResource("myfile.txt");
    }

    @ParameterizedTest
    @ValueSource(strings = {"/multipart/args", "/part/args", "/part/bind"})
    void successfulResolvingAndBindingBehaviors(String url) throws IOException {
        var endpoint = String.format("http://127.0.0.1:%d%s", port, url);

        var result = doUpload(endpoint);

        System.out.println(result);
        assert result.getStatusCode().is2xxSuccessful();
        assert result.getBody().equals(readString(fileForUpload));
    }

    @ParameterizedTest
    @ValueSource(strings = {"/multipart/bind"})
    void unsuccessfulBindingBehavior(String url) {
        var endpoint = String.format("http://127.0.0.1:%d%s", port, url);

        var result = doUpload(endpoint);

        System.out.println(result);
        assert result.getStatusCode().is4xxClientError();
    }

    private ResponseEntity<String> doUpload(String url) {
        var request = new HttpEntity<>(getBody(), getHeader());
        return restTemplate.postForEntity(url, request, String.class);
    }

    private HttpHeaders getHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    private MultiValueMap<String, Object> getBody() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("upload", fileForUpload);
        System.out.println(body);
        return body;
    }

    private String readString(Resource resource) throws IOException {
        return Files.readString(Path.of(resource.getURI()));
    }
}