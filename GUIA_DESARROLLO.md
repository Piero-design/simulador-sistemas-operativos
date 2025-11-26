# Guía de Desarrollo para el Equipo

## 🚀 Cómo Empezar

### 1. Clonar y Configurar
```bash
git clone https://github.com/Piero-design/simulador-sistemas-operativos.git
cd simulador-sistemas-operativos
```

### 2. Abrir en IntelliJ IDEA
- File → Open → Seleccionar la carpeta del proyecto
- Esperar a que IntelliJ indexe
- Verificar que JDK 21 esté configurado

### 3. Compilar y Ejecutar

**Opción A - Desde IntelliJ:**
- Click derecho en `SimuladorMain.java`
- Run 'SimuladorMain.main()'

**Opción B - Desde Terminal:**
```bash
chmod +x compile.sh run.sh
./compile.sh
./run.sh
```

---

## 📂 Estructura del Código

### Paquetes Principales:

#### `simulador.process`
- **Process.java**: Modelo del PCB (Process Control Block)
  - Estados: NEW, READY, RUNNING, BLOCKED_MEM, BLOCKED_IO, TERMINATED
  - Atributos: PID, prioridad, páginas, ráfagas

#### `simulador.scheduler`
- **CPUScheduler.java**: Interfaz de planificación
- **FCFS.java**: First Come First Served
- **SJF.java**: Shortest Job First  
- **RoundRobin.java**: Round Robin con quantum
- **SchedulerEngine.java**: ⭐ Motor principal de simulación

#### `simulador.memory`
- **PageReplacement.java**: Interfaz de reemplazo
- **FIFO.java**: First In First Out
- **LRU.java**: Least Recently Used
- **Optimal.java**: Algoritmo Óptimo
- **MemoryManager.java**: Gestor de marcos y tablas de páginas

#### `simulador.sync`
- **ProcessSync.java**: Semáforos, Mutex, Condition Variables
- **IOManager.java**: Gestor de operaciones de E/S asíncronas

#### `simulador.utils`
- **ProcessParser.java**: Lee archivos de procesos
- **Metrics.java**: Calcula todas las métricas
- **Burst.java**: Representa ráfagas CPU/IO

#### `simulador.gui`
- **MainWindow.java**: Ventana principal integrada
- **GanttPanel.java**: Diagrama de Gantt visual
- **MemoryPanel.java**: Visualización de marcos
- **ProcessQueuesPanel.java**: Colas de procesos

---

## 🎯 Tareas Pendientes por Módulo

### 🔴 Prioridad Alta

#### 1. **Completar SchedulerEngine** (Responsable: Integrante 2)
- ✅ Loop principal implementado
- ⚠️ Mejorar manejo de quantum en RR
- ⚠️ Implementar preemption completa
- ⚠️ Agregar soporte para prioridades

#### 2. **Algoritmo por Prioridades** (Responsable: Integrante 2)
Crear `Priority.java`:
```java
public class Priority implements CPUScheduler {
    private PriorityQueue<Process> queue;
    // Implementar ordenamiento por prioridad
}
```

#### 3. **Mejorar Sincronización** (Responsable: Integrante 3)
- ✅ Semáforos básicos implementados
- ⚠️ Agregar detección de deadlocks
- ⚠️ Mejorar condition variables

#### 4. **Clock Algorithm** (Responsable: Integrante 3)
Crear `Clock.java` en `memory/`:
```java
public class Clock implements PageReplacement {
    // Implementar algoritmo del reloj
}
```

### 🟡 Prioridad Media

#### 5. **Casos de Prueba Completos** (Responsable: Integrante 4)
- ✅ `procesos.txt` básico
- ✅ `procesos_caso2.txt` - alta memoria
- ✅ `procesos_caso3.txt` - SJF
- ⚠️ Crear caso 4: Comparativa FIFO vs LRU vs Óptimo
- ⚠️ Crear caso 5: Round Robin vs FCFS

#### 6. **Botones Funcionales en GUI** (Responsable: Integrante 4)
En `MainWindow.java`:
```java
loadButton.addActionListener(e -> {
    // Abrir diálogo para seleccionar archivo
    JFileChooser fileChooser = new JFileChooser();
    // cargar procesos
});

startButton.addActionListener(e -> {
    // Iniciar simulación
});
```

#### 7. **Exportar Resultados** (Responsable: Integrante 4)
- Generar CSV con Gantt
- Exportar métricas a archivo
- Guardar configuración de simulación

### 🟢 Mejoras Opcionales (+2 puntos extras)

#### 8. **Configuración Dinámica**
- Panel de configuración antes de simular
- Seleccionar algoritmos desde GUI
- Ajustar quantum en tiempo real

