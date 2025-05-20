package com.gnu.pbl2.utils;

import com.gnu.pbl2.exception.handler.InterviewHandler;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

@Component
public class UploadUtil {

    @Value("${sftp.username}")
    private String username;

    @Value("${sftp.host}")
    private String host;

    @Value("${sftp.port}")
    private int port;

    @Value("${sftp.password}")
    private String password;

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".mp4", ".mov");

    // 예시 base = lostitem-images / baseDirectory + id
    public String postDirectory(String base, Long id) {
        return "/home/bgt/pbl2/" + base + "/" + id + "/";
    }

    public String save(MultipartFile multipartFile, ChannelSftp channelSftp, String postDirectory) {
        try {

            // 파일 이름 체크
            String originalFilename = multipartFile.getOriginalFilename();
            if (originalFilename == null) {
                throw new InterviewHandler(ErrorStatus.FILE_UPLOAD_INVALID_NAME);
            }

            // 확장자 추출
            String extension = originalFilename.contains(".") ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase() : "";

            // 허용된 확장자인지 체크
            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new InterviewHandler(ErrorStatus.FILE_UPLOAD_EXTENSION_NOT_ALLOWED);
            }

            // MIME 타입 체크
            String contentType = multipartFile.getContentType();
            if (contentType == null ||
                    (!contentType.startsWith("image/") && !contentType.startsWith("video/"))) {
                throw new InterviewHandler(ErrorStatus.FILE_UPLOAD_MIME_NOT_ALLOWED);
            }

            String filePath = UUID.randomUUID().toString() + extension;
            String remoteFilePath = postDirectory + filePath;

            channelSftp.put(multipartFile.getInputStream(), remoteFilePath);

            return filePath;

        } catch (IOException e) {
            e.printStackTrace();
            throw new InterviewHandler(ErrorStatus.FILE_UPLOAD_IO_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InterviewHandler(ErrorStatus.INTERVIEW_SFTP_CONNECT_ERROR);
        }
    }

    // 디렉토리 삭제 메서드
    public void deleteDirectory(ChannelSftp channelSftp, String directoryPath) throws SftpException {
        Vector<ChannelSftp.LsEntry> list = channelSftp.ls(directoryPath);

        for (ChannelSftp.LsEntry entry : list) {
            String fileName = entry.getFilename();
            if (".".equals(fileName) || "..".equals(fileName)) {
                continue; // 현재 디렉터리와 상위 디렉터리는 스킵
            }
            String filePath = directoryPath + "/" + fileName;
            if (entry.getAttrs().isDir()) {
                // 하위 디렉터리 삭제 (재귀 호출)
                deleteDirectory(channelSftp, filePath);
            } else {
                // 파일 삭제
                channelSftp.rm(filePath);
            }
        }
        // 빈 디렉터리 삭제
        channelSftp.rmdir(directoryPath);
    }

    //디렉토리 확인
    public boolean isDirectoryExists(ChannelSftp channelSftp, String directoryPath) {
        try {
            channelSftp.cd(directoryPath);
            return true;
        } catch (SftpException e) {
            return false;
        }
    }

    //디렉토리 확인 후 삭제
    public void recreateDirectory(ChannelSftp channelSftp, String directoryPath) {
        try {
            // 디렉터리 존재 확인
            if (isDirectoryExists(channelSftp, directoryPath)) {
                // 디렉터리 통째로 삭제
                deleteDirectory(channelSftp, directoryPath);
            }
            // 새 디렉터리 생성
            channelSftp.mkdir(directoryPath);
        } catch (SftpException e) {
            e.printStackTrace();
            throw new InterviewHandler(ErrorStatus.INTERVIEW_SFTP_DELETE_ERROR);
        }
    }

    // sftp연결
    public ChannelSftp sessionConnect(String postDirectory) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
        try {
            System.out.println(username + host + port + password + postDirectory);
            // 서버 접속
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // SFTP 채널 열기
            channelSftp = (ChannelSftp) session.openChannel("sftp");

            try {
                channelSftp.connect();
                channelSftp.cd(postDirectory); // 게시글 디렉터리로 이동
            } catch (SftpException e) {
                // 게시글 디렉터리가 없으면 생성
                channelSftp.mkdir(postDirectory);
                channelSftp.cd(postDirectory);
            }

            return  channelSftp;
        } catch (Exception e) {
            e.printStackTrace();
            throw new InterviewHandler(ErrorStatus.INTERVIEW_SFTP_CONNECT_ERROR);
        }
    }

    public String filePath(String videoUrl) {
        return "http://" + host + ":4400" + "/mojadol/api/v1/video/" + videoUrl;
    }
}
