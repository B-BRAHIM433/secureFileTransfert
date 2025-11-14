package com.securefiletransfert.client;

import com.securefiletransfert.common.CryptoUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Classe pour le pré-traitement des fichiers côté client
 * - Calcul du hash SHA-256
 * - Chiffrement AES
 */
public class FileProcessor {
    
    /**
     * Traite un fichier : calcule le hash et le chiffre
     */
    public static ProcessedFile processFile(String filePath) throws Exception {
        Path path = Paths.get(filePath);
        
        // Vérifier que le fichier existe
        if (!Files.exists(path)) {
            throw new IOException("Le fichier n'existe pas: " + filePath);
        }
        
        // Lire le contenu du fichier
        byte[] fileContent = Files.readAllBytes(path);
        System.out.println("Fichier lu: " + fileContent.length + " bytes");
        
        // Calculer le hash SHA-256 du fichier original
        String hash = CryptoUtils.calculateFileHash(fileContent);
        System.out.println("Hash SHA-256 calculé: " + hash);
        
        // Chiffrer le contenu
        byte[] encryptedContent = CryptoUtils.encrypt(fileContent);
        System.out.println("Fichier chiffré: " + encryptedContent.length + " bytes");
        
        // Récupérer le nom du fichier
        String filename = path.getFileName().toString();
        
        return new ProcessedFile(filename, encryptedContent, hash, fileContent.length);
    }
    
    /**
     * Classe pour stocker les informations du fichier traité
     */
    public static class ProcessedFile {
        private final String filename;
        private final byte[] encryptedContent;
        private final String hash;
        private final long originalSize;
        
        public ProcessedFile(String filename, byte[] encryptedContent, 
                           String hash, long originalSize) {
            this.filename = filename;
            this.encryptedContent = encryptedContent;
            this.hash = hash;
            this.originalSize = originalSize;
        }
        
        public String getFilename() { return filename; }
        public byte[] getEncryptedContent() { return encryptedContent; }
        public String getHash() { return hash; }
        public long getOriginalSize() { return originalSize; }
        public int getEncryptedSize() { return encryptedContent.length; }
    }
}

