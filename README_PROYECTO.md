# Simulador de Sistema Operativo
## Planificación de Procesos y Gestión de Memoria Virtual

**Trabajo Final - Sistemas Operativos**

---

## 📋 Descripción

Simulador educativo que integra:
- ✅ **Planificación de CPU**: FCFS, SJF, Round Robin
- ✅ **Memoria Virtual**: FIFO, LRU, Óptimo
- ✅ **Sincronización**: Semáforos, Mutex, Condition Variables
- ✅ **Gestión de E/S**: Operaciones asíncronas
- ✅ **Interfaz Gráfica**: Gantt, Estado de Memoria, Colas de Procesos

---

## 🚀 Requisitos

- **Java JDK 21** (LTS)
- **MacOS** / Linux / Windows
- IDE recomendado: IntelliJ IDEA o NetBeans

---

## 📦 Estructura del Proyecto

```
simulador-sistemas-operativos/
├── src/main/java/simulador/
│   ├── SimuladorMain.java          # Clase principal
│   ├── process/
│   │   └── Process.java            # Modelo de proceso (PCB)
│   ├── scheduler/
│   │   ├── CPUScheduler.java       # Interfaz de planificación
│   │   ├── FCFS.java               # First Come First Served
│   │   ├── SJF.java                # Shortest Job First
│   │   └── RoundRobin.java         # Round Robin
│   ├── memory/
│   │   ├── PageReplacement.java    # Interfaz de reemplazo
│   │   ├── FIFO.java               # First In First Out
│   │   ├── LRU.java                # Least Recently Used
│   │   ├── Optimal.java            # Algoritmo Óptimo
│   │   └── MemoryManager.java      # Gestor de memoria
│   ├── sync/
│   │   ├── ProcessSync.java        # Sincronización
│   │   └── IOManager.java          # Gestor de E/S
│   ├── utils/
│   │   ├── ProcessParser.java      # Parser de archivos
│   │   └── Metrics.java            # Sistema de métricas
│   └── gui/
│       ├── MainWindow.java         # Ventana principal
│       ├── GanttPanel.java         # Diagrama de Gantt
│       ├── MemoryPanel.java        # Estado de memoria
│       └── ProcessQueuesPanel.java # Colas de procesos
├── procesos.txt                     # Archivo de entrada
└── README.md
```

---

## 🎯 Cómo Ejecutar

### Opción 1: Desde IntelliJ IDEA (Recomendado)

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/Piero-design/simulador-sistemas-operativos.git
   cd simulador-sistemas-operativos
   ```

2. **Abrir en IntelliJ**:
   - File → Open → Seleccionar la carpeta del proyecto

3. **Configurar JDK 21**:
   - File → Project Structure → Project SDK → Java 21

4. **Ejecutar**:
   - Click derecho en `SimuladorMain.java` → Run

### Opción 2: Desde Terminal

```bash
# Compilar
javac -d out -sourcepath src/main/java src/main/java/simulador/SimuladorMain.java src/main/java/simulador/**/*.java

# Ejecutar
java -cp out simulador.SimuladorMain
```

---

## 📝 Formato del Archivo de Procesos

**Archivo**: `procesos.txt`

**Formato**:
```
PID arrivalTime bursts priority pages [pageReferences]
```

**Ejemplo**:
```
P1 0 CPU(4),IO(2),CPU(3) 1 3 [0,1,2,0,1,2,0]
P2 2 CPU(6),IO(1),CPU(2) 2 4 [0,1,2,3,1,0,2,3]
P3 4 CPU(8) 3 2 [0,1,0,1,0]
```

**Campos**:
- `PID`: Identificador del proceso
- `arrivalTime`: Tiempo de llegada
- `bursts`: Ráfagas alternadas CPU/IO (ej: CPU(4),IO(3),CPU(5))
- `priority`: Prioridad (menor = mayor prioridad)
- `pages`: Número de páginas requeridas
- `[pageReferences]`: Secuencia de referencias (opcional)

---

## 🎨 Interfaz Gráfica

La ventana principal muestra:

### 1. **Diagrama de Gantt**
- Visualización temporal de ejecución de procesos
- Colores únicos por proceso
- Escala de tiempo

### 2. **Colas de Procesos**
- ▶️ **Ejecutando**: Proceso actualmente en CPU
- ⏸️ **Listos**: Cola de procesos listos
- ⏹️ **Bloqueados**: Procesos bloqueados (E/S o Memoria)

### 3. **Estado de Memoria**
- Marcos de memoria (libre/ocupado)
- Estadísticas: fallos de página, reemplazos
- Visualización por colores

### 4. **Registro de Eventos**
- Log en tiempo real
- Cambios de estado
- Fallos de página
- Operaciones de E/S

---

## 📊 Métricas Calculadas

### Planificación:
- ⏱️ Tiempo promedio de espera
- 🔄 Tiempo promedio de retorno (turnaround)
- ⚡ Tiempo promedio de respuesta
- 📈 Utilización de CPU (%)

### Memoria:
- ❌ Total de fallos de página
- 🔄 Total de reemplazos
- 📉 Tasa de fallos por proceso

### E/S:
- 📥 Total de operaciones
- ⏳ Tiempo total en E/S

---

## 🔧 Parámetros Configurables

En `SimuladorMain.java`:

```java
int totalFrames = 16;              // Marcos de memoria
String schedAlgorithm = "RR";      // FCFS, SJF, RR
int quantum = 3;                   // Quantum para RR
String memAlgorithm = "LRU";       // FIFO, LRU, Optimal
```

---

## 🧪 Casos de Prueba

### Caso 1: Sin E/S
Prueba algoritmos de planificación sin bloqueos

### Caso 2: Con E/S
Prueba bloqueos y sincronización

### Caso 3: Memoria Escasa
Fuerza reemplazos frecuentes

### Caso 4: Secuencias Conocidas
Demuestra eficiencia del algoritmo Óptimo

---

## 👥 Equipo

- **Integrante 1**: [Nombre] - Planificador y Scheduler
- **Integrante 2**: [Nombre] - Memoria Virtual
- **Integrante 3**: [Nombre] - Sincronización y E/S
- **Integrante 4**: [Nombre] - GUI y Testing

---

## 📚 Referencias

- Silberschatz, A., Galvin, P. B., & Gagne, G. (2018). *Operating System Concepts* (10th ed.)
- Tanenbaum, A. S., & Bos, H. (2015). *Modern Operating Systems* (4th ed.)

---

## 📅 Entregas

- **Fecha límite**: 03/12/2025 12:00 m.
- **Exposiciones**: 04-10/12/2025

---

## ⚖️ Licencia

Este proyecto es académico y educativo.

---

## 📞 Contacto

Para dudas o sugerencias:
- GitHub: [@Piero-design](https://github.com/Piero-design)

---

**¡Buena suerte con el proyecto!** 🚀
