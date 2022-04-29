package multipart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.servlet.MultipartConfigElement;

/**
 * @author : jbinchoo
 * @since : 2022-04-29
 */
@SpringBootApplication
public class MultipartPracticeMain {

    public static void main(String[] args) {
        SpringApplication.run(MultipartPracticeMain.class);
    }


    @Bean
    MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement(null, 50000, 50000, 50000);
    }
}
