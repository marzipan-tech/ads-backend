package io.github.marzipan.ads.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import io.github.marzipan.ads.exception.FileStorageException;
import io.github.marzipan.ads.service.FileStorageService;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 * Сервис хранения файлов (изображений пользователей и объявлений).
 * Отвечает за:
 * - сохранение файлов на диск,
 * - получение файлов по пути.
 */
@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * Сохраняет изображение пользователя.
     */
    @Override
    public String saveUserImage(Integer id, MultipartFile image) {
        return save(image, "users/" + id);
    }

    private String save(MultipartFile image, String folder) {
        try {
            if (image == null || image.isEmpty()) {
                throw new FileStorageException("Image is empty");
            }

            String contentType = image.getContentType();
            if (!"image/jpeg".equals(contentType)
                    && !"image/png".equals(contentType)
                    && !"image/gif".equals(contentType)) {
                throw new FileStorageException("Unsupported image format");
            }

            try (InputStream inputStream = image.getInputStream()) {
                if (ImageIO.read(inputStream) == null) {
                    throw new FileStorageException("Invalid image file");
                }
            }

            Path root = Paths.get(uploadDir);
            Path directory = root.resolve(folder);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }
            String originalFileName = image.getOriginalFilename();
            if (originalFileName == null || originalFileName.isBlank()) {
                throw new FileStorageException("Invalid file name");
            }
            String fileName = Paths.get(originalFileName).getFileName().toString();
            Path filePath = directory.resolve(fileName).normalize();
            if (!filePath.startsWith(directory)) {
                throw new FileStorageException("Invalid file name");
            }
            try (InputStream inputStream = image.getInputStream()) {
                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
            }
            return folder + "/" + fileName;
        } catch (IOException exception) {
            throw new FileStorageException("Error saving file");
        }
    }

    /**
     * Сохраняет изображение объявления.
     */
    @Override
    public String saveAdImage(Integer id, MultipartFile image) {
        return save(image, "ads/" + id);
    }

    /**
     * Загружает файл изображения по пути.
     * @return массив байт изображения
     */
    @Override
    public byte[] getImage(String path) {
        try {
            String cleanPath = path.startsWith("/")
                    ? path.substring(1)
                    : path;
            Path root = Paths.get(uploadDir);
            Path filePath = root.resolve(cleanPath).normalize();
            if (!filePath.startsWith(root)) {
                throw new FileStorageException("Invalid file path");
            }
            return Files.readAllBytes(filePath);
        } catch (IOException exception) {
            throw new FileStorageException("Error reading file");
        }
    }

    @Override
    public void delete(String oldImagePath) {
        try {
            Path root = Paths.get(uploadDir);
            Path path = root.resolve(oldImagePath).normalize();
            if (!path.startsWith(root)) {
                throw new FileStorageException("Invalid file path");
            }
            Files.deleteIfExists(path);
        } catch (IOException exception) {
            throw new FileStorageException("Error deleting old file");
        }

    }
}
