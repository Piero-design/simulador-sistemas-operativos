# DEPENDENCIAS DEL PROYECTO
## Simulador de Sistemas Operativos

---

## 📚 LIBRERÍAS Y DEPENDENCIAS

### **Librerías de Java Standard Edition (Incluidas en JDK)**

Este proyecto **NO requiere dependencias externas**. Todas las librerías utilizadas vienen incluidas en el JDK 11 o superior.

#### **1. java.util.*** - Estructuras de Datos
```java
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.*;
```

**Uso en el proyecto:**
- `LinkedList`: Colas FIFO para FCFS y FIFO
- `PriorityQueue`: Cola ordenada para SJF
- `HashMap`: Tablas de páginas, mapeos PID→Process
- `ArrayList`: Listas de procesos y métricas

#### **2. java.util.concurrent.*** - Concurrencia
```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
```

**Uso en el proyecto:**
- `Thread`: Clase base para ProcessThread
- `ExecutorService`: Pool de threads para E/S
- `Semaphore`: Control de acceso a CPU
- `BlockingQueue`: Cola de completados de E/S

#### **3. java.util.concurrent.locks.*** - Sincronización
```java
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;
```

**Uso en el proyecto:**
- `ReentrantLock`: Exclusión mutua en secciones críticas
- `Condition`: Variables de condición para espera/notificación
- Coordinación entre planificador y memoria

#### **4. javax.swing.*** - Interfaz Gráfica
```java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
```

**Uso en el proyecto:**
- `JFrame`: Ventana principal
- `JPanel`: Paneles de Gantt, Memoria, Colas
- `JButton`, `JLabel`, `JTextArea`: Controles UI
- `Graphics2D`: Dibujo de diagramas

#### **5. java.io.*** - Entrada/Salida
```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
```

**Uso en el proyecto:**
- `BufferedReader`: Lectura de archivos de procesos
- `FileReader`: Apertura de archivos .txt
- Manejo de excepciones de I/O

---

## 🔧 INSTALACIÓN DE JDK

### **Windows:**

1. Descargar JDK 11 o superior:
   - Oracle JDK: https://www.oracle.com/java/technologies/downloads/
   - OpenJDK: https://adoptium.net/

2. Instalar el ejecutable descargado

3. Configurar variable de entorno:
   ```cmd
   # Agregar a PATH:
   C:\Program Files\Java\jdk-11\bin
   ```

4. Verificar instalación:
   ```cmd
   java -version
   javac -version
   ```

### **macOS:**

1. Instalar Homebrew (si no lo tienes):
   ```bash
   /bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
   ```

2. Instalar OpenJDK:
   ```bash
   brew install openjdk@11
   ```

3. Verificar instalación:
   ```bash
   java -version
   ```

### **Linux (Ubuntu/Debian):**

```bash
# Actualizar repositorios
sudo apt update

# Instalar OpenJDK 11
sudo apt install openjdk-11-jdk

# Verificar instalación
java -version
javac -version
```

### **Linux (Fedora/RHEL):**

```bash
sudo dnf install java-11-openjdk java-11-openjdk-devel
```

---

## 📦 DEPENDENCIAS MAVEN (Opcional)

Si prefieres usar Maven, el archivo `pom.xml` ya está configurado:

```xml
<properties>
    <maven.compiler.source>11</maven.compiler.source>
    <maven.compiler.target>11</maven.compiler.target>
</properties>

<dependencies>
    <!-- NO HAY DEPENDENCIAS EXTERNAS -->
    <!-- Todo usa Java Standard Library -->
</dependencies>
```

**Compilar con Maven:**
```bash
mvn clean compile
```

**Empaquetar JAR:**
```bash
mvn package
```

---

## ✅ VERIFICACIÓN DE ENTORNO

### **Script de Verificación (verificar_entorno.sh):**

```bash
#!/bin/bash

echo "=== VERIFICACIÓN DE ENTORNO ==="
echo ""

# Verificar Java
echo "1. Java Runtime Environment:"
if command -v java &> /dev/null; then
    java -version
    echo "✅ Java instalado"
else
    echo "❌ Java NO instalado"
    echo "   Instalar desde: https://adoptium.net/"
fi

echo ""

# Verificar Javac
echo "2. Java Compiler:"
if command -v javac &> /dev/null; then
    javac -version
    echo "✅ Javac instalado"
else
    echo "❌ Javac NO instalado"
    echo "   Instalar JDK completo"
fi

echo ""

# Verificar versión mínima
echo "3. Versión de Java:"
java_version=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$java_version" -ge 11 ]; then
    echo "✅ Versión Java $java_version (>= 11 requerido)"
else
    echo "❌ Versión Java $java_version insuficiente"
    echo "   Se requiere Java 11 o superior"
fi

echo ""

# Verificar compilación
echo "4. Prueba de Compilación:"
if [ -d "target/classes/simulador/gui" ]; then
    echo "✅ Proyecto compilado"
else
    echo "⚠️  Proyecto sin compilar"
    echo "   Ejecutar: javac -d target/classes src/main/java/simulador/**/*.java"
fi

echo ""
echo "=== VERIFICACIÓN COMPLETA ==="
```

**Ejecutar verificación:**
```bash
chmod +x verificar_entorno.sh
./verificar_entorno.sh
```

---

## 📋 RESUMEN DE DEPENDENCIAS

| Componente | Versión | Requerido | Incluido en JDK |
|------------|---------|-----------|-----------------|
| Java Runtime (JRE) | 11+ | ✅ Sí | ✅ Sí |
| Java Compiler (javac) | 11+ | ✅ Sí | ✅ Sí |
| java.util | Estándar | ✅ Sí | ✅ Sí |
| java.util.concurrent | Estándar | ✅ Sí | ✅ Sí |
| javax.swing | Estándar | ✅ Sí | ✅ Sí |
| java.io | Estándar | ✅ Sí | ✅ Sí |
| **Librerías externas** | - | ❌ No | N/A |
| Maven | 3.6+ | ⚠️ Opcional | ❌ No |

---

## 🎯 CONCLUSIÓN

### **NO SE NECESITAN LIBRERÍAS ADICIONALES**

✅ El proyecto solo usa librerías estándar de Java (JDK 11+)
✅ No requiere Maven (opcional)
✅ No requiere dependencias externas
✅ No requiere instalación de paquetes adicionales

### **Requisito Único:**
- **JDK 11 o superior instalado y configurado**

---

## 📝 NOTAS PARA EL PROFESOR

Este simulador fue diseñado intencionalmente para:
1. ✅ No requerir dependencias externas
2. ✅ Ser portable entre sistemas operativos
3. ✅ Facilitar la ejecución sin configuración compleja
4. ✅ Usar solo librerías estándar de Java

**Todas las funcionalidades se implementaron con la Java Standard Library:**
- Concurrencia: `java.util.concurrent`
- GUI: `javax.swing`
- Estructuras de datos: `java.util`
- E/S: `java.io`

**No se usaron frameworks externos como:**
- ❌ Spring Framework
- ❌ Apache Commons
- ❌ Google Guava
- ❌ JUnit (tests no incluidos)

---

## 🚀 INICIO RÁPIDO

```bash
# 1. Verificar Java
java -version  # Debe ser 11 o superior

# 2. Compilar
javac -d target/classes -sourcepath src/main/java src/main/java/simulador/**/*.java

# 3. Ejecutar
java -cp target/classes simulador.gui.MainWindow

# ¡Listo! 🎉
```
