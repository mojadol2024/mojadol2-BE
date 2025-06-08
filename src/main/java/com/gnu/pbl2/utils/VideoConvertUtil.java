package com.gnu.pbl2.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Component
public class VideoConvertUtil {

    public File convertMultipartWebmToMp4(MultipartFile multipartFile) throws IOException, InterruptedException {
        // 1. MultipartFile을 임시 webm 파일로 저장
        String originalFilename = multipartFile.getOriginalFilename();
        if (originalFilename == null || !originalFilename.endsWith(".webm")) {
            throw new IllegalArgumentException("webm 파일만 변환 가능합니다.");
        }

        File tempWebmFile = File.createTempFile("upload-", ".webm");
        multipartFile.transferTo(tempWebmFile);

        // 2. 임시 webm 파일을 mp4로 변환
        File mp4File = convertWebmToMp4(tempWebmFile);

        // 3. 변환 후 임시 webm 파일 삭제
        tempWebmFile.delete();

        return mp4File;
    }

    public File convertWebmToMp4(File webmFile) throws IOException, InterruptedException {
        String mp4FilePath = webmFile.getParent() + "/" +
                webmFile.getName().replace(".webm", ".mp4");
        File mp4File = new File(mp4FilePath);

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg",
                "-i", webmFile.getAbsolutePath(),
                "-c:v", "libx264",
                "-c:a", "aac",
                "-strict", "experimental",
                mp4File.getAbsolutePath()
        );

        pb.redirectErrorStream(true);
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new IOException("FFmpeg 변환 실패, exit code: " + exitCode);
        }

        return mp4File;
    }
}
