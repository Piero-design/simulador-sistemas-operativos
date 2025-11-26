# 🎤 DIVISIÓN DE LA EXPOSICIÓN
## Simulador de Sistemas Operativos

**Duración Total:** 20 minutos  
**Equipo:** 3-4 integrantes  
**Fecha:** 4-10 de Diciembre, 2025

---

## 👥 DIVISIÓN POR INTEGRANTE

### 🎯 **INTEGRANTE 1: INTRODUCCIÓN + PLANIFICACIÓN DE CPU** (5 min)

#### Responsabilidades del Código:
- `src/main/java/simulador/scheduler/`
- `CPUScheduler.java`, `FCFS.java`, `SJF.java`, `RoundRobin.java`
- `SchedulerEngine.java`

#### Exposición:
1. **Introducción (1 min)**
   - Presentar el proyecto
   - Objetivos del simulador
   - Tecnologías: Java 21, Swing, Maven

2. **Arquitectura General (1 min)**
   - Mostrar diagrama de componentes
   - Explicar flujo de ejecución
   - Interacción entre módulos

3. **Planificación de CPU (3 min)**
   - Demo de algoritmos: FCFS, SJF, Round Robin
   - Mostrar Diagrama de Gantt
   - Explicar cambios de contexto
   - **Mostrar código:** Método `getNextProcess()` de cada algoritmo
   - Métricas: Tiempo de espera, turnaround, respuesta

#### Demo en Vivo:
```bash
./run.sh  # Ejecutar con Round Robin
```
- Señalar procesos en el Gantt
- Explicar quantum y cambios de contexto

---

### 🎯 **INTEGRANTE 2: GESTIÓN DE MEMORIA VIRTUAL** (5 min)

#### Responsabilidades del Código:
- `src/main/java/simulador/memory/`
- `MemoryManager.java`, `PageReplacement.java`
- `FIFO.java`, `LRU.java`, `Optimal.java`

#### Exposición:
1. **Conceptos de Memoria (1.5 min)**
   - Paginación y marcos de memoria
   - Page faults y reemplazos
   - Tabla de páginas por proceso

2. **Algoritmos de Reemplazo (2.5 min)**
   - **FIFO:** Primera página en entrar, primera en salir
   - **LRU:** Least Recently Used (cola temporal)
   - **Optimal:** Con referencias futuras
   - **Mostrar código:** Método `selectVictim()` de cada algoritmo

3. **Visualización (1 min)**
   - Panel de memoria con marcos
   - Verde = libre, Rojo = ocupado
   - Contador de fallos y reemplazos

#### Demo en Vivo:
- Editar `SimuladorMain.java`: cambiar a `procesos_caso2.txt` (alta demanda)
- Cambiar algoritmo: `"FIFO"` → `"LRU"` → `"Optimal"`
- Comparar número de fallos de página

---

### 🎯 **INTEGRANTE 3: SINCRONIZACIÓN Y E/S** (5 min)

#### Responsabilidades del Código:
- `src/main/java/simulador/sync/`
- `ProcessSync.java`, `IOManager.java`
- `src/main/java/simulador/process/Process.java`

#### Exposición:
1. **Estados de Procesos (1.5 min)**
   - Modelo de 6 estados: NEW, READY, RUNNING, BLOCKED_MEM, BLOCKED_IO, TERMINATED
   - Transiciones entre estados
   - **Mostrar código:** Enum `State` en `Process.java`

2. **Sincronización (2 min)**
   - Semáforos para CPU disponible
   - Locks para cola de listos
   - Condiciones para espera/notificación
   - **Mostrar código:** `ProcessSync.java` con semáforos

3. **Operaciones de E/S (1.5 min)**
   - E/S asíncrona con callbacks
   - `IOManager` usando `CompletableFuture`
   - Desbloqueo automático al completar
   - **Mostrar código:** Método `startIO()` en `IOManager.java`

#### Demo en Vivo:
- Mostrar panel de colas
- Señalar proceso moviéndose: LISTO → EJECUTANDO → BLOQUEADO → LISTO
- Mostrar log: eventos de E/S

