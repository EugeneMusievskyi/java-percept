package bsa.java.concurrency.fs;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
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

    public FileSystemImpl() {
        directory = new File("images/");
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    @Override
    public CompletableFuture<String> saveFile(Path path, byte[] file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                Files.write(path, file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return path.toString();
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
        Path path = Paths.get(directory.getPath() + "/" + multipartFile.getOriginalFilename());
        try {
            multipartFile.transferTo(path);
            return path;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
