#!/bin/bash
# Build script for MinecraftShop Plugin

echo "🔨 Building MinecraftShop Plugin..."
mvn clean package -DskipTests

if [ -f "target/MinecraftShop-1.0.0.jar" ]; then
    echo "✅ Build erfolgreich!"
    echo "📦 JAR-Datei: target/MinecraftShop-1.0.0.jar"
else
    echo "❌ Build fehlgeschlagen!"
    exit 1
fi
