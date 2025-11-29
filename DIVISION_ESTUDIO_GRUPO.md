# DIVISIÓN DE ESTUDIO PARA EXPOSICIÓN
## Simulador de Sistemas Operativos - Grupo de 4 integrantes

---

## 📚 DISTRIBUCIÓN DE MÓDULOS

### **INTEGRANTE 1: Módulo de Planificación de CPU**
**Tiempo de exposición:** 4-5 minutos

#### **Archivos a estudiar:**
```
src/main/java/simulador/scheduler/
├── CPUScheduler.java          (Interface - 20 líneas)
├── FCFS.java                  (88 líneas)
├── SJF.java                   (95 líneas)
└── RoundRobin.java            (130 líneas)

src/main/java/simulador/gui/
└── GanttPanel.java            (Entender cómo se visualiza)
```

#### **Conceptos clave a dominar:**

1. **Cola de procesos listos (Ready Queue)**
   - Cómo se mantiene la cola de procesos esperando ejecución
   - Operaciones: `addProcess()`, `getNextProcess()`, `isEmpty()`

2. **FCFS (First Come, First Served)**
   - Algoritmo más simple: primero en llegar, primero en ejecutar
   - No apropiativo
   - Usa `LinkedList` como cola FIFO
   - Método clave: `schedule()` retorna el primero de la cola

3. **SJF (Shortest Job First)**
   - Selecciona el proceso con menor tiempo restante
   - Puede ser apropiativo o no apropiativo
   - Usa `PriorityQueue` ordenada por `remainingTime`
   - Previene starvation con procesos largos

4. **Round Robin**
   - Apropiativo con quantum configurable
   - Cada proceso ejecuta máximo `quantum` unidades
   - Si no termina, vuelve al final de la cola
   - Ideal para sistemas interactivos

5. **Cambio de contexto**
   - Coordinación con `SynchronizationManager`
   - Espera confirmación de memoria antes de ejecutar
   - Método `acquireCPU()` y `releaseCPU()`

#### **Diagrama a explicar:**
```
[Proceso llega] → [Cola Ready] → [Scheduler selecciona] 
                                        ↓
                                  [Verifica memoria]
                                        ↓
                                  [Ejecuta en CPU]
                                        ↓
                                  [Termina o vuelve a Ready]
```

#### **Métricas a explicar:**
- **Tiempo de espera:** Tiempo en Ready antes de ejecutar
- **Tiempo de retorno:** Tiempo total desde llegada hasta terminación
- **Utilización de CPU:** Porcentaje de tiempo que CPU estuvo ocupada

#### **Preguntas esperadas:**
1. ¿Cuál es la diferencia entre FCFS y SJF?
2. ¿Cómo funciona el quantum en Round Robin?
3. ¿Qué pasa si un proceso no puede ejecutar porque no tiene páginas en memoria?
4. ¿Qué algoritmo es mejor para cada escenario?

#### **Demo en vivo:**
- Cargar `procesos.txt`
- Ejecutar con FCFS, luego SJF, luego RR
- Mostrar diferencias en el diagrama de Gantt
- Comparar tiempos de espera y retorno

---

### **INTEGRANTE 2: Módulo de Gestión de Memoria Virtual**
**Tiempo de exposición:** 4-5 minutos

#### **Archivos a estudiar:**
```
src/main/java/simulador/memory/
├── PageReplacement.java       (Interface - 15 líneas)
├── MemoryManager.java         (200 líneas) ← CLAVE
├── FIFO.java                  (70 líneas)
├── LRU.java                   (85 líneas)
└── Optimal.java               (100 líneas)
```

#### **Conceptos clave a dominar:**

1. **Paginación**
   - Memoria dividida en marcos (frames) de tamaño fijo
   - Procesos divididos en páginas del mismo tamaño
   - Configuración: 10 marcos por defecto

2. **Tabla de páginas**
   - Cada proceso tiene su propia tabla de páginas
   - Clase interna: `PageTable` y `PageEntry`
   - Mapea páginas virtuales → marcos físicos
   - Atributos: `frameNumber`, `valid`, `lastUsed`, `loadTime`

3. **FIFO (First In, First Out)**
   - Reemplaza la página que llegó primero
   - Usa una cola para trackear orden de llegada
   - Simple pero puede sufrir anomalía de Belady

4. **LRU (Least Recently Used)**
   - Reemplaza la página menos recientemente usada
   - Usa campo `lastUsed` para trackear accesos
   - Mejor rendimiento que FIFO en la mayoría de casos

