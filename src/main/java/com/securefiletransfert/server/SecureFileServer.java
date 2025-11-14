package com.securefiletransfert.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Serveur principal pour le transfert de fichiers sécurisé
 * Écoute sur un port et délègue chaque client à un thread dédié
 */
public class SecureFileServer {
    
    private static final int DEFAULT_PORT = 8888;
    private static final String DEFAULT_STORAGE_PATH = "server_storage";
    
    private final int port;
    private final String storagePath;
    private ServerSocket serverSocket;
    private boolean running = false;
    
    public SecureFileServer(int port, String storagePath) {
        this.port = port;
        this.storagePath = storagePath;
    }
    
    /**
     * Démarre le serveur
     */
    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            running = true;
            System.out.println("Serveur démarré sur le port " + port);
            System.out.println("Répertoire de stockage: " + storagePath);
            System.out.println("En attente de connexions...");
            
            while (running) {
                Socket clientSocket = serverSocket.accept();
                
                // Déléguer chaque client à un nouveau thread
                ClientTransferHandler handler = new ClientTransferHandler(
                    clientSocket, storagePath);
                handler.start();
                
                System.out.println("Nouveau client accepté, thread créé");
            }
            
        } catch (IOException e) {
            if (running) {
                System.err.println("Erreur serveur: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Arrête le serveur
     */
    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de l'arrêt du serveur: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        String storagePath = DEFAULT_STORAGE_PATH;
        
        // Parse des arguments
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Port invalide, utilisation du port par défaut: " + 
                    DEFAULT_PORT);
            }
        }
        
        if (args.length > 1) {
            storagePath = args[1];
        }
        
        SecureFileServer server = new SecureFileServer(port, storagePath);
        
        // Gestion de l'arrêt propre avec Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nArrêt du serveur...");
            server.stop();
        }));
        
        server.start();
    }
}

