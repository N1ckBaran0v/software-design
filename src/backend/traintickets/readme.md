# Шпаргалка по запуску

## Postgres

Перед запуском сгенерировать данные и поместить в **csv_data/**.

```bash
docker build -t traintickets-postgres -f Dockerfile-postgres .
docker run --name traintickets-postgres -p 5432:5432 -d traintickets-postgres
```

## Mongo

Перед запуском сгенерировать данные и поместить в **mongo_data/**.

```bash
docker build -t traintickets-mongo -f Dockerfile-mongo .
docker run --name traintickets-mongo -p 27017:27017 -d traintickets-mongo
```

## Redis

Можно вообще не запускать. Правда, в таком случае авторизация попросту не будет работать.

```bash
docker build -t traintickets-redis -f Dockerfile-mongo-redis .
docker run --name traintickets-redis -p 6379:6379 -d traintickets-redis
```

## Основное приложение

При первом запуске

```bash
./gradlew wrapper
```

После изменения в коде

```bash
./gradlew jar
./gradlew copyDependencies
```

После добавления компонента или импорта зависимости

```bash
python3 prepare.py
```

Если **start.sh** ранее не существовал

```bash
chmod 777 ./start.sh
```

Запуск приложения

```bash
./start.sh
```
