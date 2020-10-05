package bsa.java.concurrency.fs;

import bsa.java.concurrency.image.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
public interface FileSystem {
    CompletableFuture<Image> saveFile(Path path, byte[] file);

    File transferToFile(MultipartFile multipartFile);

    Path transferToPath(MultipartFile multipartFile);

    void deleteFile(String path);

    void deleteAll();
}
