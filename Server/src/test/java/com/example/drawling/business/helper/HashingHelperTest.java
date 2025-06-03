package com.example.drawling.business.helper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashingHelperTest {

    @InjectMocks
    private HashingHelper hashingHelper;

    @Mock
    private InputStream inputStream;

    @Test
    void testComputeFileHash() throws Exception {
        String data = "test";
        byte[] dataBytes = data.getBytes();
        InputStream mockInputStream = new ByteArrayInputStream(dataBytes);

        String expectedHash = "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08";
        String actualHash = hashingHelper.computeFileHash(mockInputStream);

        assertEquals(expectedHash, actualHash);
    }

    @Test
    void testComputeFileHash_IOException() {
        try {
            when(inputStream.read(any(byte[].class))).thenThrow(new IOException("Read error"));
            hashingHelper.computeFileHash(inputStream);
        } catch (IOException e) {
            assertEquals("Read error", e.getMessage());
        }
    }

    @Test
    void testComputeFileHash_NoSuchAlgorithmException() {
        try (MockedStatic<MessageDigest> mockedDigest = mockStatic(MessageDigest.class)) {
            lenient().when(inputStream.read(any(byte[].class))).thenReturn(-1);
            mockedDigest.when(() -> MessageDigest.getInstance("SHA-256")).thenThrow(new NoSuchAlgorithmException());

            hashingHelper.computeFileHash(inputStream);
        } catch (IOException e) {
            assertEquals("Could not compute file hash", e.getMessage());
        }
    }

    @Test
    void testComputeImageHash() throws IOException {
        // Arrange
        BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillRect(0, 0, 100, 100);
        g2d.dispose();

        // Act
        String hash = hashingHelper.computeImageHash(bufferedImage);

        // Assert
        Assertions.assertNotNull(hash, "Hash should not be null");
        Assertions.assertFalse(hash.isEmpty(), "Hash should not be empty");
        Assertions.assertTrue(hash.matches("[a-fA-F0-9]{64}"), "Hash should be a valid SHA-256 hash");
    }



    @Test
    void testBytesToHex() {
        // Arrange
        byte[] hashBytes = {0x00, 0x1A, 0x2B, 0x3C, 0x4D, 0x5E, 0x6F, (byte) 0xFF};

        // Act
        String hexString = hashingHelper.bytesToHex(hashBytes);

        // Assert
        Assertions.assertEquals("001a2b3c4d5e6fff", hexString, "Hex string should match expected value");
    }



}