5. **Algoritmo Óptimo**
   - Reemplaza la página que no se usará por más tiempo
   - Requiere conocer referencias futuras
   - Imposible en la práctica, pero útil como referencia

6. **Fallo de página (Page Fault)**
   - Ocurre cuando proceso necesita página no en memoria
   - `loadPages()` carga páginas necesarias
   - Si no hay marcos libres → reemplazo usando algoritmo seleccionado

#### **Diagrama a explicar:**
```
[Proceso necesita página] → [¿Está en memoria?]
                                    ↓ NO
                              [Page Fault]
                                    ↓
                            [¿Hay marco libre?]
                    ↓ NO                    ↓ SÍ
          [Algoritmo de reemplazo]      [Cargar página]
                    ↓                         ↓
          [Víctima seleccionada]        [Actualizar tabla]
                    ↓                         ↓
          [Cargar nueva página]         [Notificar listo]
```

#### **Métricas a explicar:**
- **Fallos de página:** Total de veces que se necesitó cargar páginas
- **Reemplazos realizados:** Cuántas veces se desalojó una página
- **Tasa de aciertos:** (Accesos - Fallos) / Accesos

#### **Preguntas esperadas:**
1. ¿Qué es un fallo de página y cuándo ocurre?
2. ¿Por qué LRU es generalmente mejor que FIFO?
3. ¿Por qué el algoritmo Óptimo no se puede usar en la realidad?
4. ¿Qué pasa si un proceso tiene más páginas que marcos disponibles?

#### **Demo en vivo:**
- Cargar `procesos_comparacion.txt` (5 procesos, 24 páginas total, 10 marcos)
- Ejecutar con FIFO → mostrar fallos de página
- Ejecutar con LRU → comparar fallos
- Mostrar tabla de memoria en tiempo real

---

### **INTEGRANTE 3: Módulo de Sincronización**
**Tiempo de exposición:** 4-5 minutos

#### **Archivos a estudiar:**
```
src/main/java/simulador/sync/
└── SynchronizationManager.java    (134 líneas) ← MUY IMPORTANTE

src/main/java/simulador/core/
└── Simulator.java                 (326 líneas - entender flujo)
```

#### **Conceptos clave a dominar:**

1. **Problema de condiciones de carrera**
   - Múltiples hilos (procesos) acceden a recursos compartidos
   - Sin sincronización → resultados impredecibles
   - Ejemplo: planificador y memoria actualizando estado del proceso simultáneamente

2. **Mecanismos de sincronización implementados:**

   **a) Locks (ReentrantLock)**
   - `cpuLock`: Protege acceso al planificador
   - `memoryLock`: Protege acceso a memoria
   - Uso: `lock.lock()` → código crítico → `lock.unlock()`

   **b) Conditions (Variables de condición)**
   - `memoryReady`: Señala cuando páginas están cargadas
   - `cpuAvailable`: Señala cuando CPU está libre
   - Uso: `condition.await()` para esperar, `condition.signal()` para notificar

   **c) Semaphores**
   - `cpuSemaphore`: Controla acceso exclusivo a CPU (1 permit)
   - Asegura que solo 1 proceso ejecute a la vez

3. **Flujo de coordinación:**
   ```
   1. Planificador selecciona proceso → acquireCPU()
   2. Verifica si tiene páginas → waitForMemory()
   3. Memoria carga páginas → notifyMemoryReady()
   4. Proceso ejecuta en CPU
   5. Proceso termina ráfaga → releaseCPU()
   6. Otro proceso puede tomar CPU
   ```

4. **Bloqueos y desbloqueos:**
   - Proceso bloqueado por memoria: `BLOCKED_MEM`
   - Proceso bloqueado por E/S: `BLOCKED_IO`
   - Transiciones de estado seguras con locks

5. **Deadlock prevention:**
   - Orden consistente de adquisición de locks
   - Timeouts en operaciones de espera
   - No hay circular wait

#### **Diagrama a explicar:**
```
[Planificador Thread]          [Proceso Thread]          [Memoria Thread]
        ↓                              ↓                         ↓
   acquireCPU() ──────────────→ ¿Páginas ready? ────→ loadPages()
        ↓                              ↓                         ↓
   waitForMemory() ←── BLOCKED ── wait() ←────────────── signal()
        ↓                              ↓                         ↓
   RUNNING ←────────────────── execute() ─────────────→ monitoring
        ↓                              ↓                         
   releaseCPU() ←───────────── done() ─────────────────→
```

