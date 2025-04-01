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
public class firebaseConfig {
    // 혹시나 알림 서비스 할까 싶어서 넣어놓음 안하면 지울게요

    @Value("${firebase_project_id}")
    private String firebase_project_id;

    @Value("${firebase_private_key_id}")
    private String firebase_private_key_id;

    @Value("${firebase_private_key}")
    private String firebase_private_key;

    @Value("${firebase_client_email}")
    private String firebase_client_email;

    @Value("${firebase_client_id}")
    private String firebase_client_id;

    @PostConstruct
    public void initialize() throws IOException {
        Map<String, Object> firebaseConfig = Map.of(
                "type", "service_account",
                "project_id", firebase_project_id,
                "private_key_id", firebase_private_key_id,
                "private_key", firebase_private_key.replace("\\n", "\n"),
                "client_email", firebase_client_email,
                "client_id", firebase_client_id,
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
