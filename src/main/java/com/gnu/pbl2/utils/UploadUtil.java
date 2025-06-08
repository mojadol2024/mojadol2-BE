package com.gnu.pbl2.utils;

import com.gnu.pbl2.exception.handler.InterviewHandler;
import com.gnu.pbl2.response.code.status.ErrorStatus;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.Session;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
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

    private static final List<String> ALLOWED_EXTENSIONS = List.of(".jpg", ".jpeg", ".png", ".mp4", ".mov", ".webm");

    public String postDirectory(String base, Long id) {
        return "/home/bgt/pbl2/" + base + "/" + id + "/";
    }

    public String save(File file, ChannelSftp channelSftp, String postDirectory) {
        try {
            String originalFilename = file.getName();
            if (originalFilename == null) {
                throw new InterviewHandler(ErrorStatus.FILE_UPLOAD_INVALID_NAME);
            }

            String extension = originalFilename.contains(".") ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase() : "";

            if (!ALLOWED_EXTENSIONS.contains(extension)) {
                throw new InterviewHandler(ErrorStatus.FILE_UPLOAD_EXTENSION_NOT_ALLOWED);
            }

            String filePath = UUID.randomUUID().toString() + extension;
            String remoteFilePath = postDirectory + filePath;

            try (FileInputStream fis = new FileInputStream(file)) {
                channelSftp.put(fis, remoteFilePath);
            }

            return filePath;

        } catch (IOException e) {
            e.printStackTrace();
            throw new InterviewHandler(ErrorStatus.FILE_UPLOAD_IO_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            throw new InterviewHandler(ErrorStatus.INTERVIEW_SFTP_CONNECT_ERROR);
        }
    }

    public void deleteDirectory(ChannelSftp channelSftp, String directoryPath) throws SftpException {
        Vector<ChannelSftp.LsEntry> list = channelSftp.ls(directoryPath);

        for (ChannelSftp.LsEntry entry : list) {
            String fileName = entry.getFilename();
            if (".".equals(fileName) || "..".equals(fileName)) {
                continue;
            }
            String filePath = directoryPath + "/" + fileName;
            if (entry.getAttrs().isDir()) {
                deleteDirectory(channelSftp, filePath);
            } else {
                channelSftp.rm(filePath);
            }
        }
        channelSftp.rmdir(directoryPath);
    }

    public boolean isDirectoryExists(ChannelSftp channelSftp, String directoryPath) {
        try {
            channelSftp.cd(directoryPath);
            return true;
        } catch (SftpException e) {
            return false;
        }
    }

    public void recreateDirectory(ChannelSftp channelSftp, String directoryPath) {
        try {
            if (isDirectoryExists(channelSftp, directoryPath)) {
                deleteDirectory(channelSftp, directoryPath);
            }
            channelSftp.mkdir(directoryPath);
        } catch (SftpException e) {
            e.printStackTrace();
            throw new InterviewHandler(ErrorStatus.INTERVIEW_SFTP_DELETE_ERROR);
        }
    }

    @Getter
    public static class SftpConnection {
        private final Session session;
        private final ChannelSftp channelSftp;

        public SftpConnection(Session session, ChannelSftp channelSftp) {
            this.session = session;
            this.channelSftp = channelSftp;
        }

        public void disconnect() {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
        }
    }

    // 수정된 sessionConnect 메서드
    public SftpConnection sessionConnect(String postDirectory) {
        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channelSftp = null;
        try {
            session = jsch.getSession(username, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();

            try {
                channelSftp.cd(postDirectory);
            } catch (SftpException e) {
                channelSftp.mkdir(postDirectory);
                channelSftp.cd(postDirectory);
            }

            return new SftpConnection(session, channelSftp);

        } catch (Exception e) {
            if (channelSftp != null && channelSftp.isConnected()) {
                channelSftp.disconnect();
            }
            if (session != null && session.isConnected()) {
                session.disconnect();
            }
            e.printStackTrace();
            throw new InterviewHandler(ErrorStatus.INTERVIEW_SFTP_CONNECT_ERROR);
        }
    }

    public String filePath(String videoUrl) {
        return "http://" + host + ":4400" + "/mojadol/api/v1/video/" + videoUrl;
    }
}
