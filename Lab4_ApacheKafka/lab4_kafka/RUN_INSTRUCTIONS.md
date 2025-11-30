# Application WordCount Kafka - Configuration Complète et Guide d'Exécution

## 🎯 Objectif
Application de comptage de mots en temps réel utilisant Apache Kafka comme système de messagerie distribuée avec un cluster multi-brokers répliqué.

---

## 📋 Configuration Réalisée

### Cluster Kafka Multi-Brokers
| Composant | Localisation | Port |
|-----------|-------------|------|
| **Broker 1** | localhost | 9092 |
| **Broker 2** | localhost | 9093 |
| **Broker 3** | localhost | 9094 |
| **Zookeeper** | localhost | 2181 |
| **Kafka UI** | localhost | 8080 |

### Topic Créé
- **Nom**: `WordCount-Topic`
- **Partitions**: 3
- **Facteur de Réplication**: 3
- **Bootstrap Servers**: `localhost:9092,localhost:9093,localhost:9094`

### Vérification de la Configuration
```bash
docker exec kafka1 kafka-topics --describe --bootstrap-server kafka1:9092,kafka2:9092,kafka3:9092 --topic WordCount-Topic
```

**Résultat Expected**:
```
Topic: WordCount-Topic  TopicId: OQ7bHbOxQxGAQbEDOol8Cg PartitionCount: 3      ReplicationFactor: 3
Topic: WordCount-Topic  Partition: 0    Leader: 3       Replicas: 3,1,2                    Isr: 3,1,2
Topic: WordCount-Topic  Partition: 1    Leader: 1       Replicas: 1,2,3                    Isr: 1,2,3
Topic: WordCount-Topic  Partition: 2    Leader: 2       Replicas: 2,3,1                    Isr: 2,3,1
```

---

## 📦 Classes Java Développées

### WordProducer.java
**Rôle**: Producteur interactif de messages Kafka

**Caractéristiques**:
- Lit le texte saisi au clavier (interactive)
- Envoie chaque mot nettoyé (minuscules, sans ponctuation) à Kafka
- Affiche le numéro de partition et l'offset pour chaque message
- Supporte l'entrée jusqu'à la commande `exit`

**Configuration**:
- `acks: all` - Tous les replicas doivent confirmer
- `retries: 3` - Tentatives de renvoi en cas d'erreur
- `compression: snappy` - Compression des messages

---

### WordCountConsumer.java
**Rôle**: Consommateur avec agrégation et affichage statistique

**Caractéristiques**:
- Consomme les messages du topic `WordCount-Topic`
- Maintient une map de fréquence pour chaque mot
- Affiche chaque mot reçu en temps réel
- Calcule et affiche le **TOP 10 des mots les plus fréquents** toutes les 5 secondes

**Affichage**:
- Comptage en temps réel
- Distribution de fréquence en pourcentage
- Graphique ASCII en barres

---

## 🛠️ Compilation et Packaging

### Prérequis
- Java 8 ou supérieur
- Maven 3.6+
- Docker et Docker Compose (pour Kafka)

### Étapes de Compilation

```powershell
# Naviguer au répertoire du projet
cd Lab4_ApacheKafka\lab4_kafka

# Nettoyer et compiler
mvn clean package -DskipTests
```

### Fichiers JAR Générés
```
target/
├── kafka-interactive-wordcount-app-jar-with-dependencies-jar-with-dependencies.jar
│   └── Main Class: edu.supmti.kafka.WordProducer
│
└── kafka-interactive-wordcount-consumer-app-jar-with-dependencies-jar-with-dependencies.jar
    └── Main Class: edu.supmti.kafka.WordCountConsumer
```

---

## 🚀 Instructions d'Exécution

### Étape 0: Démarrer le Cluster Kafka (si ce n'est pas fait)

```powershell
cd "..\..\lab0_ installation cluster hadoop docker"
docker compose up -d
```

Vérifier que tous les conteneurs sont en cours d'exécution:
```powershell
docker compose ps
```

---

### Étape 1: Compiler les JAR Files

```powershell
cd Lab4_ApacheKafka\lab4_kafka
mvn clean package -DskipTests
```

---

### Étape 2: Copier les JAR Files vers les Conteneurs Docker

Copier les JARs du WordCount Consumer et Producer vers kafka1:

```powershell
docker cp target\kafka-interactive-wordcount-consumer-app-jar-with-dependencies-jar-with-dependencies.jar kafka1:/tmp/

docker cp target\kafka-interactive-wordcount-app-jar-with-dependencies-jar-with-dependencies.jar kafka1:/tmp/

docker cp target\kafka-producer-app-jar-with-dependencies-jar-with-dependencies.jar kafka1:/tmp/

docker cp target\kafka-consumer-app-jar-with-dependencies-jar-with-dependencies.jar kafka1:/tmp/
```