---

### 🎯 **INTEGRANTE 4: INTERFAZ GRÁFICA Y MÉTRICAS** (5 min)

#### Responsabilidades del Código:
- `src/main/java/simulador/gui/`
- `MainWindow.java`, `GanttPanel.java`, `MemoryPanel.java`, `ProcessQueuesPanel.java`
- `src/main/java/simulador/utils/Metrics.java`

#### Exposición:
1. **Interfaz Gráfica (2 min)**
   - Java Swing con 4 paneles
   - Actualización en tiempo real con callbacks
   - **Mostrar código:** `GanttPanel.paintComponent()`
   - Botones de control funcionales

2. **Métricas del Sistema (2 min)**
   - Tiempo de espera promedio
   - Tiempo de retorno (turnaround)
   - Tiempo de respuesta
   - Utilización de CPU
   - Tasa de fallos de página
   - **Mostrar código:** `Metrics.generateReport()`

3. **Resultados y Comparación (1 min)**
   - Tabla comparativa de algoritmos
   - ¿Cuál es mejor y por qué?
   - Trade-offs entre algoritmos

#### Demo en Vivo:
- Ejecutar simulación completa
- Mostrar reporte final de métricas
- Comparar 2 configuraciones en tiempo real

---

## 📊 ESTRUCTURA DE DIAPOSITIVAS (15 slides max)

### Slide 1: Portada
- Título del proyecto
- Nombres del equipo
- Fecha

### Slides 2-3: Introducción (Integrante 1)
- Objetivos
- Arquitectura del sistema

### Slides 4-6: Planificación de CPU (Integrante 1)
- Algoritmos FCFS, SJF, RR
- Diagrama de Gantt
- Código ejemplo

### Slides 7-9: Gestión de Memoria (Integrante 2)
- Paginación y marcos
- Algoritmos FIFO, LRU, Optimal
- Visualización de memoria

### Slides 10-11: Sincronización y E/S (Integrante 3)
- Estados de procesos
- Semáforos y locks
- E/S asíncrona

### Slides 12-13: GUI y Métricas (Integrante 4)
- Paneles de la interfaz
- Métricas calculadas
- Tabla comparativa

### Slide 14: Conclusiones (Todos)
- ¿Qué aprendimos?
- Dificultades encontradas
- Posibles mejoras

### Slide 15: Preguntas
- Q&A

---

## 🎬 SCRIPT DE DEMO (5 minutos integrados)

### Demo Completa (todos participan):

**Integrante 1:** "Ahora vamos a ejecutar el simulador..."
```bash
./run.sh
```

**Integrante 1:** "Aquí vemos el Diagrama de Gantt con Round Robin..."
- Señala los rectángulos de colores
- Explica quantum de 2 unidades

**Integrante 2:** "Observen el panel de memoria a la derecha..."
- Señala marcos verdes y rojos
- Menciona algoritmo LRU activo

**Integrante 3:** "En las colas vemos procesos moviéndose..."
- Señala P4 en EJECUTANDO
- Señala P2 en BLOQUEADOS (E/S)

**Integrante 4:** "Y aquí el log muestra todos los eventos..."
- Scroll al final
- Mostrar reporte de métricas final

**Todos:** "Como pueden ver, los 5 procesos completaron en 10 unidades de tiempo..."

---

## 📝 PREPARACIÓN INDIVIDUAL

### Cada integrante debe:

1. **Estudiar su módulo del código**
   - Leer y entender las clases asignadas
   - Poder explicar al menos 2 métodos clave
   - Preparar 1-2 slides con código

2. **Preparar casos de prueba**
   - Integrante 1: Cambiar algoritmos CPU
   - Integrante 2: Cambiar algoritmos memoria
   - Integrante 3: Mostrar diferentes estados
   - Integrante 4: Exportar métricas

3. **Practicar transiciones**
   - Último minuto del Integrante X debe conectar con primero del X+1
   - "Ahora mi compañero/a [nombre] explicará la memoria..."

