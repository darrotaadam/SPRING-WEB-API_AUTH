#!/bin/sh

echo "[*] $0"
set -e

if [ -z "$AUTH_TOKEN_AES256_KEY" ]; then
  AUTH_TOKEN_AES256_KEY=$(openssl rand -base64 32)
  export AUTH_TOKEN_AES256_KEY
  echo "[*] Clé AUTH_TOKEN_AES256_KEY générée au démarrage du conteneur"
fi

exec java -Dspring.profiles.active=docker -jar app.jar