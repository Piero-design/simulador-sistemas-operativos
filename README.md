# Simulador Integrado de Planificación de Procesos y Gestión de Memoria Virtual

## Descripción
Simulador educativo de un sistema operativo simplificado que integra módulos de planificación de procesos y gestión de memoria virtual, permitiendo observar la interacción entre ambos y el impacto de las políticas de planificación y reemplazo de páginas sobre el rendimiento del sistema.

## Características Implementadas

### 1. Algoritmos de Planificación de CPU
- ✅ **FCFS** (First Come, First Served)
- ✅ **SJF** (Shortest Job First)
- ✅ **Round Robin** (con quantum configurable)

### 2. Algoritmos de Reemplazo de Páginas
- ✅ **FIFO** (First In, First Out)
- ✅ **LRU** (Least Recently Used)
- ✅ **Óptimo** (Optimal)

### 3. Simulación con Hilos
- ✅ Cada proceso se ejecuta como un hilo independiente (Thread)
- ✅ Estados de proceso: NEW, READY, RUNNING, BLOCKED, TERMINATED
- ✅ Cambio de contexto entre procesos

### 4. Sincronización
- ✅ Semáforos para control de CPU
- ✅ Monitores y condiciones para coordinación
- ✅ Prevención de condiciones de carrera
- ✅ Coordinación entre planificador y memoria

### 5. Gestión de Memoria Virtual
- ✅ Memoria dividida en marcos de página
- ✅ Tabla de páginas por proceso
- ✅ Registro de fallos de página
- ✅ Reemplazos de página

### 6. Manejo de E/S (Puntos Extra +2)
- ✅ Ráfagas alternadas de CPU y E/S
- ✅ Procesos bloqueados por E/S
- ✅ Notificaciones de finalización de E/S

### 7. Métricas de Desempeño
- ✅ Tiempo promedio de espera
- ✅ Tiempo promedio de retorno (turnaround)
- ✅ Tiempo promedio de respuesta
- ✅ Utilización de CPU
- ✅ Total de fallos de página
- ✅ Total de reemplazos de página

### 8. Interfaz Gráfica
- ✅ Panel de configuración
- ✅ Visualización de cola de procesos
- ✅ Tabla de estado de memoria
- ✅ Log de eventos en tiempo real
- ✅ Métricas de desempeño
- ✅ Control de simulación (iniciar/detener/reiniciar)

## Estructura del Proyecto

```
src/main/java/simulador/
├── core/
│   └── Simulator.java              # Coordinador principal
├── process/
│   ├── Process.java                # Definición de proceso
│   └── ProcessThread.java          # Thread de ejecución
├── scheduler/
│   ├── CPUScheduler.java           # Interfaz de planificador
│   ├── FCFS.java                   # First Come First Served
│   ├── SJF.java                    # Shortest Job First
│   └── RoundRobin.java             # Round Robin
├── memory/
│   ├── PageReplacement.java        # Interfaz de reemplazo
│   ├── FIFO.java                   # First In First Out
│   ├── LRU.java                    # Least Recently Used
│   ├── Optimal.java                # Algoritmo Óptimo
│   └── MemoryManager.java          # Gestor de memoria
├── sync/
│   └── SynchronizationManager.java # Sincronización
├── io/
│   └── IOManager.java              # Gestor de E/S
├── metrics/
│   └── MetricsCollector.java       # Recolector de métricas
├── utils/
│   └── FileParser.java             # Parser de archivos
└── gui/
    └── MainWindow.java             # Interfaz gráfica
```

## Requisitos

- **Java**: JDK 11 o superior
- **Maven**: 3.6 o superior
- **Sistema Operativo**: Windows, Linux o macOS con soporte gráfico

## Instalación

1. Clonar el repositorio:
```bash
git clone https://github.com/Piero-design/simulador-sistemas-operativos.git
cd simulador-sistemas-operativos
```

