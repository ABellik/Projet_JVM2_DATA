# 🎮 Projet JVM2_DATA - Flux d'événements de jeux vidéos

Auteurs : 
- Casssandra AMOUSSA
- Ambre BAFOUR
- Adam BELLIK
- Mame Sira DIOP
- Floriane TANG

Projet de groupe JVM2 / Ingénierie des données - ET4 IIM

## 📋 Description

Système distribué basé sur des événements pour gérer une plateforme de jeux vidéo avec communication asynchrone via Kafka.

## 🏗️ Architecture

Le projet est composé de **3 microservices indépendants** :

- **editeur** (Java) : Gestion des éditeurs, publication de jeux et patches
- **plateforme** (Java) : Hub central, catalogue, utilisateurs, monitoring
- **joueur** (Kotlin) : Simulation de l'activité des joueurs

## 🛠️ Technologies utilisées

- **Langages** : Java , Kotlin
- **Messaging** : Apache Kafka
- **Sérialisation** : Apache Avro
- **Schema Registry** : Confluent Schema Registry
- **Base de données** : PostgreSQL
- **Build** : Maven
- **Containerisation** : Docker

## 📦 Prérequis

- Java
- Maven
- Docker
- IntelliJ IDEA Ultimate

## 🚀 Installation et lancement

### 1. Cloner le projet sur votre PC



### 2. Démarrer l'infrastructure

```bash
docker-compose up -d
```

### 3. Compiler le projet

```bash
mvn clean install
```

Cette commande va :
- Générer les classes Java depuis les schémas Avro (dans `common/`)
- Compiler tous les modules

## 🌐 Interfaces web

Une fois Docker lancé :

- **Kafka UI** : http://localhost:8080 (voir topics, messages, schemas)
- **PgAdmin** : http://localhost:5050 (admin@projet.com / admin)
- **Schema Registry API** : http://localhost:8081

## 🗄️ Bases de données

### Éditeur DB
- **Host** : localhost:5435
- **Database** : editeur_db
- **User** : editeur
- **Password** : editeur123

### Plateforme DB
- **Host** : localhost:5433
- **Database** : plateforme_db
- **User** : plateforme
- **Password** : plateforme123



## 📊 Monitoring

### Kafka UI
Accédez à http://localhost:8080 pour :
- Voir tous les topics et leur contenu
- Consulter les schémas Avro enregistrés

## 🛑 Arrêter le projet

Arrêter les microservices : `Ctrl+C` dans chaque terminal

Arrêter l'infrastructure Docker :
```bash
docker-compose down
```

Supprimer aussi les volumes (⚠️ supprime les données) :
```bash
docker-compose down -v
```

## 🐛 Problèmes connus

### Kafka ne démarre pas
```bash
docker-compose down -v
docker-compose up -d
```

### Classes Avro non générées
```bash
cd common
mvn clean generate-sources
cd ..
mvn clean install
```

## 🧪 Procédure de test du module Joueur

### 1) Effacer les modules (si créés auparavant)
```bash
docker-compose down -v
```

### 2) Relancer le conteneur
```bash
docker-compose up -d
```

### 3) Lancer le fichier Main.java présent dans le module *plateforme*
#### Cela devrait créer les tables de la base de données de *plateforme*

### 4) Ajouter les éléments de tests suivants dans la base de données
```bash
INSERT INTO "Editeur" VALUES (42, 'admin456','nomEditeurTest','Type??','editeur@email.com');

INSERT INTO "Jeu" VALUES (1243,'NomJeuTest',50,40,'BASE','2.0.1',42);

INSERT INTO "Jeu" VALUES (1244,'NomJeuTest2',50,40,'BASE','2.0.1',42);

INSERT INTO "Plateforme" VALUES (1, 'PC'),(2,'PS5'),(3,'XBOX');

INSERT INTO "Licence" VALUES (1243,1), (1243,3);
```

### 5) Lancer le fichier UIJoueur2.kt dans le module *Joueur*

### 6) Tester l'interface du module *Joueur*
 Il devrait être possible de :
- Créer un compte
- Se connecter à celui-ci
- Voir son profil dès que l'on est connecté
- Voir la liste des jeux disponibles
- Acheter un jeu disponible
- Jouer à un jeu possédé
- Évaluer un jeu possédé
- Voir le profil d'un autre joueur
- Se déconnecter ou quitter l'interface de Joueur


## 🧪 Procédure de test du module Editeur

### 1) Effacer les modules (si créés auparavant)
```bash
docker-compose down -v
```

### 2) Relancer le conteneur
```bash
docker-compose up -d
```
### 3) Exemple de création de topic
```bash
docker exec -it kafka kafka-topics \
--bootstrap-server kafka:29092 \
--create --topic game-sessions --partitions 1 --replication-factor 1
```
### 4) Vérification de la création des topics
- sur Kafka UI : http://localhost:8080

### 5) Lancer le fichier Main.java présent dans le module *Editeur*
#### Cela devrait créer les tables de la base de données de *Editeur*

### 6) Tester l'interface du module *Editeur*

#### Gestion du compte éditeur
•	créer un compte éditeur
•	modifier les informations du compte
•	supprimer le compte

#### Publication de jeux et DLC
•	publier un jeu
•	publier un DLC après consommation et filtrage des commentaires

#### Crashs et patchs
•	consommer les sessions de jeu
•	détecter automatiquement les crashs
•	déclencher et publier un patch selon un filtre

PS : La plateforme n'étant pas fini, pour tester les comptes eiditeurs notamment les operations de modifications ou de suppressions, on utilise un idEditeur déjà existant depuis la table Jeu du module Editeur

