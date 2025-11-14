package com.securefiletransfert.common;

/**
 * Constantes du protocole de communication
 */
public class ProtocolConstants {
    
    // Messages d'authentification
    public static final String AUTH_OK = "AUTH_OK";
    public static final String AUTH_FAIL = "AUTH_FAIL";
    
    // Messages de négociation
    public static final String READY_FOR_TRANSFER = "READY_FOR_TRANSFER";
    
    // Messages de transfert
    public static final String TRANSFER_SUCCESS = "TRANSFER_SUCCESS";
    public static final String TRANSFER_FAIL = "TRANSFER_FAIL";
    
    // Séparateurs
    public static final String FIELD_SEPARATOR = "|";
    public static final String AUTH_SEPARATOR = ":";
    
    // Taille du buffer pour le transfert
    public static final int BUFFER_SIZE = 8192;
    
    private ProtocolConstants() {
        // Classe utilitaire, pas d'instanciation
    }
}

