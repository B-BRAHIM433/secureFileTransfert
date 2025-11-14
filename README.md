# Système de Transfert de Fichiers Sécurisé

## Compilation

```bash
cd securefiletransfert
mvn clean compile
```

## Utilisation

### 1. Démarrer le serveur

```bash
mvn exec:java -Dexec.mainClass="com.securefiletransfert.server.SecureFileServer"

# Ou avec des paramètres personnalisés
mvn exec:java -Dexec.mainClass="com.securefiletransfert.server.SecureFileServer" \
              -Dexec.args="8888 /path/to/storage"
```

### 2. Lancer le client

```bash
mvn exec:java -Dexec.mainClass="com.securefiletransfert.client.SecureFileClient"
```

## Utilisateurs par défaut

Le serveur contient quelques utilisateurs de test :
- `admin` / `admin123`
- `user1` / `password1`
- `test` / `test123`



## Structure du Projet

```
src/main/java/com/securefiletransfert/
├── common/
│   ├── ProtocolConstants.java    # Constantes du protocole
│   └── CryptoUtils.java           # Utilitaires cryptographiques
├── server/
│   ├── SecureFileServer.java      # Serveur principal
│   ├── ClientTransferHandler.java # Gestionnaire de session client
│   └── AuthenticationManager.java # Gestionnaire d'authentification
└── client/
    ├── SecureFileClient.java      # Client principal
    └── FileProcessor.java         # Pré-traitement des fichiers
```

## Exemple d'utilisation dans notre Pc personelle

### Terminal 1 - Client
```bash
=== Client de Transfert de Fichiers Sécurisé ===

Adresse IP du serveur [localhost]: 127.0.0.1
Port du serveur [8888]: 8888
Login: admin
Mot de passe: admin123
Chemin du fichier à transférer: /home/baychou/Documents/Books/LINUX_NOTES_DEVOPS.pdf  
Connecté au serveur 127.0.0.1:8888
Authentification réussie
Traitement du fichier...
Fichier lu: 1518293 bytes
Hash SHA-256 calculé: bba8a7ff08822fb0a255e20617a70fd27d0c413094fac68ac5ea567ec913b55a
Fichier chiffré: 1518304 bytes
Métadonnées envoyées:
  - Nom: LINUX_NOTES_DEVOPS.pdf
  - Taille: 1518304 bytes
  - Hash: bba8a7ff08822fb0a255e20617a70fd27d0c413094fac68ac5ea567ec913b55a
Serveur prêt pour le transfert
Envoi du fichier chiffré...
Fichier envoyé: 1518304 bytes
Transfert confirmé par le serveur
Transfert réussi!
```

### Terminal 2 - Serveur
```bash
Serveur démarré sur le port 8888
Répertoire de stockage: server_storage
En attente de connexions...
Nouveau client accepté, thread créé
Nouveau client connecté: /127.0.0.1:55720
Authentification réussie pour: admin
Prêt pour le transfert: LINUX_NOTES_DEVOPS.pdf (1518304 bytes)
Fichier chiffré reçu: 1518304 bytes
Fichier déchiffré: 1518293 bytes
Fichier sauvegardé: server_storage/LINUX_NOTES_DEVOPS.pdf
Connexion fermée avec: /127.0.0.1:55720
```


