# multi-module

# Kafka API
## Build API
```shell
cd kafka-api
mvn clean package -am -DskipTests
```

# Kafka Producer
## Build Docker Image
```shell
cd kafka-producer
docker build --no-cache -f Dockerfile -t dev.perfectbogus/kafka-producer:0.0.1 -t dev.perfectbogus/kafka-producer:latest
```

## Build Package
```shell
cd kafka-producer
mvn clean package -am -DskipTests
```

# Kafka Consumer
## Build

# Kafka Infra
```shell
cd docker
cp .env.example .env

```
```shell
cd docker
docker compose up kafka kafka-ui -d
```

```shell
cd docker
docker compose down -v
```


