#!/bin/bash

# Script de compilación para el simulador

echo "═══════════════════════════════════════"
echo "  Compilando Simulador de SO"
echo "═══════════════════════════════════════"

# Crear directorio de salida
mkdir -p out

# Compilar todos los archivos Java
echo "Compilando archivos fuente..."

javac -d out \
    -sourcepath src/main/java \
    src/main/java/simulador/*.java \
    src/main/java/simulador/**/*.java

if [ $? -eq 0 ]; then
    echo "✓ Compilación exitosa"
    echo ""
    echo "Para ejecutar:"
    echo "  java -cp out simulador.SimuladorMain"
    echo ""
else
    echo "✗ Error en la compilación"
    exit 1
fi
