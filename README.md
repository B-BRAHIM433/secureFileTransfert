# Système de Transfert de Fichiers Sécurisé

Application Client-Serveur pour le transfert de fichiers sécurisé utilisant TCP, avec chiffrement AES et authentification.

## Fonctionnalités

- ✅ Transfert de fichiers via TCP
- ✅ Authentification par login/password
- ✅ Chiffrement AES (Advanced Encryption Standard)
- ✅ Vérification d'intégrité via SHA-256
- ✅ Architecture multi-thread (serveur concurrent)
- ✅ Protocole de négociation en 3 phases

## Architecture

### Serveur (SecureFileServer)
- Écoute sur un port configurable
- Gère plusieurs clients simultanément (un thread par client)
- Implémente les 3 phases du protocole :
  1. **Authentification** : Vérification login/password
  2. **Négociation** : Réception des métadonnées (nom, taille, hash)
  3. **Transfert** : Réception, déchiffrement, sauvegarde et vérification

### Client (SecureFileClient)
- Interface en ligne de commande
- Pré-traitement du fichier :
  - Calcul du hash SHA-256
  - Chiffrement AES
- Communication suivant le protocole en 3 phases

## Compilation

```bash
cd securefiletransfert
mvn clean compile
```

## Utilisation

### 1. Démarrer le serveur

```bash
# Avec les paramètres par défaut (port 8888, dossier server_storage)
mvn exec:java -Dexec.mainClass="com.securefiletransfert.server.SecureFileServer"

# Ou avec des paramètres personnalisés
mvn exec:java -Dexec.mainClass="com.securefiletransfert.server.SecureFileServer" \
              -Dexec.args="8888 /path/to/storage"
```

Ou directement :
```bash
java -cp target/classes com.securefiletransfert.server.SecureFileServer [port] [storage_path]
```

### 2. Lancer le client

```bash
mvn exec:java -Dexec.mainClass="com.securefiletransfert.client.SecureFileClient"
```

Ou directement :
```bash
java -cp target/classes com.securefiletransfert.client.SecureFileClient
```

Le client vous demandera :
- Adresse IP du serveur (par défaut: localhost)
- Port du serveur (par défaut: 8888)
- Login
- Mot de passe
- Chemin du fichier à transférer

## Utilisateurs par défaut

Le serveur contient quelques utilisateurs de test :
- `admin` / `admin123`
- `user1` / `password1`
- `test` / `test123`

## Protocole de Communication

### Phase 1 : Authentification
```
Client → Serveur : "username:password"
Serveur → Client : "AUTH_OK" ou "AUTH_FAIL"
```

### Phase 2 : Négociation
```
Client → Serveur : "filename|size|sha256_hash"
Serveur → Client : "READY_FOR_TRANSFER"
```

### Phase 3 : Transfert
```
Client → Serveur : [fichier chiffré en bytes]
Serveur → Client : "TRANSFER_SUCCESS" ou "TRANSFER_FAIL"
```

## Sécurité

- **Chiffrement** : AES/ECB/PKCS5Padding
- **Hash** : SHA-256 pour la vérification d'intégrité
- **Clé AES** : Clé prédéfinie (pour ce projet éducatif)
  - ⚠️ En production, utiliser un échange de clés sécurisé (ex: Diffie-Hellman, RSA)

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

## Exemple d'utilisation

### Terminal 1 - Serveur
```bash
$ mvn exec:java -Dexec.mainClass="com.securefiletransfert.server.SecureFileServer"
Serveur démarré sur le port 8888
Répertoire de stockage: server_storage
En attente de connexions...
```

### Terminal 2 - Client
```bash
$ mvn exec:java -Dexec.mainClass="com.securefiletransfert.client.SecureFileClient"
=== Client de Transfert de Fichiers Sécurisé ===

Adresse IP du serveur [localhost]: 
Port du serveur [8888]: 
Login: admin
Mot de passe: admin123
Chemin du fichier à transférer: /path/to/myfile.txt
Connecté au serveur localhost:8888
Authentification réussie
Traitement du fichier...
Fichier lu: 1024 bytes
Hash SHA-256 calculé: abc123...
Fichier chiffré: 1024 bytes
Métadonnées envoyées
Serveur prêt pour le transfert
Envoi du fichier chiffré...
Fichier envoyé: 1024 bytes
Transfert confirmé par le serveur
Transfert réussi!
```

## Technologies Utilisées

- Java 21
- Java Sockets (TCP)
- Java Threading
- javax.crypto (AES)
- java.security.MessageDigest (SHA-256)
- Maven

## Notes

- Les fichiers reçus sont sauvegardés dans le répertoire `server_storage/` (ou celui spécifié)
- Le serveur peut gérer plusieurs clients simultanément
- La clé AES est partagée entre client et serveur (prédéfinie pour ce projet)

