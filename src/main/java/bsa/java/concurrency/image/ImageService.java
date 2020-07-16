package bsa.java.concurrency.image;

import bsa.java.concurrency.fs.FileSystem;
import bsa.java.concurrency.image.service.DHasher;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.concurrent.*;

@Slf4j
@Service
public class ImageService {
    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private FileSystem fileSystem;

    private ExecutorService executorService;

    private static final Object lock = new Object();

    public ImageService() {
//        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        executorService = Executors.newCachedThreadPool();
    }

    public void save(Path path) {
        executorService.submit(new FileSaver(path));
    }

    public void save(MultipartFile[] files) {
        for (MultipartFile file : files) {
            save(fileSystem.transferToPath(file));
        }
    }

    private class FileSaver implements Runnable {
        private Path path;

        public FileSaver(Path path) {
            this.path = path;
        }

        @Override
        public void run() {
            byte[] fileBytes = null;
            try {
                fileBytes = Files.readAllBytes(path);
            } catch (IOException e) {
                e.printStackTrace();
            }

            var futureFilePath = fileSystem.saveFile(path, fileBytes);
            var futureHash = executorService.submit(new HashCounter(fileBytes));

            try {
                String filePath = futureFilePath.get();
                Long hash = futureHash.get();
                Image image = new Image(filePath, hash);
                imageRepository.save(image);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private static class HashCounter implements Callable<Long> {
        byte[] fileBytes;

        public HashCounter(byte[] fileBytes) {
            this.fileBytes = fileBytes;
        }

        @Override
        public Long call() {
            if (fileBytes == null)
                throw new NullPointerException();

            return DHasher.calculateHash(fileBytes);
        }
    }

    public void deleteImage(UUID id) {
        var image = imageRepository.findById(id);
        if (image.isPresent()) {
            fileSystem.deleteFile(image.get().getPath());
            imageRepository.deleteById(id);
        }
    }

    public void purgeImages() {
        var images = imageRepository.findAll();
        for (Image image : images) {
            fileSystem.deleteFile(image.getPath());
        }
        imageRepository.deleteAll();
    }
}
