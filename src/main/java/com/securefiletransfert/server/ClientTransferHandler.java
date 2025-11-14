package com.securefiletransfert.server;

import com.securefiletransfert.common.CryptoUtils;
import com.securefiletransfert.common.ProtocolConstants;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Thread qui gère la session complète d'un client
 * Implémente les 3 phases du protocole
 */
public class ClientTransferHandler extends Thread {
    
    private final Socket clientSocket;
    private final String serverStoragePath;
    
    public ClientTransferHandler(Socket socket, String storagePath) {
        this.clientSocket = socket;
        this.serverStoragePath = storagePath;
    }
    
    @Override
    public void run() {
        try (
            InputStream inputStream = new java.io.BufferedInputStream(
                clientSocket.getInputStream());
            PrintWriter out = new PrintWriter(
                clientSocket.getOutputStream(), true)
        ) {
            System.out.println("Nouveau client connecté: " + 
                clientSocket.getRemoteSocketAddress());
            
            // PHASE 1 : Authentification
            String authLine = readLine(inputStream);
            if (!handleAuthentication(authLine, out)) {
                System.out.println("Authentification échouée pour: " + 
                    clientSocket.getRemoteSocketAddress());
                return;
            }
            
            // PHASE 2 : Négociation
            String metadataLine = readLine(inputStream);
            FileMetadata metadata = handleNegotiation(metadataLine, out);
            if (metadata == null) {
                System.out.println("Négociation échouée pour: " + 
                    clientSocket.getRemoteSocketAddress());
                return;
            }
            
            // PHASE 3 : Transfert et Vérification
            // Utiliser le flux brut pour les données binaires
            handleTransfer(inputStream, out, metadata);
            
        } catch (IOException e) {
            System.err.println("Erreur lors de la gestion du client: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                System.out.println("Connexion fermée avec: " + 
                    clientSocket.getRemoteSocketAddress());
            } catch (IOException e) {
                System.err.println("Erreur lors de la fermeture de la connexion: " + 
                    e.getMessage());
            }
        }
    }
    
    /**
     * Lit une ligne depuis un InputStream (jusqu'à \n ou \r\n)
     */
    private String readLine(InputStream in) throws IOException {
        StringBuilder line = new StringBuilder();
        int c;
        while ((c = in.read()) != -1) {
            if (c == '\n') {
                break;
            }
            if (c != '\r') {
                line.append((char) c);
            }
        }
        if (line.length() == 0 && c == -1) {
            return null;
        }
        return line.toString();
    }
    
    /**
     * Phase 1 : Authentification
     */
    private boolean handleAuthentication(String authData, PrintWriter out) {
        if (authData == null || authData.isEmpty()) {
            out.println(ProtocolConstants.AUTH_FAIL);
            return false;
        }
        
        String[] parts = authData.split(ProtocolConstants.AUTH_SEPARATOR);
        if (parts.length != 2) {
            out.println(ProtocolConstants.AUTH_FAIL);
            return false;
        }
        
        String username = parts[0];
        String password = parts[1];
        
        if (AuthenticationManager.authenticate(username, password)) {
            out.println(ProtocolConstants.AUTH_OK);
            System.out.println("Authentification réussie pour: " + username);
            return true;
        } else {
            out.println(ProtocolConstants.AUTH_FAIL);
            return false;
        }
    }
    
    /**
     * Phase 2 : Négociation (récupération des métadonnées)
     */
    private FileMetadata handleNegotiation(String metadataLine, PrintWriter out) {
        if (metadataLine == null || metadataLine.isEmpty()) {
            return null;
        }
        
        String[] parts = metadataLine.split("\\" + ProtocolConstants.FIELD_SEPARATOR);
        if (parts.length != 3) {
            return null;
        }
        
        String filename = parts[0];
        long fileSize = Long.parseLong(parts[1]);
        String expectedHash = parts[2];
        
        FileMetadata metadata = new FileMetadata(filename, fileSize, expectedHash);
        
        out.println(ProtocolConstants.READY_FOR_TRANSFER);
        System.out.println("Prêt pour le transfert: " + filename + 
            " (" + fileSize + " bytes)");
        
        return metadata;
    }
    
    /**
     * Phase 3 : Transfert et Vérification
     */
    private void handleTransfer(InputStream inputStream, PrintWriter out, 
                                FileMetadata metadata) {
        try {
            // Lire les données chiffrées
            byte[] encryptedData = new byte[(int) metadata.getFileSize()];
            int totalBytesRead = 0;
            
            while (totalBytesRead < metadata.getFileSize()) {
                int bytesRead = inputStream.read(encryptedData, totalBytesRead, 
                    (int) metadata.getFileSize() - totalBytesRead);
                if (bytesRead == -1) {
                    throw new IOException("Connexion fermée prématurément");
                }
                totalBytesRead += bytesRead;
            }
            
            System.out.println("Fichier chiffré reçu: " + totalBytesRead + " bytes");
            
            // Déchiffrer
            byte[] decryptedData = CryptoUtils.decrypt(encryptedData);
            System.out.println("Fichier déchiffré: " + decryptedData.length + " bytes");
            
            // Vérifier l'intégrité
            String receivedHash = CryptoUtils.calculateFileHash(decryptedData);
            if (!receivedHash.equals(metadata.getHash())) {
                System.err.println("Hash mismatch! Attendu: " + metadata.getHash() + 
                    ", Reçu: " + receivedHash);
                out.println(ProtocolConstants.TRANSFER_FAIL);
                return;
            }
            
            // Sauvegarder le fichier
            Path storageDir = Paths.get(serverStoragePath);
            if (!Files.exists(storageDir)) {
                Files.createDirectories(storageDir);
            }
            
            Path filePath = storageDir.resolve(metadata.getFilename());
            Files.write(filePath, decryptedData);
            
            System.out.println("Fichier sauvegardé: " + filePath);
            out.println(ProtocolConstants.TRANSFER_SUCCESS);
            
        } catch (Exception e) {
            System.err.println("Erreur lors du transfert: " + e.getMessage());
            e.printStackTrace();
            out.println(ProtocolConstants.TRANSFER_FAIL);
        }
    }
    
    /**
     * Classe interne pour stocker les métadonnées du fichier
     */
    private static class FileMetadata {
        private final String filename;
        private final long fileSize;
        private final String hash;
        
        public FileMetadata(String filename, long fileSize, String hash) {
            this.filename = filename;
            this.fileSize = fileSize;
            this.hash = hash;
        }
        
        public String getFilename() { return filename; }
        public long getFileSize() { return fileSize; }
        public String getHash() { return hash; }
    }
}

