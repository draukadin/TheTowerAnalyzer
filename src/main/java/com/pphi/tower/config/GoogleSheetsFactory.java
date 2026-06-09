package com.pphi.tower.config;

import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

@Configuration
public class GoogleSheetsFactory {

    @Bean @Lazy
    public Sheets createSheets(DriveProperties props, OAuthStateService oAuthStateService) throws Exception {
        return new Sheets.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance(),
                oAuthStateService.getCredential())
                .setApplicationName(props.getApplicationName())
                .build();
    }
}
