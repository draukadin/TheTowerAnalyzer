package com.pphi.tower.repository;

import com.google.api.client.http.FileContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Repository
public class GoogleDriveRepository {

    private final Drive drive;

    public GoogleDriveRepository(Drive drive) {
        this.drive = drive;
    }

    public List<File> listFilesInFolder(String folderId) throws IOException {
        List<File> result = new ArrayList<>();
        String pageToken = null;
        do {
            FileList response = drive.files().list()
                    .setQ("'" + folderId + "' in parents and mimeType = 'text/plain' and trashed = false")
                    .setFields("nextPageToken, files(id, name, parents)")
                    .setPageToken(pageToken)
                    .execute();
            List<File> page = response.getFiles();
            if (page != null) {
                result.addAll(page);
            }
            pageToken = response.getNextPageToken();
        } while (pageToken != null);
        return result;
    }

    public InputStream downloadFile(String fileId) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            return downloadFile(fileId, out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream downloadFile(String fileId, OutputStream out) throws IOException {
        drive.files().get(fileId).executeMediaAndDownloadTo(out);
        return convertToInputStream(out);
    }

    public void moveFilesToFolder(List<File> files, String destinationFolderId) throws IOException {
        for (File file : files) {
            String currentParents = String.join(",", file.getParents());
            drive.files().update(file.getId(), new File())
                    .setAddParents(destinationFolderId)
                    .setRemoveParents(currentParents)
                    .setFields("id, parents")
                    .execute();
        }
    }

    public File uploadFile(java.io.File localFile, String fileName, String folderId) throws IOException {
        File metadata = new File();
        metadata.setName(fileName);
        metadata.setParents(List.of(folderId));
        FileContent content = new FileContent("application/octet-stream", localFile);
        return drive.files().create(metadata, content)
                .setFields("id, name")
                .setSupportsAllDrives(true)
                .execute();
    }

    private InputStream convertToInputStream(OutputStream os) throws IOException {
        if (os instanceof ByteArrayOutputStream out) {
            try (out) {
                return new ByteArrayInputStream(out.toByteArray());
            }
        }
        throw new IOException("OutputStream must be of type ByteArrayInputStream to convert to InputStream");
    }
}