**Vérification**:
```powershell
docker exec kafka1 ls -lh /tmp/*.jar
```

---

### Étape 3: Ouvrir Deux Terminaux Séparés

**Important**: Vous avez besoin de **2 terminaux** car producer et consumer doivent fonctionner simultanément.

---

### TERMINAL 1 - Lancer le CONSOMMATEUR (dans le conteneur Docker)

```powershell
docker exec -it kafka1 java -jar /tmp/kafka-interactive-wordcount-consumer-app-jar-with-dependencies-jar-with-dependencies.jar WordCount-Topic
```

**Expected Output**:
```
Starting WordCount Consumer...
Connecting to Kafka cluster at: localhost:9092
Waiting for messages...
[Message reçu: hello]
[Message reçu: world]
...
TOP 10 DES MOTS LES PLUS FREQUENTS
============================================================
```

Le consommateur affichera chaque mot reçu en temps réel et actualisera les statistiques toutes les 5 secondes.

---

### TERMINAL 2 - Lancer le PRODUCTEUR (dans le conteneur Docker)

```powershell
docker exec -it kafka1 java -jar /tmp/kafka-interactive-wordcount-app-jar-with-dependencies-jar-with-dependencies.jar WordCount-Topic
```

**Expected Output**:
```
Starting WordCount Producer...
Connecting to Kafka cluster at: localhost:9092
Enter text (type 'exit' to quit):
```

Tapez du texte, par exemple:
```
hello world hello kafka is great kafka kafka big data engineering
```

Puis tapez `exit` pour quitter le producteur.

---

## 📝 Exemple d'Utilisation Complet

### Étape 1: Démarrer le Consumer (Terminal 1)
```powershell
docker exec -it kafka1 java -jar /tmp/kafka-interactive-wordcount-consumer-app-jar-with-dependencies-jar-with-dependencies.jar WordCount-Topic
```

Attendez que le message `Waiting for messages...` s'affiche.

### Étape 2: Démarrer le Producer (Terminal 2)
```powershell
docker exec -it kafka1 java -jar /tmp/kafka-interactive-wordcount-app-jar-with-dependencies-jar-with-dependencies.jar WordCount-Topic
```

### Étape 3: Entrer du texte dans le Producer (Terminal 2):
```
hello world hello
kafka is great kafka kafka
big data engineering
hello kafka
data big data
exit
```

### Sortie Attendue du Consumer (Terminal 1):

**Messages reçus en temps réel**:
```
Message reçu: hello
Message reçu: world
Message reçu: hello
Message reçu: kafka
Message reçu: is
Message reçu: great
Message reçu: kafka
Message reçu: kafka
Message reçu: big
Message reçu: data
Message reçu: engineering
Message reçu: hello
Message reçu: kafka
Message reçu: data
Message reçu: big
Message reçu: data
```

**Statistiques en temps réel (mises à jour toutes les 5 secondes)**:
```
TOP 10 DES MOTS LES PLUS FREQUENTS
============================================================
kafka               |    4 ( 16.67%) ################################################
hello               |    3 ( 12.50%) #############################################
data                |    3 ( 12.50%) #############################################
big                 |    2 (  8.33%) ##########################
world               |    1 (  4.17%) #############
is                  |    1 (  4.17%) #############
great               |    1 (  4.17%) #############
engineering         |    1 (  4.17%) #############
============================================================
Total de mots traites: 24
Mots uniques: 8
```

---

## 🧪 Test Rapide avec les Applications Simples

### Test 1: EventProducer (Envoie 10 messages)

```powershell
docker exec kafka1 java -jar /tmp/kafka-producer-app-jar-with-dependencies-jar-with-dependencies.jar Hello-Kafka
```

**Output**:
```
[main] INFO org.apache.kafka.clients.producer.ProducerConfig - Idempotence will be disabled because retries is set to 0.
[main] INFO org.apache.kafka.clients.producer.ProducerConfig - ProducerConfig values: 
...
[main] INFO org.apache.kafka.common.utils.AppInfoParser - Kafka version: 3.5.1
Message envoye avec succes
```

### Test 2: Vérifier les messages avec ConsumerConsole

```powershell
docker exec kafka1 kafka-console-consumer --bootstrap-server localhost:9092 --topic Hello-Kafka --from-beginning --max-messages 10 --timeout-ms 5000
```

**Output**:
```
0
1
2
3
4
5
6
7
8
9
Processed a total of 10 messages
```

✅ Producer et Consumer fonctionnent correctement!

Chaque partition du topic est répliquée sur 3 brokers pour assurer la **haute disponibilité**:

