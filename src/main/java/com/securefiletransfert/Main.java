package com.securefiletransfert;

import com.securefiletransfert.client.SecureFileClient;
import com.securefiletransfert.server.SecureFileServer;

/**
 * Point d'entrée principal
 * Permet de lancer soit le serveur soit le client
 * 
 * Usage:
 *   Serveur: java Main server [port] [storage_path]
 *   Client:  java Main client
 */
public class Main {
    
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            return;
        }
        
        String mode = args[0].toLowerCase();
        
        if ("server".equals(mode)) {
            // Lancer le serveur
            String[] serverArgs = new String[args.length - 1];
            System.arraycopy(args, 1, serverArgs, 0, args.length - 1);
            SecureFileServer.main(serverArgs);
        } else if ("client".equals(mode)) {
            // Lancer le client
            SecureFileClient.main(new String[0]);
        } else {
            System.err.println("Mode invalide: " + mode);
            printUsage();
        }
    }
    
    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  Serveur: java Main server [port] [storage_path]");
        System.out.println("  Client:  java Main client");
        System.out.println();
        System.out.println("Exemples:");
        System.out.println("  java Main server 8888 server_storage");
        System.out.println("  java Main client");
    }
}

