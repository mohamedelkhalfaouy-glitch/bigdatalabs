# Démarrage d'un petit cluster Hadoop avec Docker

Ce dossier contient une configuration Docker Compose pour démarrer un petit cluster HDFS composé d'un **Namenode** et de **2 Datanodes**.

Prérequis
- Docker Desktop (ou Docker Engine) installé et en cours d'exécution.
- `docker-compose` disponible dans le PATH.
- PowerShell (Windows) pour exécuter les scripts fournis.

Fichiers importants
- `docker-compose.yml` : définition des services Namenode, Datanode1 et Datanode2.
- `start-cluster.ps1` : script PowerShell pour démarrer le cluster.
- `stop-cluster.ps1` : script PowerShell pour arrêter et supprimer le cluster et ses volumes.

Dossier partagé host <-> container
- Créez ou utilisez le dossier sur l'hôte : `D:\BigData2025\lab0_ installation cluster hadoop docker\hadoop_project`.
- Ce dossier est monté automatiquement dans chaque conteneur sous le chemin `/hadoop_project`.

Exemples d'utilisation
- Depuis l'hôte (PowerShell) :
  ```powershell
  echo "Bonjour depuis l'hôte" > D:\BigData2025\lab0_ installation cluster hadoop docker\hadoop_project\hello_from_host.txt
  ```
- Depuis le container Namenode :
  ```powershell
  docker exec -it namenode bash -c "ls -la /hadoop_project && cat /hadoop_project/hello_from_host.txt"
  ```

Remarque: sur Windows, si Docker Desktop demande des autorisations pour partager le lecteur `D:`, acceptez-les (Settings -> Resources -> File Sharing). Si vous préférez un autre chemin local, modifiez le `docker-compose.yml` et remontez les services.

Démarrage (PowerShell)

Ouvrez PowerShell et placez-vous dans ce dossier, puis lancez :

```powershell
.\n+cd "lab0_ installation cluster hadoop docker"
.\
./start-cluster.ps1
```

Vérifications
- Voir les conteneurs en cours : `docker ps`
- Logs du Namenode : `docker logs -f namenode`
- Interface Web du Namenode : http://localhost:9870
- Interfaces Web des Datanodes : http://localhost:9864 et http://localhost:9865

Commandes utiles à l'intérieur du container Namenode
- Lister la racine HDFS :
  ```powershell
  docker exec -it namenode bash -c "hdfs dfs -ls /"
  ```

Arrêt et nettoyage

```powershell
./stop-cluster.ps1
```

Remarques
- Les images utilisées sont publiques (`bde2020/hadoop-namenode` et `bde2020/hadoop-datanode`). Si votre réseau d'entreprise bloque Docker Hub, récupérez les images à l'avance ou adaptez le `docker-compose.yml` vers une image locale.
- Les ports mappés localement permettent d'accéder aux UIs. Si un port est déjà pris, modifiez les mappings dans `docker-compose.yml`.
