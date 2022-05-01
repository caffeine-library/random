package multipart;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author : jbinchoo
 * @since : 2022-04-30
 */
public class FileUploadTestSupport {

    private final TestRestTemplate restTemplate;
    private final Resource testFile;

    protected FileUploadTestSupport() {
        restTemplate = new TestRestTemplate();
        testFile = loadTestFile();
    }

    protected Resource loadTestFile() {
        return new ClassPathResource("myfile.txt");
    }

    protected ResponseEntity<String> doUpload(String url) {
        var request = new HttpEntity<>(getBody(), getHeader());
        return restTemplate.postForEntity(url, request, String.class);
    }

    protected HttpHeaders getHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        return headers;
    }

    protected MultiValueMap<String, Object> getBody() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Resource fileForUpload = loadTestFile();
        body.add("upload", fileForUpload);
        body.add("uploadList", fileForUpload);
        body.add("uploadList", fileForUpload);
        body.add("uploadList", fileForUpload);

        body.add("username", "binchoo");
        body.add("usernameList", "binchoo");
        body.add("usernameList", "binchoo");
        body.add("usernameList", "binchoo");

        System.out.println("RequestBody: " + body);
        return body;
    }

    protected String getFileContent() {
        return this.readAsString(testFile);
    }

    protected String readAsString(Resource resource) {
        try {
            return Files.readString(Path.of(resource.getURI()));
        } catch (IOException e) {
            PrintWriter printWriter = new PrintWriter(System.out);
            e.printStackTrace(printWriter);
            throw new IllegalArgumentException(e);
        }
    }
}
