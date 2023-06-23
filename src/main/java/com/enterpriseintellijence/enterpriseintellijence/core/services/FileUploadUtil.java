package com.enterpriseintellijence.enterpriseintellijence.core.services;

import org.springframework.web.multipart.MultipartFile;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

public class FileUploadUtil {
    public static void saveFile(String uploadDir, String fileName, MultipartFile multipartFile) throws IOException {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        byte[] compressedImage = compressImage(multipartFile.getBytes(), 3 * 1024 * 1024); // 3 MB
        if (compressedImage.length == 0) {
            throw new IOException("Compressed image data is empty.");
        }

        ByteArrayInputStream inputStream = new ByteArrayInputStream(compressedImage);
        BufferedImage bufferedImage = ImageIO.read(inputStream);

        //ridimensiona l'immagine
        int targetSize = Math.min(bufferedImage.getWidth(), bufferedImage.getHeight());
        Image scaledImage = bufferedImage.getScaledInstance(targetSize, targetSize, Image.SCALE_SMOOTH);

        BufferedImage finalImage = new BufferedImage(targetSize, targetSize, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = finalImage.createGraphics();
        graphics.drawImage(scaledImage, 0, 0, null);
        graphics.dispose();

        Path filePath = uploadPath.resolve(fileName);
        ImageIO.write(finalImage, "jpg", filePath.toFile());
    }

    private static byte[] compressImage(byte[] imageData, long maxSize) throws IOException {
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        float quality = 1.0f;
        ImageIO.write(image, "jpg", outputStream);

        while (outputStream.size() > maxSize && quality >= 0.1f) {
            outputStream.reset();
            ImageIO.write(image, "jpg", outputStream);
            quality -= 0.1f;
            image = resizeImage(image, quality);
        }

        return outputStream.toByteArray();
    }

    private static BufferedImage resizeImage(BufferedImage image, float quality) {
        int newWidth = (int) (image.getWidth() * quality);
        int newHeight = (int) (image.getHeight() * quality);

        BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, image.getType());
        Graphics2D graphics = resizedImage.createGraphics();
        graphics.drawImage(image, 0, 0, newWidth, newHeight, null);
        graphics.dispose();

        return resizedImage;
    }
}
