package com.gnu.pbl2.video.controller;

import com.gnu.pbl2.exception.handler.InterviewHandler;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/mojadol/api/v1/video")
public class VideoController {

    private final Path baseDir = Paths.get("/home/bgt/pbl2/");

    @GetMapping("/{foldername}/{folderId}/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable("filename") String filename,
                                              @PathVariable("folderId") String folderId,
                                              @PathVariable("foldername") String foldername) {
        try {
            // 기준 디렉토리 설정
            Path filePath = baseDir.resolve(foldername).resolve(folderId).resolve(filename).normalize();
            // 디렉토리 탈출 공격 방지
            if (!filePath.startsWith(baseDir)) {
                throw new RuntimeException("Image Runtime Exception");
            }
            // 파일 자원 로드
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                // MIME 타입 추론
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                throw new RuntimeException("파일 없음");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new InterviewHandler(ErrorStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
