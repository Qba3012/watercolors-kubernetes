package ogorkiewicz.jakub.mail.service;

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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.extern.jbosslog.JBossLog;
import ogorkiewicz.jakub.mail.config.NewsletterConfig;

@Service
@JBossLog
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class FileService {

    private NewsletterConfig newsletterConfig;

    public Path saveFile(MultipartFile multipartFile) {
        Path filePath = Paths.get(newsletterConfig.getHome(), "templates", multipartFile.getOriginalFilename());

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

}