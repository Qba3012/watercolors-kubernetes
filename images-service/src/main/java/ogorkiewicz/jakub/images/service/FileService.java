package ogorkiewicz.jakub.images.service;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.jbosslog.JBossLog;

@Service
@JBossLog
public class FileService {

    @Value("${images.homePath}")
    private String path;

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
            System.out.println(filePath.toString());
            InputStream is = Files.newInputStream(filePath);
            System.out.println(is.available());
            return is;
        }catch (IOException e){
            log.error("Unable to read file. " + e.getMessage());
        }
        return null;
    }

    public boolean deleteAllDeviceFiles(Long deviceId){
        Path filePath = Paths.get(path + "/device" + deviceId);
        return deleteFile(filePath);
    }
}