4. **Responder preguntas comunes**
   - ¿Por qué eligieron Java Swing?
   - ¿Cómo manejan la concurrencia?
   - ¿Qué algoritmo es más eficiente?

---

## ❓ PREGUNTAS FRECUENTES (preparar respuestas)

### Para Integrante 1 (CPU):
- ¿Qué pasa si quantum es muy grande o muy pequeño?
- ¿Por qué SJF puede causar starvation?
- ¿Cómo calculan el burst time en SJF?

### Para Integrante 2 (Memoria):
- ¿Cómo implementan el algoritmo Optimal sin futuro real?
- ¿Qué pasa si no hay marcos libres?
- ¿Por qué LRU es mejor que FIFO?

### Para Integrante 3 (Sync):
- ¿Cómo evitan race conditions?
- ¿Qué pasa si dos procesos usan E/S simultáneamente?
- ¿Por qué usan CompletableFuture?

### Para Integrante 4 (GUI):
- ¿Por qué no usaron JavaFX?
- ¿Cómo actualizan la GUI sin bloquear?
- ¿Qué métricas son más importantes?

---

## ✅ CHECKLIST PRE-EXPOSICIÓN

### 1 Semana Antes:
- [ ] Dividir responsabilidades según este documento
- [ ] Cada uno estudia su módulo asignado
- [ ] Crear diapositivas individuales (3-4 por integrante)

### 3 Días Antes:
- [ ] Reunión: Ensayo completo de la presentación
- [ ] Integrar diapositivas en presentación única
- [ ] Cronometrar: asegurar 20 minutos totales
- [ ] Probar demo en al menos 2 computadoras diferentes

### 1 Día Antes:
- [ ] Verificar que el proyecto compile en todas las máquinas
- [ ] Tener backup del proyecto en USB
- [ ] Preparar 2-3 configuraciones de prueba diferentes
- [ ] Ensayar respuestas a preguntas frecuentes

### Día de la Exposición:
- [ ] Llegar 10 minutos antes
- [ ] Probar proyector/pantalla
- [ ] Tener `./run.sh` listo
- [ ] Agua para todos
- [ ] ¡Respirar y confiar en el equipo!

---

## 🎯 CRITERIOS DE EVALUACIÓN (20% de la nota)

### Aspectos a cubrir:

1. **Claridad en la explicación (5 pts)**
   - Todos hablan claro y se entienden
   - Uso correcto de términos técnicos

2. **Dominio del código (5 pts)**
   - Pueden responder preguntas sobre su módulo
   - Explican la lógica, no solo leen

3. **Demo funcional (5 pts)**
   - El simulador funciona sin errores
   - Se muestran diferentes configuraciones

4. **Coordinación del equipo (5 pts)**
   - Transiciones suaves entre integrantes
   - Todos participan equitativamente
   - Se apoyan mutuamente

---

## 💡 TIPS FINALES

### Para destacar en la exposición:

1. **No lean las diapositivas** - Hablen naturalmente
2. **Usen puntero o mouse** - Señalen elementos específicos
3. **Hagan contacto visual** - Con el profesor y compañeros
4. **Muestren entusiasmo** - Es TU proyecto, siéntete orgulloso
5. **Si algo falla** - Tengan plan B (capturas de pantalla)
6. **Practiquen juntos** - Al menos 2 veces completo
7. **Respiren** - Si se ponen nerviosos, pausa de 2 segundos

### Plan B si algo falla:
- Demo no inicia → Usar capturas ya preparadas
- Se cuelga → Reiniciar mientras otro habla
- Pregunta difícil → "Excelente pregunta, déjame consultarlo con mi equipo..."

---

## 🏆 ¡ÉXITO EN LA EXPOSICIÓN!

**Recuerden:** Ustedes conocen el código mejor que nadie. Confíen en su trabajo y muéstrenlo con orgullo. 🚀

---

**Última Actualización:** 25 de Noviembre, 2025  
**Preparado por:** GitHub Copilot para el equipo Piero
