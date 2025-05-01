package com.gnu.pbl2.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

@Configuration
public class FirebaseConfig {
    // 혹시나 알림 서비스 할까 싶어서 넣어놓음 안하면 지울게요

    @Value("${firebase.project-id}")
    private String firebaseProjectId;

    @Value("${firebase.private-key-id}")
    private String firebasePrivateKeyId;

    @Value("${firebase.private-key}")
    private String firebasePrivateKey;

    @Value("${firebase.client-email}")
    private String firebaseClientEmail;

    @Value("${firebase.client-id}")
    private String firebaseClientId;

    @PostConstruct
    public void initialize() throws IOException {
        Map<String, Object> firebaseConfig = Map.of(
                "type", "service_account",
                "project_id", firebaseProjectId,
                "private_key_id", firebasePrivateKeyId,
                "private_key", firebasePrivateKey.replace("\\n", "\n"),
                "client_email", firebaseClientEmail,
                "client_id", firebaseClientId,
                "auth_uri", "https://accounts.google.com/o/oauth2/auth",
                "token_uri", "https://oauth2.googleapis.com/token",
                "auth_provider_x509_cert_url", "https://www.googleapis.com/oauth2/v1/certs",
                "client_x509_cert_url", "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-zzsr8%40mojadol.iam.gserviceaccount.com"
        );

        // JSON을 바이트 배열로 변환하여 GoogleCredentials로 로드
        ByteArrayInputStream serviceAccount = new ByteArrayInputStream(
                new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsBytes(firebaseConfig)
        );

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);

    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        return FirebaseMessaging.getInstance();
    }
}
