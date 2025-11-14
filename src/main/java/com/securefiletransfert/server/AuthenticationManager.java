package com.securefiletransfert.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire d'authentification
 * Stocke les identifiants en dur (dans une Map)
 */
public class AuthenticationManager {
    
    private static final Map<String, String> credentials = new HashMap<>();
    
    static {
        // Initialisation avec quelques utilisateurs de test
        credentials.put("admin", "admin123");
        credentials.put("user1", "password1");
        credentials.put("test", "test123");
    }
    
    /**
     * Vérifie si les identifiants sont valides
     */
    public static boolean authenticate(String username, String password) {
        String storedPassword = credentials.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }
    
    /**
     * Ajoute un nouvel utilisateur (pour tests)
     */
    public static void addUser(String username, String password) {
        credentials.put(username, password);
    }
}

