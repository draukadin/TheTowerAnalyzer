package com.pphi.tower.config;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.io.FileInputStream;
import java.util.List;

@Configuration
public class GoogleDriveFactory {

    @Bean @Lazy
    public Drive create(DriveProperties props) throws Exception {
        NetHttpTransport transport = new NetHttpTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(props.getCredentialsFile()))
                .createScoped(List.of(DriveScopes.DRIVE_READONLY));

        return new Drive.Builder(transport, jsonFactory, new HttpCredentialsAdapter(credentials))
                .setApplicationName(props.getApplicationName())
                .build();
    }
}
