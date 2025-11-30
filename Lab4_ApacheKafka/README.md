# Lab4 : Apache Kafka

## Objectifs
- Installation et démarrage d'Apache Kafka et Zookeeper avec Docker Compose
- Première utilisation d'Apache Kafka (topics, producer, consumer)
- Développement d'applications Kafka avec Java (Producer, Consumer, Word Count Streams)
- Utilisation de Kafka Streams pour le traitement des données en temps réel

---

## 1. Architecture du Cluster Kafka

Le cluster Kafka est configuré dans **Docker Compose** avec les composants suivants:

### Infrastructure Docker
- **Zookeeper**: Coordination du cluster (port 2181)
- **Kafka Brokers** (3 instances):
  - `kafka1`: Port 9092
  - `kafka2`: Port 9093
  - `kafka3`: Port 9094
- **Kafka UI**: Interface web pour visualiser le cluster (port 8080)
- **Kafka Connect**: Service de connecteurs (port 8083)

### Démarrage automatique
Les conteneurs sont définis dans `../../lab0_ installation cluster hadoop docker/docker-compose.yml` et doivent être lancés avec:

```powershell
cd "d:\BigData2025\bigdatalabs\lab0_ installation cluster hadoop docker"
docker compose up -d
```

---

## 2. Vérification du Cluster

### a) Vérifier que les conteneurs sont en cours d'exécution
```powershell
docker compose ps
```

### b) Accéder à Kafka UI
Ouvrez votre navigateur et allez à: **http://localhost:8080**

### c) Accéder à un broker Kafka
```powershell
docker exec -it kafka1 bash
```

---

## 3. Opérations de base sur les Topics

### a) Créer un topic
```bash
kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 3 --partitions 3 --topic Hello-Kafka
```

### b) Afficher la liste des topics
```bash
kafka-topics --list --bootstrap-server localhost:9092
```

### c) Description d'un topic
```bash
kafka-topics --describe --topic Hello-Kafka --bootstrap-server localhost:9092
```

### d) Écrire des événements dans un topic
```bash
kafka-console-producer --bootstrap-server localhost:9092 --topic Hello-Kafka
```
Tapez des messages, puis faites `Ctrl+C` pour quitter.

### e) Lire des événements
```bash
kafka-console-consumer --bootstrap-server localhost:9092 --topic Hello-Kafka --from-beginning
```

---

## 4. Applications Java Kafka

### Structure du Projet
```
lab4_kafka/
├── pom.xml                          # Configuration Maven
├── src/main/java/edu/supmti/kafka/
│   ├── EventProducer.java          # Producteur simple d'événements
│   ├── EventConsumer.java          # Consommateur simple d'événements
│   ├── WordCountApp.java           # Kafka Streams Word Count
│   ├── WordProducer.java           # Producteur interactif de mots
│   └── WordCountConsumer.java      # Consommateur de comptage de mots
└── target/
    └── [JAR files générés par Maven]
```

### Dépendances Maven
- **Kafka Clients** 3.5.1: Client Kafka pour producer/consumer
- **Kafka Streams** 3.5.1: Framework de traitement des flux
- **SLF4J**: Logging simple

### Compilation
```powershell
cd .\lab4_kafka
mvn clean package
```

Cela génère les fichiers JAR exécutables:
- `kafka-producer-app-jar-with-dependencies.jar`
- `kafka-consumer-app-jar-with-dependencies.jar`
- `kafka-wordcount-streams-app-jar-with-dependencies.jar`
- `kafka-interactive-wordcount-app-jar-with-dependencies.jar`
- `kafka-interactive-wordcount-consumer-app-jar-with-dependencies.jar`

---

## 5. Exécution des Applications

### a) Producteur Simple (EventProducer)
```bash
java -cp "target/kafka-producer-app-jar-with-dependencies.jar" edu.supmti.kafka.EventProducer
```

### b) Consommateur Simple (EventConsumer)
```bash
java -cp "target/kafka-consumer-app-jar-with-dependencies.jar" edu.supmti.kafka.EventConsumer
```

### c) Word Count avec Kafka Streams (WordCountApp)
```bash
java -cp "target/kafka-wordcount-streams-app-jar-with-dependencies.jar" edu.supmti.kafka.WordCountApp
```

### d) Word Count Interactif
Dans un terminal, lancez le producteur:
```bash
java -cp "target/kafka-interactive-wordcount-app-jar-with-dependencies.jar" edu.supmti.kafka.WordProducer
```

Dans un autre terminal, lancez le consommateur:
```bash
java -cp "target/kafka-interactive-wordcount-consumer-app-jar-with-dependencies.jar" edu.supmti.kafka.WordCountConsumer
```

---

## 6. Configuration et Tuning

### Bootstrap Servers
Depuis Kafka 2.x, utilisez `--bootstrap-server` au lieu de `--zookeeper`.

### Facteur de Réplication
Le cluster est configuré avec un facteur de réplication de 3 pour la haute disponibilité:
- `KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 3`
- `KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR: 3`
- `KAFKA_MIN_INSYNC_REPLICAS: 2`

### Volumes Persistants
Les données du cluster Kafka et Hadoop sont persistées dans des volumes Docker nommés.

---

## 7. Arrêt du Cluster

```powershell
cd "d:\BigData2025\bigdatalabs\lab0_ installation cluster hadoop docker"
docker compose down
```

Pour supprimer également les volumes:
```powershell
docker compose down -v
```

---

## 8. Troubleshooting

### Les conteneurs ne démarrent pas
```powershell
docker compose logs -f
```

### Erreur de port déjà utilisé
```powershell
docker ps -a  # Vérifier les conteneurs en cours d'exécution
docker stop <container-id>  # Arrêter les conteneurs conflictuels
```

### Vérifier la connexion Kafka
```bash
kafka-broker-api-versions --bootstrap-server localhost:9092
```

---

## 9. Ressources Utiles

- **Kafka Documentation**: https://kafka.apache.org/documentation/
- **Kafka Streams**: https://kafka.apache.org/documentation/streams/
- **Kafka UI**: http://localhost:8080
- **Hadoop NameNode**: http://localhost:9870
- **YARN ResourceManager**: http://localhost:8088

---

## Notes
- Assurez-vous que Docker et Docker Compose sont installés et en cours d'exécution
- Les trois brokers Kafka travaillent ensemble avec réplication pour assurer la haute disponibilité
- Zookeeper doit être exécuté avant les brokers Kafka
- Les données persistes à travers les redémarrages du conteneur