#### 9. **Gráficos Mejorados**
- Gráfico de líneas para CPU usage
- Histograma de fallos de página
- Timeline interactivo

#### 10. **Análisis Comparativo**
- Ejecutar múltiples algoritmos automáticamente
- Tabla comparativa de métricas
- Recomendación del mejor algoritmo

---

## 🧪 Cómo Probar

### Test Básico
```bash
./run.sh
```
Debería ver:
1. Ventana con 4 paneles
2. Log mostrando procesos
3. Gantt actualizándose
4. Memoria cambiando de color

### Test con Diferentes Algoritmos
Modificar en `SimuladorMain.java`:
```java
String schedAlgorithm = "FCFS"; // o "SJF" o "RR"
String memAlgorithm = "FIFO";   // o "LRU" o "Optimal"
int quantum = 2;                 // solo para RR
int totalFrames = 12;
```

### Test con Archivos Diferentes
```java
List<Process> processes = ProcessParser.parseFile("procesos_caso2.txt");
```

---

## 📊 Métricas a Validar

Asegurarse que se calculan correctamente:

- [x] Tiempo promedio de espera
- [x] Tiempo promedio de retorno
- [x] Tiempo promedio de respuesta
- [x] Utilización de CPU (%)
- [x] Total de fallos de página
- [x] Tasa de fallos
- [x] Total de operaciones E/S

---

## 🐛 Problemas Conocidos

### 1. Quantum no se respeta completamente
**Solución**: En `SchedulerEngine.executeCurrentProcess()`, dividir ráfagas en sub-unidades.

### 2. GUI se congela con muchos procesos
**Solución**: Ya se ejecuta en thread separado, pero considerar throttling de updates.

### 3. Optimal requiere referencias futuras
**Solución**: Siempre incluir `[pageReferences]` en archivos de prueba.

---

## 📝 Para el Informe IEEE

### Sección: Diseño del Simulador

**Diagrama de Clases**:
```
┌─────────────┐       ┌──────────────┐
│   Process   │──────▶│SchedulerEngine│
└─────────────┘       └──────────────┘
                             │
      ┌──────────────────────┼──────────────────────┐
      │                      │                      │
┌─────▼─────┐        ┌──────▼──────┐      ┌───────▼──────┐
│CPUScheduler│        │MemoryManager│      │  IOManager   │
└───────────┘        └─────────────┘      └──────────────┘
```

**Flujo de Procesos**:
1. Proceso llega → NEW
2. Se carga en memoria → READY
3. Scheduler lo despacha → RUNNING
4. Si tiene IO → BLOCKED_IO
5. IO termina → READY
6. Todas las ráfagas completas → TERMINATED

### Sección: Resultados

Incluir:
- Tabla comparativa de algoritmos
- Capturas de pantalla del simulador
- Gráficos de Gantt para cada caso
- Análisis de fallos de página

---

## 🎤 Para la Exposición

### División de la Presentación (20 min):

**Minuto 1-3: Introducción** (Todos)
- Qué es el simulador
- Objetivos cumplidos

**Minuto 4-7: Módulo de Planificación** (Integrante 2)
- Demostrar FCFS vs SJF vs RR
- Explicar cambios de estado
- Mostrar Gantt

**Minuto 8-11: Módulo de Memoria** (Integrante 3)
- Explicar FIFO, LRU, Óptimo
- Mostrar fallos de página en vivo
- Tabla de páginas

**Minuto 12-15: Sincronización y E/S** (Integrante 3)
- Semáforos y mutex
- Cómo se coordinan los módulos
- Demostrar bloqueos por E/S

**Minuto 16-18: GUI y Métricas** (Integrante 4)
- Recorrer la interfaz
- Explicar cada panel
- Mostrar métricas finales

**Minuto 19-20: Conclusiones** (Todos)
- Lecciones aprendidas
- Dificultades superadas
- Preguntas

---

## ✅ Checklist Pre-Entrega

- [ ] Código compila sin errores
- [ ] Todos los algoritmos funcionan
- [ ] GUI muestra todo correctamente
- [ ] Métricas se calculan bien
- [ ] Al menos 4 casos de prueba
- [ ] README.md actualizado
- [ ] Código comentado
- [ ] Sin warnings importantes
- [ ] Informe IEEE completo
- [ ] Presentación lista

---

## 🆘 Contacto del Equipo

- **Coordinador**: [Nombre]
- **GitHub**: https://github.com/Piero-design/simulador-sistemas-operativos
- **Reuniones**: [Días y horario]

---

**¡Éxito en el proyecto!** 🚀