2. Compilar el proyecto:
```bash
mvn clean compile
```

## Ejecución

### Opción 1: Con Maven
```bash
mvn compile exec:java -Dexec.mainClass="simulador.gui.MainWindow"
```

### Opción 2: Crear JAR ejecutable
```bash
mvn package
java -jar target/simulador-sistemas-operativos-1.0-SNAPSHOT.jar
```

### Opción 3: Desde el IDE
Ejecutar la clase `simulador.gui.MainWindow` directamente desde tu IDE favorito.

## Uso del Simulador

### 1. Preparar Archivo de Procesos

Crear un archivo de texto con el formato:
```
PID tiempo_llegada ráfagas prioridad páginas
```

**Ejemplo (`procesos.txt`):**
```
P1 0 CPU(5) 1 3
P2 2 CPU(3) 2 2
P3 4 CPU(8) 3 4
```

**Con E/S:**
```
P1 0 CPU(4),E/S(3),CPU(5) 1 4
P2 2 CPU(6),E/S(2),CPU(3) 2 5
```

### 2. Configurar el Simulador

En la interfaz gráfica:
1. Seleccionar archivo de procesos
2. Elegir algoritmo de planificación (FCFS, SJF, Round Robin)
3. Configurar quantum (para Round Robin)
4. Elegir algoritmo de memoria (FIFO, LRU, Optimal)
5. Configurar número de marcos de memoria

### 3. Ejecutar la Simulación

1. Hacer clic en **"Cargar Procesos"**
2. Hacer clic en **"Iniciar Simulación"**
3. Observar la ejecución en tiempo real:
   - Cola de procesos y sus estados
   - Estado de los marcos de memoria
   - Log de eventos
   - Métricas de desempeño

### 4. Analizar Resultados

Al finalizar la simulación, se mostrarán:
- Tiempos de espera por proceso
- Tiempos de retorno (turnaround)
- Tiempos de respuesta
- Utilización de CPU
- Fallos de página
- Reemplazos de página

## Archivos de Prueba Incluidos

- **`procesos.txt`**: Procesos simples sin E/S
- **`procesos_io.txt`**: Procesos con múltiples ráfagas de E/S
- **`procesos_comparacion.txt`**: Casos para comparar algoritmos
- **`config.txt`**: Archivo de configuración opcional

## Ejemplos de Uso

### Comparar Algoritmos de Planificación

1. Cargar `procesos_comparacion.txt`
2. Ejecutar con FCFS, guardar métricas
3. Reiniciar y ejecutar con SJF
4. Reiniciar y ejecutar con Round Robin (quantum=3)
5. Comparar tiempos de espera y utilización de CPU

### Comparar Algoritmos de Memoria

1. Usar el mismo archivo de procesos
2. Ejecutar con FIFO
3. Ejecutar con LRU
4. Ejecutar con Optimal
5. Comparar fallos de página y reemplazos

### Probar con E/S

1. Cargar `procesos_io.txt`
2. Observar cómo los procesos se bloquean por E/S
3. Ver la interacción entre CPU y dispositivos de E/S
4. Analizar el impacto en el tiempo de respuesta

## Características Técnicas

### Sincronización
- **Semáforos**: Control de acceso a CPU (un proceso a la vez)
- **Locks**: Protección de estructuras compartidas
- **Conditions**: Notificación entre módulos
- **Thread-safe**: Todas las operaciones críticas están protegidas

### Gestión de Memoria
- **Marcos de página**: Configurables (1-50)
- **Tabla de páginas**: Una por proceso
- **Fallos de página**: Contabilizados y registrados
- **Reemplazos**: Según algoritmo seleccionado

### Manejo de E/S
- **Asíncrono**: No bloquea el planificador
- **ExecutorService**: Pool de threads para E/S
- **BlockingQueue**: Para notificaciones de finalización

## Autores

- Piero Design

## Licencia

Este proyecto es con fines educativos para el curso de Sistemas Operativos.