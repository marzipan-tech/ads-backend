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

import java.io.ByteArrayInputStream;
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
    void setUp() {
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());
        data = "image".getBytes();
    }

    @Test
    void saveUserImage_shouldReturnUserImagePathAndSaveImageFile() throws Exception {
        when(image.getOriginalFilename()).thenReturn("avatar.jpg");
        when(image.getInputStream()).thenReturn(new ByteArrayInputStream(data));

        String result = fileStorageService.saveUserImage(1, image);

        assertEquals("users/1/avatar.jpg", result);
        Path file = tempDir.resolve("users/1/avatar.jpg");
        assertTrue(Files.exists(file));
        assertArrayEquals(data, Files.readAllBytes(file));
    }

    @Test
    void saveUserImage_shouldThrowFileStorageException_whenIOException() throws Exception {
        when(image.getOriginalFilename()).thenReturn("avatar.jpg");
        when(image.getInputStream()).thenThrow(new IOException());

        FileStorageException exception = assertThrows(FileStorageException.class, () -> fileStorageService.saveUserImage(1, image));
        assertEquals("Error saving file", exception.getMessage());
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
}
