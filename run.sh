#!/bin/bash

# Script de ejecución para el simulador

echo "═══════════════════════════════════════"
echo "  Ejecutando Simulador de SO"
echo "═══════════════════════════════════════"
echo ""

if [ ! -d "out" ]; then
    echo "No se encontró el directorio 'out'. Compilando primero..."
    ./compile.sh
fi

java -cp out simulador.SimuladorMain
