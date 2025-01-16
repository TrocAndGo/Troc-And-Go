
package com.trocandgo.trocandgo.services;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import com.trocandgo.trocandgo.model.Users;
import com.trocandgo.trocandgo.repository.UserRepository;
import com.trocandgo.trocandgo.util.EncryptionUtil;
import net.coobird.thumbnailator.Thumbnails;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

import org.apache.tika.Tika;
import java.awt.image.BufferedImage;


@Service
public class ImageService {

    @Autowired
    private EncryptionUtil encryptionUtil;

    private final UserRepository userRepository;
    private final Path uploadDirectory = Paths.get("src/main/resources/static/uploads/profile-pictures");

    public ImageService(UserRepository userRepository, EncryptionUtil encryptionUtil) {
        this.userRepository = userRepository;
        this.encryptionUtil = encryptionUtil;
    }

    public String uploadProfilePicture(MultipartFile image, String username) throws Exception {
        // Valider le type MIME de l'image
        validateImageMimeType(image);

        // Récupérer l'utilisateur
        Users user = getUserByUsername(username);

        // Supprimer l'ancienne image si elle existe
        deleteExistingImageIfExists(user);

        // Redimensionner, convertir et chiffrer l'image
        byte[] encryptedImage = processAndEncryptImage(image);

        // Générer le chemin du fichier et sauvegarder l'image
        String fileName = generateEncryptedFileName(username);
        Path filePath = uploadDirectory.resolve(fileName);
        Files.write(filePath, encryptedImage);

        // Mettre à jour l'utilisateur et retourner l'URL de l'image
        String imageUrl = "/uploads/profile-pictures/" + fileName;
        updateUserImage(user, imageUrl);

        return imageUrl;
    }

    public byte[] downloadProfilePicture(String username) throws Exception {
        // Récupérer l'utilisateur
        Users user = getUserByUsername(username);

        // Vérifier et obtenir le chemin du fichier
        Path filePath = getFilePath(user);

        // Lire et décrypter l'image
        byte[] encryptedImage = Files.readAllBytes(filePath);
        return encryptionUtil.decrypt(encryptedImage);
    }

    // === Méthodes Privées Utilitaires ===

    private void validateImageMimeType(MultipartFile image) throws Exception {
        Tika tika = new Tika();
        String mimeType = tika.detect(image.getInputStream());
        if (mimeType == null || (!mimeType.equals("image/jpeg") && !mimeType.equals("image/png"))) {
            throw new IllegalArgumentException("Invalid image format. Only .jpg, .jpeg, and .png are allowed.");
        }
    }

    private Users getUserByUsername(String username) {
        return userRepository.findByName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void deleteExistingImageIfExists(Users user) throws Exception {
        if (user.getPicture() != null && !user.getPicture().isEmpty()) {
            String oldFilePath = user.getPicture().replace("/uploads/profile-pictures/", "");
            Path oldFile = uploadDirectory.resolve(oldFilePath);
            if (Files.exists(oldFile)) {
                Files.delete(oldFile);
            }
        }
    }

    private byte[] processAndEncryptImage(MultipartFile image) throws Exception {
        BufferedImage resizedImage = Thumbnails.of(image.getInputStream())
                .size(400, 400)
                .outputFormat("jpg")
                .asBufferedImage();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", outputStream);
        return encryptionUtil.encrypt(outputStream.toByteArray());
    }

    private String generateEncryptedFileName(String username) {
        return username + "_" + System.currentTimeMillis() + ".enc";
    }

    private void updateUserImage(Users user, String imageUrl) {
        user.setPicture(imageUrl);
        userRepository.save(user);
    }

    private Path getFilePath(Users user) {
        if (user.getPicture() == null || user.getPicture().isEmpty()) {
            throw new IllegalArgumentException("No profile picture found.");
        }
        String fileName = user.getPicture().replace("/uploads/profile-pictures/", "");
        Path filePath = uploadDirectory.resolve(fileName);
        if (!Files.exists(filePath)) {
            throw new IllegalArgumentException("Profile picture not found.");
        }
        return filePath;
    }
}
