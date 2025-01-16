package com.trocandgo.trocandgo.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.apache.tika.Tika;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mock.web.MockMultipartFile;

import com.trocandgo.trocandgo.util.EncryptionUtil;
import com.trocandgo.trocandgo.model.Users;
import com.trocandgo.trocandgo.repository.UserRepository;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import javax.imageio.ImageIO;

class ImageServiceTest {

    // Mock du repository utilisateur pour simuler les interactions avec la base de données.
    @Mock
    private UserRepository userRepository;

    // Mock de l'outil de chiffrement pour simuler le chiffrement des images.
    @Mock
    private EncryptionUtil encryptionUtil;

    // Instance du service à tester.
    private ImageService imageService;

    @BeforeEach
    void setUp() {
        // Initialise les mocks avant chaque test.
        MockitoAnnotations.openMocks(this);
        // Crée une instance d'ImageService avec les mocks injectés.
        imageService = new ImageService(userRepository, encryptionUtil);
    }

    @Test
    void testUploadProfilePicture_WithMockedTika() throws Exception {
        // Arrange : Simule un comportement de Tika pour forcer un type MIME valide (ici "image/jpeg").
        Tika tikaMock = mock(Tika.class);
        // Simule la détection du type MIME comme "image/jpeg".
        when(tikaMock.detect(any(InputStream.class))).thenReturn("image/jpeg");

        // Crée une image JPEG en mémoire et la convertit en tableau d'octets pour simuler une image valide.
        BufferedImage bufferedImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        File tempFile = File.createTempFile("testImage", ".jpg");
        ImageIO.write(bufferedImage, "jpg", tempFile);
        byte[] imageData = Files.readAllBytes(tempFile.toPath());  // Lit les octets du fichier temporaire.

        // Crée un MockMultipartFile qui contient des données valides d'une image JPEG.
        MockMultipartFile image = new MockMultipartFile("image", "profile.jpg", "image/jpeg", imageData);

        // Crée un utilisateur fictif à utiliser dans le test.
        String username = "testUser";
        Users user = new Users();
        user.setName(username);

        // Simule la réponse du repository pour trouver l'utilisateur par son nom.
        when(userRepository.findByName(username)).thenReturn(Optional.of(user));

        // Simule le comportement de l'outil de chiffrement pour renvoyer des données chiffrées fictives.
        byte[] encryptedImage = "encrypted data".getBytes();
        when(encryptionUtil.encrypt(any(byte[].class))).thenReturn(encryptedImage);

        // Simule la méthode save du repository pour qu'elle retourne l'objet utilisateur après avoir été sauvegardé.
        when(userRepository.save(any(Users.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Injecte le mock Tika dans l'instance du service via un setter (si nécessaire).
        imageService.setTika(tikaMock);

        // Act : Appelle la méthode à tester pour uploader l'image de profil.
        String imageUrl = imageService.uploadProfilePicture(image, username);

        // Assert : Vérifie que l'URL de l'image a bien été générée et que l'image a été sauvegardée.
        assertNotNull(imageUrl);  // L'URL de l'image ne doit pas être null.
        verify(userRepository, times(1)).save(any(Users.class));  // Vérifie que la méthode save a été appelée une seule fois.
    }

    @Test
    void testDownloadProfilePicture() throws Exception {
        // Arrange : Crée un utilisateur avec un chemin d'image valide.
        String username = "testUser";
        Users user = new Users();
        user.setName(username);
        user.setPicture("/uploads/profile-pictures/testUser_1234567890.enc");

        // Simule la réponse du repository pour trouver l'utilisateur par son nom.
        when(userRepository.findByName(username)).thenReturn(Optional.of(user));

        // Crée un fichier d'image temporaire et écrit des données chiffrées dedans.
        Path filePath = Path.of("src/main/resources/static/uploads/profile-pictures/testUser_1234567890.enc");
        Files.createDirectories(filePath.getParent());  // Crée les répertoires parents si nécessaires.
        Files.write(filePath, "encrypted data".getBytes());  // Écrit les données chiffrées dans le fichier.

        // Simule le décryptage des données de l'image pour renvoyer des données décryptées.
        byte[] decryptedImage = "decrypted image data".getBytes();
        when(encryptionUtil.decrypt(any(byte[].class))).thenReturn(decryptedImage);

        // Act : Appelle la méthode à tester pour télécharger l'image de profil.
        byte[] result = imageService.downloadProfilePicture(username);

        // Assert : Vérifie que l'image décryptée a bien été renvoyée.
        assertNotNull(result);  // Les données décryptées doivent être présentes.
        assertArrayEquals(decryptedImage, result);  // Les données retournées doivent correspondre aux données décryptées.
    }

    @Test
    void testDownloadProfilePicture_NoImage() throws Exception {
        // Arrange : Crée un utilisateur sans image de profil.
        String username = "testUser";
        Users user = new Users();
        user.setName(username);
        user.setPicture("");  // Pas d'image associée à l'utilisateur.

        // Simule la réponse du repository pour trouver l'utilisateur par son nom.
        when(userRepository.findByName(username)).thenReturn(Optional.of(user));

        // Act & Assert : Vérifie que l'exception attendue est lancée lorsqu'il n'y a pas d'image de profil.
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            imageService.downloadProfilePicture(username);
        });
        assertEquals("No profile picture found.", exception.getMessage());  // Le message d'erreur attendu.
    }

}
