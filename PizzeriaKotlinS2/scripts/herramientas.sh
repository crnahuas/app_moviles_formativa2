#!/usr/bin/env bash

# Se carga desde ejecutar.sh o verificar.sh. No modifica la configuración del sistema.
if [[ -z "${JAVA_HOME:-}" && -x /usr/libexec/java_home ]]; then
    JAVA_HOME="$(/usr/libexec/java_home -v 21 2>/dev/null || /usr/libexec/java_home)"
    export JAVA_HOME
fi

if [[ -n "${KOTLINC:-}" ]]; then
    COMPILADOR_KOTLIN="$KOTLINC"
elif command -v kotlinc >/dev/null 2>&1; then
    COMPILADOR_KOTLIN="$(command -v kotlinc)"
elif [[ -x '/Applications/IntelliJ IDEA.app/Contents/plugins/Kotlin/kotlinc/bin/kotlinc' ]]; then
    COMPILADOR_KOTLIN='/Applications/IntelliJ IDEA.app/Contents/plugins/Kotlin/kotlinc/bin/kotlinc'
else
    echo 'No se encontró kotlinc. Configura KOTLINC o ejecuta el proyecto desde IntelliJ IDEA.' >&2
    exit 1
fi

if [[ -n "${JAVA_HOME:-}" && -x "$JAVA_HOME/bin/java" ]]; then
    JAVA_PIZZERIA="$JAVA_HOME/bin/java"
elif command -v java >/dev/null 2>&1; then
    JAVA_PIZZERIA="$(command -v java)"
else
    echo 'No se encontró Java. Configura un JDK 17 o superior.' >&2
    exit 1
fi
