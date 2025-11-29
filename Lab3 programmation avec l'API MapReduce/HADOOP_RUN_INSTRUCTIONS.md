# Hadoop Execution Instructions (Final)

Run these commands inside the `hadoop-master` Docker container.

## 1. Start Hadoop Cluster (if not already running)
```bash
# On Windows Host
cd D:\BigData2025
docker start hadoop-master hadoop-slave1 hadoop-slave2
docker exec -it hadoop-master bash
```

## 2. Start Hadoop Services (inside container)
```bash
./start-hadoop.sh
# Verify services
jps
```

## 3. Prepare HDFS Input
```bash
# Clean up previous attempts
hdfs dfs -rm -r /user/root/output_java
hdfs dfs -rm -r /user/root/output_python

# Create input directory and upload data
hdfs dfs -mkdir -p /user/root/input
hdfs dfs -put -f /shared_volume/alice.txt /user/root/input/

# Verify input
hdfs dfs -ls /user/root/input
```

## 4. Run Java WordCount
**IMPORTANT:** Do NOT include the class name in the command. The JAR already knows which class to run.
```bash
hadoop jar /shared_volume/WordCount.jar /user/root/input/alice.txt /user/root/output_java

# View results
hdfs dfs -cat /user/root/output_java/part-r-00000 | head -30
```

## 5. Run Hadoop Streaming (Python)
First, find the correct path to the streaming JAR:
```bash
export STREAMING_JAR=$(find /opt -name "hadoop-streaming*.jar" | head -n 1)
echo "Streaming JAR found at: $STREAMING_JAR"
```

Then run the job using the variable:
```bash
hadoop jar $STREAMING_JAR \
  -files /shared_volume/mapper.py,/shared_volume/reducer.py \
  -mapper "python3 mapper.py" \
  -reducer "python3 reducer.py" \
  -input /user/root/input/alice.txt \
  -output /user/root/output_python

# View results
hdfs dfs -cat /user/root/output_python/part-00000 | head -30
```

## 6. Retrieve Results to Windows (Optional)
```bash
# On Windows Host
mkdir "D:\BigData2025\bigdatalabs\Lab3 programmation avec l'API MapReduce\resultats"
hdfs dfs -get /user/root/output_java   "D:\BigData2025\bigdatalabs\Lab3 programmation avec l'API MapReduce\resultats\output_java"
hdfs dfs -get /user/root/output_python "D:\BigData2025\bigdatalabs\Lab3 programmation avec l'API MapReduce\resultats\output_python"
```