#### **Métricas a explicar:**
- **Tiempo bloqueado por memoria:** Cuánto esperó cada proceso
- **Conteo de bloqueos:** Cuántas veces se bloqueó
- **No deadlocks:** Demostrar que el sistema nunca se traba

#### **Preguntas esperadas:**
1. ¿Qué es una condición de carrera y cómo la previenen?
2. ¿Qué diferencia hay entre Lock y Semaphore?
3. ¿Qué pasa si el planificador intenta ejecutar un proceso sin páginas?
4. ¿Cómo se evitan deadlocks en el sistema?

#### **Demo en vivo:**
- Cargar `procesos_io.txt` (con E/S)
- Mostrar en log: bloqueos por memoria
- Mostrar transiciones: READY → RUNNING → BLOCKED_MEM → READY
- Explicar cada mensaje de sincronización en el log

---

### **INTEGRANTE 4: Procesos Simulados (Threads) + Integración + E/S**
**Tiempo de exposición:** 4-5 minutos

#### **Archivos a estudiar:**
```
src/main/java/simulador/process/
├── Process.java               (145 líneas) ← Modelo
└── ProcessThread.java         (195 líneas) ← Ejecución

src/main/java/simulador/io/
└── IOManager.java             (119 líneas)

src/main/java/simulador/metrics/
└── MetricsCollector.java      (172 líneas)

src/main/java/simulador/gui/
└── MainWindow.java            (Entender UI)
```

#### **Conceptos clave a dominar:**

1. **Proceso como Thread**
   - Cada proceso = 1 hilo independiente (`ProcessThread extends Thread`)
   - Ejecución concurrente real usando hilos de Java
   - Método `run()` contiene lógica de ejecución

2. **Estados del proceso:**
   ```java
   enum State {
       NEW,          // Recién creado
       READY,        // Listo para ejecutar
       RUNNING,      // Ejecutando en CPU
       BLOCKED_MEM,  // Esperando páginas
       BLOCKED_IO,   // Esperando E/S
       TERMINATED    // Finalizado
   }
   ```

3. **Atributos del proceso:**
   - `pid`: Identificador único
   - `arrivalTime`: Cuándo llega al sistema
   - `bursts`: Lista de ráfagas (CPU, E/S, CPU...)
   - `priority`: Prioridad (para algoritmo opcional)
   - `pages`: Páginas requeridas
   - `remainingTime`: Tiempo restante de ejecución

4. **Ciclo de vida del proceso:**
   ```
   NEW → READY → RUNNING → [CPU burst] → E/S? 
                                ↓ Sí      ↓ No
                          BLOCKED_IO   TERMINATED
                                ↓
                            [E/S completa]
                                ↓
                              READY
   ```

5. **Ráfagas alternadas (CPU y E/S):**
   - Formato: `CPU(4),E/S(3),CPU(5)`
   - Procesamiento secuencial de ráfagas
   - E/S asíncrona con `IOManager`

6. **Manejo de E/S (+2 puntos extra):**
   - `IOManager` usa `ExecutorService` para E/S paralela
   - `BlockingQueue` para completados
   - E/S no bloquea a otros procesos
   - Callback cuando E/S termina

7. **Métricas recolectadas:**
   - `MetricsCollector` registra:
     - Tiempo de espera (wait time)
     - Tiempo de retorno (turnaround time)
     - Tiempo de respuesta (response time)
     - Fallos de página
     - Utilizacion de CPU

8. **Comunicación entre hilos:**
   - Cola compartida de procesos listos
   - Métodos sincronizados para cambiar estado
   - Notificaciones con `Condition` variables

#### **Diagrama a explicar (Flujo completo):**
```
[Usuario carga procesos.txt] 
        ↓
[Simulator crea Process objects]
        ↓
[Cada Process → ProcessThread]
        ↓
[Threads inician] → [Esperan en READY]
        ↓
[Scheduler selecciona] → [Adquiere CPU lock]
        ↓
[Verifica memoria] → [Si no tiene → BLOCKED_MEM]
        ↓
[Páginas cargadas] → [RUNNING]
        ↓
[Ejecuta ráfaga CPU] → [Simula con Thread.sleep()]
        ↓
[¿Siguiente ráfaga E/S?]
    ↓ Sí              ↓ No
[BLOCKED_IO]      [READY o TERMINATED]
    ↓
[IOManager.submit()]
    ↓
[Thread.sleep() simula E/S]
    ↓
[E/S completa → READY]
```

