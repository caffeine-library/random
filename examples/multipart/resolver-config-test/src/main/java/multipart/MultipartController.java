package multipart;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Part;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author : jbinchoo
 * @since : 2022-04-29
 */
@Slf4j
@RestController
public class MultipartController {

    @PostMapping("/part/args")
    public ResponseEntity<?> receivePartArgs(
            @RequestPart(value="upload") Part part) throws IOException {

        if (part != null) {
            String name = part.getName();
            String content = StreamUtils.copyToString(part.getInputStream(), StandardCharsets.UTF_8);
            log.info("Part={}", name);
            log.info("Content={}", content);
            return ResponseEntity.ok(content);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/multipart/args")
    public ResponseEntity<?> receiveMultiPartArgs(
            @RequestParam(value="upload") MultipartFile file) throws IOException {

        if (file != null) {
            String name = file.getName();
            String content = StreamUtils.copyToString(file.getInputStream(), StandardCharsets.UTF_8);
            log.info("MultipartFile={}", file.getName());
            log.info("Content={}", StreamUtils.copyToString(file.getInputStream(), StandardCharsets.UTF_8));
            return ResponseEntity.ok(content);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/part/bind")
    public ResponseEntity<?> receivePartVo(
            PartVo partVO, BindingResult bindingResult) throws IOException {

        if (!bindingResult.hasErrors())
            return this.receivePartArgs(partVO.getUpload());

        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/multipart/bind")
    public ResponseEntity<?> receiveMultiPartVo(
            MultipartVo fileVO, BindingResult bindingResult) throws IOException {

        if (!bindingResult.hasErrors())
            return this.receiveMultiPartArgs(fileVO.getUpload());

        return ResponseEntity.badRequest().build();
    }
}

@Setter
@Getter
@ToString
class PartVo {

    private Part upload;
}

@Setter
@Getter
@ToString
class MultipartVo {

    private MultipartFile upload;
}