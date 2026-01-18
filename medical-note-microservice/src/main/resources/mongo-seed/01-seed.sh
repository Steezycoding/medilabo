#!/bin/sh
set -e

echo "[init] Waiting for Mongo to be ready..."
until mongosh --host localhost --quiet --eval "db.runCommand({ ping: 1 }).ok" >/dev/null 2>&1
do
  sleep 1
done

echo "[init] Importing seed data..."
mongoimport \
  --host localhost \
  --db medicalnotedb \
  --collection notes \
  --file /docker-entrypoint-initdb.d/data.json \
  --jsonArray

echo "[init] Done."