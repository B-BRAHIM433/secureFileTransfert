package com.securefiletransfert.client;

import com.securefiletransfert.common.ProtocolConstants;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class SecureFileClient {
    
    private String serverAddress;
    private int serverPort;
    private String username;
    private String password;
    private String filePath;
    
    public SecureFileClient(String serverAddress, int serverPort, 
                          String username, String password, String filePath) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.username = username;
        this.password = password;
        this.filePath = filePath;
    }
    

    public void transferFile() {
        try (Socket socket = new Socket(serverAddress, serverPort);
             BufferedReader in = new BufferedReader(
                 new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(
                 socket.getOutputStream(), true);
             DataOutputStream dataOut = new DataOutputStream(
                 socket.getOutputStream())) {
            
            System.out.println("Connecté au serveur " + serverAddress + ":" + serverPort);

            if (!handleAuthentication(in, out)) {
                System.err.println("Échec de l'authentification");
                return;
            }
            
            System.out.println("Traitement du fichier...");
            FileProcessor.ProcessedFile processedFile = FileProcessor.processFile(filePath);

            if (!handleNegotiation(in, out, processedFile)) {
                System.err.println("Échec de la négociation");
                return;
            }

            if (!handleTransfer(dataOut, in, processedFile)) {
                System.err.println("Échec du transfert");
                return;
            }
            
            System.out.println("Transfert réussi!");
            
        } catch (Exception e) {
            System.err.println("Erreur lors du transfert: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private boolean handleAuthentication(BufferedReader in, PrintWriter out) 
            throws IOException {
        String authData = username + ProtocolConstants.AUTH_SEPARATOR + password;
        out.println(authData);
        out.flush(); // S'assurer que l'authentification est envoyée
        String response = in.readLine();
        if (ProtocolConstants.AUTH_OK.equals(response)) {
            System.out.println("Authentification réussie");
            return true;
        } else {
            System.err.println("Authentification échouée: " + response);
            return false;
        }
    }
    

    private boolean handleNegotiation(BufferedReader in, PrintWriter out, 
                                     FileProcessor.ProcessedFile processedFile) 
            throws IOException {
        String metadata = processedFile.getFilename() + 
            ProtocolConstants.FIELD_SEPARATOR + 
            processedFile.getEncryptedSize() + 
            ProtocolConstants.FIELD_SEPARATOR + 
            processedFile.getHash();
        
        out.println(metadata);
        out.flush(); // S'assurer que les métadonnées sont envoyées avant de lire la réponse
        System.out.println("Métadonnées envoyées:");
        System.out.println("  - Nom: " + processedFile.getFilename());
        System.out.println("  - Taille: " + processedFile.getEncryptedSize() + " bytes");
        System.out.println("  - Hash: " + processedFile.getHash());
        
        String response = in.readLine();
        if (ProtocolConstants.READY_FOR_TRANSFER.equals(response)) {
            System.out.println("Serveur prêt pour le transfert");
            return true;
        } else {
            System.err.println("Serveur non prêt: " + response);
            return false;
        }
    }
    

    private boolean handleTransfer(DataOutputStream dataOut, BufferedReader in, 
                                  FileProcessor.ProcessedFile processedFile) 
            throws IOException {
        System.out.println("Envoi du fichier chiffré...");

        dataOut.write(processedFile.getEncryptedContent());
        dataOut.flush();
        
        System.out.println("Fichier envoyé: " + processedFile.getEncryptedSize() + " bytes");

        String response = in.readLine();
        if (ProtocolConstants.TRANSFER_SUCCESS.equals(response)) {
            System.out.println("Transfert confirmé par le serveur");
            return true;
        } else {
            System.err.println("Échec du transfert: " + response);
            return false;
        }
    }
    

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Client de Transfert de Fichiers Sécurisé ===\n");
        
        System.out.print("Adresse IP du serveur [localhost]: ");
        String serverAddress = scanner.nextLine().trim();
        if (serverAddress.isEmpty()) {
            serverAddress = "localhost";
        }
        
        System.out.print("Port du serveur [8888]: ");
        String portStr = scanner.nextLine().trim();
        int serverPort = 8888;
        if (!portStr.isEmpty()) {
            try {
                serverPort = Integer.parseInt(portStr);
            } catch (NumberFormatException e) {
                System.err.println("Port invalide, utilisation du port par défaut: 8888");
            }
        }
        
        System.out.print("Login: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Mot de passe: ");
        String password = scanner.nextLine().trim();
        
        System.out.print("Chemin du fichier à transférer: ");
        String filePath = scanner.nextLine().trim();
        
        scanner.close();

        SecureFileClient client = new SecureFileClient(
            serverAddress, serverPort, username, password, filePath);
        client.transferFile();
    }
}

