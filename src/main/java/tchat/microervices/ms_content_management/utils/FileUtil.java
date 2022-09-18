package tchat.microervices.ms_content_management.utils;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

@Component
public class FileUtil {

    public String namingFile(MultipartFile photoVideoFile) {
        String originalFileName =
                StringUtils.cleanPath(Objects.requireNonNull(photoVideoFile.getOriginalFilename()));
        int dotIndex = originalFileName.lastIndexOf('.');
        String fileExtension = originalFileName.substring(dotIndex);
        return UUID.randomUUID() + fileExtension;
    }

    public void saveNewPhotoOrVideo(
            String uploadDir,
            String newPhotoVideoName,
            MultipartFile photoVideoFile)
            throws IOException {

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        try (InputStream inputStream = photoVideoFile.getInputStream()) {
            Path filePath = uploadPath.resolve(newPhotoVideoName);

            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Could not save file");
        }
    }

    public void deletePhotoOrVideo(String uploadDir, String photoVideoName) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        Path filePath = uploadPath.resolve(photoVideoName);
        Files.deleteIfExists(filePath);
    }
}
