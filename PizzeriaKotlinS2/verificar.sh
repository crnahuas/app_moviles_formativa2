#!/usr/bin/env bash
set -euo pipefail
PROYECTO_PIZZERIA="$(cd "$(dirname "$0")" && pwd)"
source "$PROYECTO_PIZZERIA/scripts/herramientas.sh"
mkdir -p "$PROYECTO_PIZZERIA/build"
"$COMPILADOR_KOTLIN" "$PROYECTO_PIZZERIA"/src/*.kt "$PROYECTO_PIZZERIA"/tests/*.kt -jvm-target 17 -include-runtime -d "$PROYECTO_PIZZERIA/build/pruebas.jar"
"$JAVA_PIZZERIA" -cp "$PROYECTO_PIZZERIA/build/pruebas.jar" Pruebas
