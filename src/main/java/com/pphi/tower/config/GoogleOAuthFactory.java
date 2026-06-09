package com.pphi.tower.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.sheets.v4.SheetsScopes;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Configuration
public class GoogleOAuthFactory {

    @Bean
    public GoogleAuthorizationCodeFlow googleAuthorizationCodeFlow(DriveProperties props) throws Exception {
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        GoogleClientSecrets secrets = GoogleClientSecrets.load(
                jsonFactory, new FileReader(props.getOauthCredentialsFile()));

        Path tokensPath = Path.of(props.getTokensDir());
        Files.createDirectories(tokensPath);

        return new GoogleAuthorizationCodeFlow.Builder(
                new NetHttpTransport(), jsonFactory, secrets,
                List.of(DriveScopes.DRIVE, SheetsScopes.SPREADSHEETS))
                .setDataStoreFactory(new FileDataStoreFactory(tokensPath.toFile()))
                .setAccessType("offline")
                .build();
    }
}
