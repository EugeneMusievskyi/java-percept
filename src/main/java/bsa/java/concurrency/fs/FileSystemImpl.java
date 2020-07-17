package bsa.java.concurrency.fs;

import bsa.java.concurrency.image.Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
public class FileSystemImpl implements FileSystem {
    private File directory;

    private String serverPort;

    private String filesUrlTemplate;

    @Autowired
    public FileSystemImpl(FileSystemConfigurationProperties properties) {
        directory = new File("images/");
        if (!directory.exists()) {
            directory.mkdir();
        }

        filesUrlTemplate = String.format("http://127.0.0.1:%s/files/", properties.port);
    }

    @Override
    public CompletableFuture<Image> saveFile(Path path, byte[] file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Files.write(path, file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            String fileName = path.getFileName().toString();
            String id = fileName.substring(0, fileName.indexOf(getFileExtension(fileName)));
            String fileUrl = filesUrlTemplate + fileName;

            return new Image(UUID.fromString(id), path.toString(), fileUrl, null);
        });
    }

    @Override
    public File transferToFile(MultipartFile multipartFile) {
        File file = new File(directory.getPath() + "/" + multipartFile.getOriginalFilename());
        try {
            multipartFile.transferTo(file);
            return file;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Path transferToPath(MultipartFile multipartFile) {
        UUID id = UUID.randomUUID();
        String extension = getFileExtension(multipartFile.getOriginalFilename());
        Path path = Paths.get(directory.getPath() + "/" + id + extension);
        try {
            multipartFile.transferTo(path);
            return path;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteFile(String filePath) {
        var path = Paths.get(filePath);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFileExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

        if (i > p) {
            extension = fileName.substring(i);
        }

        return extension;
    }
}
