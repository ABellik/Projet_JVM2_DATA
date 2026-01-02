# 🎮 Projet JVM2_DATA - Flux d'événements de jeux vidéos

Projet de groupe JVM2 / Ingénierie des données - ET4 IIM

## 📋 Description

Système distribué basé sur des événements pour gérer une plateforme de jeux vidéo avec communication asynchrone via Kafka.

## 🏗️ Architecture

Le projet est composé de **3 microservices indépendants** :

- **editeur** (Java) : Gestion des éditeurs, publication de jeux et patches
- **plateforme** (Kotlin) : Hub central, catalogue, utilisateurs, monitoring
- **joueur** (Java) : Simulation de l'activité des joueurs

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
- **Host** : localhost:5432
- **Database** : editeur_db
- **User** : editeur
- **Password** : editeur123

### Plateforme DB
- **Host** : localhost:5433
- **Database** : plateforme_db
- **User** : plateforme
- **Password** : plateforme123

### Joueur DB
- **Host** : localhost:5434
- **Database** : joueur_db
- **User** : joueur
- **Password** : joueur123


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
