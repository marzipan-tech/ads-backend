package io.github.marzipan.ads.service;

import io.github.marzipan.ads.exception.FileStorageException;
import io.github.marzipan.ads.service.impl.FileStorageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileStorageServiceTest {
    @Mock
    private MultipartFile image;

    @InjectMocks
    private FileStorageServiceImpl fileStorageService;

    @TempDir
    Path tempDir;

    private byte[] data;

    @BeforeEach
    void setUp() throws IOException {
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", outputStream);
        data = outputStream.toByteArray();
    }

    @Test
    void saveUserImage_shouldReturnUserImagePathAndSaveImageFile() throws Exception {
        when(image.getOriginalFilename()).thenReturn("avatar.jpg");
        when(image.getContentType()).thenReturn("image/jpeg");
        when(image.getInputStream()).thenAnswer(invocation -> new ByteArrayInputStream(data));

        String result = fileStorageService.saveUserImage(1, image);

        assertEquals("users/1/avatar.jpg", result);
        Path file = tempDir.resolve("users/1/avatar.jpg");
        assertTrue(Files.exists(file));
        assertArrayEquals(data, Files.readAllBytes(file));
    }

    @Test
    void saveUserImage_shouldThrowFileStorageException_whenIOException() throws Exception {
        when(image.getContentType()).thenReturn("image/jpeg");
        when(image.getInputStream()).thenThrow(new IOException());

        FileStorageException exception = assertThrows(FileStorageException.class, () -> fileStorageService.saveUserImage(1, image));
        assertEquals("Error saving file", exception.getMessage());
    }

    @Test
    void saveUserImage_shouldThrowFileStorageException_whenImageIsEmpty() {
        when(image.isEmpty()).thenReturn(true);

        FileStorageException exception = assertThrows(FileStorageException.class, () -> fileStorageService.saveUserImage(1, image));
        assertEquals("Image is empty", exception.getMessage());
    }

    @Test
    void saveUserImage_shouldThrowFileStorageException_whenFileFormatUnsupported() {
        when(image.isEmpty()).thenReturn(false);
        when(image.getContentType()).thenReturn("text/plain");

        FileStorageException exception = assertThrows(FileStorageException.class, () -> fileStorageService.saveUserImage(1, image));
        assertEquals("Unsupported image format", exception.getMessage());
    }

    @Test
    void saveUserImage_shouldThrowFileStorageException_whenFileIsNotImage() throws IOException {
        byte[] invalidImage = "not an image".getBytes();
        when(image.isEmpty()).thenReturn(false);
        when(image.getContentType()).thenReturn("image/jpeg");
        when(image.getInputStream()).thenReturn(new ByteArrayInputStream(invalidImage));

        FileStorageException exception = assertThrows(FileStorageException.class, () -> fileStorageService.saveUserImage(1, image));
        assertEquals("Invalid image file", exception.getMessage());
    }

    @Test
    void getImage_shouldReturnImage() throws Exception {
        Path folder = tempDir.resolve("users/1");
        Files.createDirectories(folder);
        Files.write(folder.resolve("avatar.jpg"), data);

        byte[] result = fileStorageService.getImage("users/1/avatar.jpg");

        assertArrayEquals(data, result);
    }

    @Test
    void getImage_shouldThrowFileStorageException_whenErrorReadingFile() {
        FileStorageException exception = assertThrows(FileStorageException.class, () -> fileStorageService.getImage("unknown.jpg"));
        assertEquals("Error reading file", exception.getMessage());
    }

    @Test
    void getImage_shouldRejectPathTraversal() {
        FileStorageException exception = assertThrows(FileStorageException.class, () -> fileStorageService.getImage("../secret.jpg"));
        assertEquals("Invalid file path", exception.getMessage());
    }

    @Test
    void delete_shouldRejectPathTraversal() {
        FileStorageException exception = assertThrows(FileStorageException.class, () -> fileStorageService.delete("../secret.jpg"));
    }
}
