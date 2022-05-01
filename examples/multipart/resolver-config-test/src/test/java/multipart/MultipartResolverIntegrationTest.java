package multipart;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * 멀티파트 리졸버 빈이 있을 때,
 * 컨트롤러를 향해 자카르타 Part 타입을 어떻게 리졸빙하고 바인딩 하는 지 확인합니다.
 * @author : jbinchoo
 * @since : 2022-04-29
 */
@SpringBootTest(
        webEnvironment = RANDOM_PORT,
        properties = "spring.servlet.multipart.enabled=true")
class MultipartResolverIntegrationTest extends FileUploadTestSupport {

    @LocalServerPort
    int port;

    @ParameterizedTest
    @ValueSource(strings = {"/multipart/args", "/multipart/bind", "/part/args"})
    void successfulResolvingAndBindingBehaviors(String url) throws IOException {
        var endpoint = String.format("http://127.0.0.1:%d%s", port, url);

        var result = doUpload(endpoint);

        assert result.getStatusCode().is2xxSuccessful();
        assert result.getBody().equals(getFileContent());
    }

    @ParameterizedTest
    @ValueSource(strings = {"/part/bind"})
    void unsuccessfulBindingBehavior(String url) {
        var endpoint = String.format("http://127.0.0.1:%d%s", port, url);

        var result = doUpload(endpoint);

        System.out.println(result);
        assert result.getStatusCode().is4xxClientError();
    }
}