```
Partition 0: Leader=Broker3, Replicas=[3,1,2]
Partition 1: Leader=Broker1, Replicas=[1,2,3]
Partition 2: Leader=Broker2, Replicas=[2,3,1]
```

### Avantages:
- ✅ **Haute disponibilité**: Si un broker tombe, les replicas prennent le relais
- ✅ **Durabilité**: Les données sont présentes sur 3 nœuds
- ✅ **Équilibre**: La charge est distribuée entre les 3 brokers
- ✅ **Tolérance aux pannes**: Perte de 2 brokers tolérable (min ISR = 2)

---

## 📊 Monitoring et Debugging

### Accéder à Kafka UI
```
http://localhost:8080
```

**Dans l'UI, vous pouvez**:
- 👁️ Voir les brokers et leur statut
- 📚 Consulter les topics et leurs partitions
- 🔄 Vérifier la réplication et le leader
- 👥 Voir les consumer groups et leurs offsets
- 📈 Suivre le taux de messages en temps réel

### Vérifier les Topics via CLI
```bash
# Lister tous les topics
docker exec kafka1 kafka-topics --list --bootstrap-server kafka1:9092

# Décrire un topic spécifique
docker exec kafka1 kafka-topics --describe --bootstrap-server kafka1:9092 --topic WordCount-Topic

# Accéder à un broker
docker exec -it kafka1 bash
```

### Vérifier les Consumer Groups
```bash
docker exec kafka1 kafka-consumer-groups --bootstrap-server kafka1:9092 --list

# Détails d'un consumer group
docker exec kafka1 kafka-consumer-groups --bootstrap-server kafka1:9092 --group wordcount-consumer --describe
```

---

## 🐛 Troubleshooting

### ❌ Erreur: "Connection refused"
**Cause**: Kafka n'est pas démarré  
**Solution**:
```powershell
cd "..\..\lab0_ installation cluster hadoop docker"
docker compose up -d
```

### ❌ Erreur: "Topic does not exist"
**Cause**: Le topic `WordCount-Topic` n'a pas été créé  
**Solution**: Créer le topic manuellement
```bash
docker exec kafka1 kafka-topics --create \
  --bootstrap-server kafka1:9092 \
  --replication-factor 3 \
  --partitions 3 \
  --topic WordCount-Topic
```

### ❌ Erreur: "Port already in use"
**Cause**: Les conteneurs Kafka ne sont pas correctement stoppés  
**Solution**:
```powershell
docker compose down -v
docker compose up -d
```

### ❌ JAR not found
**Cause**: Les fichiers JAR n'ont pas été générés  
**Solution**: Compiler à nouveau
```powershell
mvn clean package -DskipTests
```

---

## 📌 Notes Techniques

- **Sérialisation**: Les messages sont sérialisés en String
- **Nettoyage des mots**: Minuscules, sans ponctuation
- **Auto Offset Reset**: `earliest` (lire depuis le début du topic)
- **Auto Commit**: Automatique toutes les 1000ms
- **Compression**: Snappy pour optimiser la bande passante
- **Replication**: Chaque message est écrit sur 3 brokers
- **Min ISR**: 2 brokers doivent confirmer la réception (acks: all)

---

## 🔗 Configuration Docker Complète

Le `docker-compose.yml` inclut:
- ✅ 3 brokers Kafka (confluent/cp-kafka:7.4.4)
- ✅ 1 Zookeeper (confluent/cp-zookeeper:7.4.4)
- ✅ 1 UI Kafka (provectuslabs/kafka-ui)
- ✅ 1 Kafka Connect (confluentinc/cp-kafka-connect:7.4.4)
- ✅ Cluster Hadoop/Spark/Jupyter
- ✅ Volumes persistants pour les données

---

## ✅ Checklist de Validation

- [ ] Docker et Docker Compose sont installés
- [ ] Conteneurs Kafka sont en cours d'exécution (`docker compose ps`)
- [ ] Maven est installé et fonctionne (`mvn --version`)
- [ ] Compilation réussie (`mvn clean package`)
- [ ] Topic `WordCount-Topic` existe (`kafka-topics --list`)
- [ ] Consumer lance sans erreur
- [ ] Producer envoie des messages sans erreur
- [ ] Les statistiques s'affichent en temps réel

---

## 📚 Ressources Utiles

- 🔗 [Kafka Documentation](https://kafka.apache.org/documentation/)
- 🔗 [Kafka Streams](https://kafka.apache.org/documentation/streams/)
- 🔗 [Kafka UI Local](http://localhost:8080)
- 🔗 [Hadoop NameNode](http://localhost:9870)
- 🔗 [YARN ResourceManager](http://localhost:8088)

---

**Version**: 1.0 | **Last Updated**: 2025-11-30 | **Lab**: Lab4 - Apache Kafka