#### **Métricas a explicar:**
- **Tiempo de espera promedio:** Σ(tiempo en READY) / n
- **Tiempo de retorno promedio:** Σ(terminación - llegada) / n
- **Utilización CPU:** (tiempo ocupado / tiempo total) × 100%
- **Fallos de página totales:** Contador global
- **Throughput:** Procesos completados / tiempo

#### **Preguntas esperadas:**
1. ¿Cómo se implementa un proceso como Thread en Java?
2. ¿Cuál es la diferencia entre BLOCKED_MEM y BLOCKED_IO?
3. ¿Cómo se manejan las ráfagas de E/S sin bloquear todo el sistema?
4. ¿Qué métricas son más importantes para evaluar rendimiento?

#### **Demo en vivo (MÁS COMPLETA):**
- Ejecutar `procesos_io.txt` con visualización completa:
  1. Mostrar diagrama de Gantt en tiempo real
  2. Tabla de procesos mostrando estados cambiantes
  3. Log con eventos: llegadas, bloqueos, E/S
  4. Métricas finales: tiempos, CPU%, fallos de página
- Comparar 3 algoritmos de planificación
- Comparar 3 algoritmos de memoria
- Explicar por qué ciertos algoritmos son mejores

---

## 🎯 COORDINACIÓN PARA LA EXPOSICIÓN

### **Orden recomendado (20 minutos totales):**

**1. INTRODUCCIÓN (1 min) - TODOS**
- Presentación del grupo
- Objetivos del simulador
- Tecnologías usadas (Java, Swing, Threads)

**2. ARQUITECTURA GENERAL (1 min) - Integrante 4**
- Diagrama de módulos
- Flujo de integración

**3. MÓDULO DE PLANIFICACIÓN (4 min) - Integrante 1**
- 3 algoritmos implementados
- Diagrama de Gantt
- Métricas de planificación

**4. MÓDULO DE MEMORIA (4 min) - Integrante 2**
- Paginación y tabla de páginas
- 3 algoritmos de reemplazo
- Comparación de fallos de página

**5. MÓDULO DE SINCRONIZACIÓN (4 min) - Integrante 3**
- Locks, Conditions, Semaphores
- Prevención de race conditions
- Coordinación planificador-memoria

**6. PROCESOS Y E/S (4 min) - Integrante 4**
- Threads y estados
- Manejo de E/S asíncrona
- Recolección de métricas

**7. DEMOSTRACIÓN EN VIVO (5 min) - Integrante 4 (con apoyo de todos)**
- Cargar procesos
- Ejecutar con diferentes algoritmos
- Mostrar resultados y análisis

**8. CONCLUSIONES (1 min) - TODOS**
- Logros del proyecto
- Lecciones aprendidas

---

## 📖 MATERIAL DE ESTUDIO COMÚN (TODOS DEBEN LEER)

### **Documentos principales:**
1. `README.md` - Visión general
2. `ANALISIS_COMPARATIVO.md` - Cumplimiento de requisitos
3. `INSTRUCCIONES_EJECUCION.md` - Cómo ejecutar
4. `GUIA_INFORME_IEEE.md` - Estructura del paper

### **Conceptos teóricos fundamentales:**
- Sistemas operativos: planificación, memoria, sincronización
- Programación concurrente con threads
- Algoritmos de planificación (FCFS, SJF, RR)
- Algoritmos de reemplazo de páginas (FIFO, LRU, Óptimo)
- Mecanismos de sincronización (locks, semaphores)

---

## ✅ CHECKLIST INDIVIDUAL DE PREPARACIÓN

Cada integrante debe:
- [ ] Leer y entender los archivos de su módulo
- [ ] Ejecutar el simulador y probar su módulo
- [ ] Crear diagramas visuales para su parte
- [ ] Preparar ejemplos y casos de uso
- [ ] Anticipar preguntas del profesor
- [ ] Cronometrar su exposición (4-5 min)
- [ ] Coordinar transiciones con otros integrantes
- [ ] Tener backup de screenshots/videos

---

## 🎤 CONSEJOS PARA LA EXPOSICIÓN

1. **Claridad:** Explicar como si el profesor no supiera programar
2. **Visual:** Mostrar código solo lo necesario, priorizar diagramas
3. **Práctica:** Ensayar en voz alta varias veces
4. **Coordinación:** Transiciones suaves entre integrantes
5. **Tiempo:** Respetar los 4-5 minutos asignados
6. **Seguridad:** Tener plan B si falla la demo en vivo

---

**¡ÉXITO EN LA EXPOSICIÓN! 🚀**
