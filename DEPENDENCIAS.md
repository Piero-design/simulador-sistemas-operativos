# DEPENDENCIAS DEL PROYECTO
## Simulador de Sistemas Operativos

---

## Resumen rápido
- Solo necesita **JDK 11 o superior**; no hay librerías externas.
- Funciona en Windows, macOS y Linux sin configuraciones especiales.
- Maven **es opcional**; el `pom.xml` ya fija `source/target` a 11.
- Todo el código usa la Java Standard Library (`java.util`, `java.util.concurrent`, `javax.swing`, `java.io`).

---

## Requisitos obligatorios

| Componente | Versión mínima | Motivo |
|------------|----------------|--------|
| Java Runtime (JRE) | 11 | Ejecutar la aplicación |
| Java Compiler (javac) | 11 | Compilar el código fuente |

> Con un JDK 11+ instalado cumples ambos requisitos.

### Principales paquetes estándar utilizados
- `java.util`: estructuras de datos para colas, listas y tablas.
- `java.util.concurrent` y `.locks`: sincronización de hilos y semáforos.
- `javax.swing` y `java.awt`: interfaz gráfica y renderizado del diagrama de Gantt.
- `java.io`: lectura de archivos de procesos y configuración.

No se emplean frameworks ni bibliotecas de terceros (Spring, Apache Commons, JUnit, etc.).

---

## Instalación del JDK 11+

### Windows
1. Descarga desde [Oracle](https://www.oracle.com/java/technologies/downloads/) o [Adoptium](https://adoptium.net/).
2. Instala y agrega `C:\Program Files\Java\jdk-11\bin` al `PATH` si el instalador no lo hizo.
3. Verifica:
   ```cmd
   java -version
   javac -version
   ```

### macOS
```bash
brew install openjdk@11
/usr/libexec/java_home -V   # confirma la ruta instalada
java -version
```
> Si no usas Homebrew, descarga el DMG desde Oracle/Adoptium e instálalo manualmente.

### Linux
- Ubuntu/Debian:
  ```bash
  sudo apt update
  sudo apt install openjdk-11-jdk
  java -version
  javac -version
  ```
- Fedora/RHEL:
  ```bash
  sudo dnf install java-11-openjdk java-11-openjdk-devel
  java -version
  ```

---

## Herramientas opcionales

| Herramienta | ¿Para qué sirve? | Instalación sugerida |
|-------------|------------------|----------------------|
| Maven 3.6+  | Compilar, limpiar y empaquetar con un sólo comando | `brew install maven`, `sudo apt install maven`, `choco install maven`, etc. |

El `pom.xml` ya incluye:
```xml
<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
</properties>
```

Comandos útiles:
```bash
mvn clean compile       # preparar clases
mvn exec:java -Dexec.mainClass="simulador.gui.MainWindow"  # lanzar GUI
mvn package             # generar JAR en target/
```

---

## Verificación express
```bash
java -version   # debe indicar 11 o superior
javac -version  # mismo resultado
```

Si ambos comandos funcionan, puedes compilar y ejecutar.

---

## Compilación manual (sin Maven)
```bash
# Crear carpeta de salida si no existe
mkdir -p target/classes

# Compilar todo el código fuente
javac -d target/classes -sourcepath src/main/java src/main/java/simulador/**/*.java

# Ejecutar la interfaz gráfica
java -cp target/classes simulador.gui.MainWindow

# Modo headless (sin GUI, parámetros en INSTRUCCIONES_EJECUCION.md)
java -cp target/classes simulador.core.Simulator --help
```

---

## Notas finales
- El proyecto es completamente portable; basta con copiar el código y tener un JDK 11+ disponible.
- No se requiere conexión a internet para compilar/ejecutar una vez instalado el JDK.
- Las instrucciones detalladas de uso (GUI y modo headless) están en `INSTRUCCIONES_EJECUCION.md`.

Con esto, el entorno queda listo para ejecutar el simulador. 🎉
