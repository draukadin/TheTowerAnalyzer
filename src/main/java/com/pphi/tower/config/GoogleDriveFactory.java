package com.pphi.tower.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Configuration
public class GoogleDriveFactory {

    @Bean @Lazy
    public Drive create(DriveProperties props) throws Exception {
        NetHttpTransport transport = new NetHttpTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        GoogleClientSecrets secrets = GoogleClientSecrets.load(
                jsonFactory, new FileReader(props.getOauthCredentialsFile()));

        Path tokensPath = Path.of(props.getTokensDir());
        Files.createDirectories(tokensPath);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                transport, jsonFactory, secrets, List.of(DriveScopes.DRIVE))
                .setDataStoreFactory(new FileDataStoreFactory(tokensPath.toFile()))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8888)
                .build();

        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return new Drive.Builder(transport, jsonFactory, credential)
                .setApplicationName(props.getApplicationName())
                .build();
    }
}
