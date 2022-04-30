package multipart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;

import javax.servlet.MultipartConfigElement;
import java.io.IOException;

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
class WithoutMultipartResolverIntegrationTest extends FileUploadTestSupport {

    @LocalServerPort
    int port;

    @BeforeEach
    public void loadTestFile() {
        super.loadTestFile();
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

    /**
     * 자카르타의 멀티파트 콘피그를 빈 등록합니다.
     */
    @TestConfiguration
    public static class ServletMultipartConfig {

        @Bean
        MultipartConfigElement multipartConfigElement() {
            return new MultipartConfigElement(null, 50000, 50000, 50000);
        }
    }
}