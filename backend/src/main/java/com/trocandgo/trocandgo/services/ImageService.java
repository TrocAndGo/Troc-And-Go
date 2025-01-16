
package com.trocandgo.trocandgo.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import com.trocandgo.trocandgo.util.EncryptionUtil;
import net.coobird.thumbnailator.Thumbnails;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.nio.file.Path;
import java.nio.file.Files;

import org.apache.tika.Tika;
import java.awt.image.BufferedImage;


@Service
public class ImageService {

    @Autowired
    private EncryptionUtil encryptionUtil;

    public String processAndEncryptImage(MultipartFile image, String username, Path uploadDirectory) throws Exception {
        // Vérification du type MIME réel avec Apache Tika
        Tika tika = new Tika();
        String mimeType = tika.detect(image.getInputStream());
        if (mimeType == null || (!mimeType.equals("image/jpeg") && !mimeType.equals("image/png"))) {
            throw new IllegalArgumentException("Invalid image format. Only .jpg, .jpeg, and .png are allowed.");
        }

        // Redimensionner et convertir l'image en JPEG avec Thumbnailator
        BufferedImage resizedImage = Thumbnails.of(image.getInputStream())
                .size(400, 400)
                .outputFormat("jpg")
                .asBufferedImage();

        // Convertir l'image redimensionnée en tableau d'octets
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", outputStream);
        byte[] imageBytes = outputStream.toByteArray();

        // Crypter et sauvegarder l'image
        String fileName = username + "_" + System.currentTimeMillis() + ".enc";
        Path filePath = uploadDirectory.resolve(fileName);
        byte[] encryptedImage = encryptionUtil.encrypt(imageBytes);
        Files.write(filePath, encryptedImage);

        return "/uploads/profile-pictures/" + fileName;
    }

    public byte[] decryptImage(Path filePath) throws Exception {
        // Lire et décrypter l'image
        byte[] encryptedImage = Files.readAllBytes(filePath);
        return encryptionUtil.decrypt(encryptedImage);
    }
}
