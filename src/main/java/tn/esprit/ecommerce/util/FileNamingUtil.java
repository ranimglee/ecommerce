package tn.esprit.ecommerce.util;

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
public class FileNamingUtil {

    public String nameFile(MultipartFile multipartFile) {
        // TODO: Validate the MultipartFile to ensure it is not null or empty.
        String originalFileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        String fileExtension = getFileExtension(originalFileName);
        return UUID.randomUUID().toString() + fileExtension;
    }

    private String getFileExtension(String filename) {
        // TODO: Validate the filename to ensure it has a proper extension.
        return filename.substring(filename.lastIndexOf('.'));
    }

    public void saveNewFile(String uploadDir, String filename, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path filePath = uploadPath.resolve(filename);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            // TODO: Replace System.out.println with proper logging.
            System.out.println("File copied to: " + filePath.toString());
        } catch (IOException e) {
            throw new IOException("Could not save file", e);
        }
    }

    public void updateFile(String uploadDir, String oldFileName, String newFileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        try (InputStream inputStream = multipartFile.getInputStream()) {
            Path oldFilePath = uploadPath.resolve(oldFileName);
            Path newFilePath = uploadPath.resolve(newFileName);
            Files.deleteIfExists(oldFilePath);
            Files.copy(inputStream, newFilePath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("Could not update file");
        }
    }

    public void deleteFile(String uploadDir, String fileName) throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        Path filePath = uploadPath.resolve(fileName);
        Files.deleteIfExists(filePath);
    }


}
