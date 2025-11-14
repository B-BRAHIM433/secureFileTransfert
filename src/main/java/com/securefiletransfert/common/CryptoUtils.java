package com.securefiletransfert.common;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilitaires pour les opérations cryptographiques
 */
public class CryptoUtils {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String HASH_ALGORITHM = "SHA-256";
    
    // Clé AES prédéfinie (128 bits = 16 bytes)
    // En production, cette clé devrait être échangée de manière sécurisée
    // Pour ce projet, on utilise une clé fixe connue du client et du serveur
    private static final String SECRET_KEY_STRING = "MySecretKey12345"; // 16 caractères = 128 bits
    
    /**
     * Génère ou récupère la clé secrète AES
     */
    public static SecretKey getSecretKey() {
        byte[] keyBytes = SECRET_KEY_STRING.getBytes(StandardCharsets.UTF_8);
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
    
    /**
     * Chiffre des données avec AES
     */
    public static byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey());
        return cipher.doFinal(data);
    }
    
    /**
     * Déchiffre des données avec AES
     */
    public static byte[] decrypt(byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey());
        return cipher.doFinal(encryptedData);
    }
    
    /**
     * Calcule le hash SHA-256 d'un tableau de bytes
     */
    public static String calculateSHA256(byte[] data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
        byte[] hash = digest.digest(data);
        
        // Convertir en hexadécimal
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    /**
     * Calcule le hash SHA-256 d'un fichier (via son contenu en bytes)
     */
    public static String calculateFileHash(byte[] fileContent) throws Exception {
        return calculateSHA256(fileContent);
    }
}

