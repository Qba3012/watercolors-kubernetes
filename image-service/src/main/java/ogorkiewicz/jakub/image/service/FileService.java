package ogorkiewicz.jakub.image.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.jbosslog.JBossLog;
import net.coobird.thumbnailator.Thumbnails;

@Service
@JBossLog
public class FileService {

    @Value("${images.homePath}")
    private String path;

    @Value("${images.resizeWidth}")
    private int resizeWidth;

    public Path resizeFile(Path imagePath, Long paintingId) {
        String fileName = FilenameUtils.getBaseName(imagePath.toString());
        String extension = FilenameUtils.getExtension(imagePath.toString());
        Path filePath = Paths.get(path, "painting" + paintingId, fileName + "-small." + extension);

        try {
            Files.createDirectories(filePath.getParent());
            Thumbnails.of(imagePath.toFile())
                    .width(resizeWidth)
                    .outputFormat(extension.toUpperCase())
                    .toFile(filePath.toFile());
        } catch (IOException e) {
            log.error("Error while resizing image" + e.getMessage());
        }

        return filePath;
    }

    public Path saveFile(MultipartFile multipartFile, Long paintingId) {
        Path filePath = Paths.get(path, "painting" + paintingId, multipartFile.getOriginalFilename());

        try (BufferedInputStream bis = new BufferedInputStream(multipartFile.getInputStream())) {
            Files.createDirectories(filePath.getParent());
            Files.copy(bis, filePath, StandardCopyOption.REPLACE_EXISTING);
            return filePath;
        } catch (IOException e) {
            log.error("Unable to save file. " + e.getMessage());
        }
        return null;
    }

    public boolean deleteFile(Path filePath) {
        try{
            if (Files.isDirectory(filePath, LinkOption.NOFOLLOW_LINKS)) {
                try (DirectoryStream<Path> entries = Files.newDirectoryStream(filePath)) {
                    for (Path entry : entries) {
                        deleteFile(entry);
                    }
                }
            }
            Files.deleteIfExists(filePath);
        }catch (IOException e){
            log.error("Unable to delete file or directory. " + e.getMessage());
            return false;
        }
        return true;

    }

    public InputStream readFile(URI localUri){
        Path filePath = Paths.get(localUri);
        try{
            InputStream is = Files.newInputStream(filePath);
            return is;
        }catch (IOException e){
            log.error("Unable to read file. " + e.getMessage());
        }
        return null;
    }

    public boolean deleteAllPaintingImages(Long paintingId){
        Path filePath = Paths.get(path, "painting" + paintingId);
        return deleteFile(filePath);
    }
}
