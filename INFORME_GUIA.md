# Guía para el Informe Técnico (Formato IEEE)

## Estructura del Informe

### 1. RESUMEN (Abstract)
**Extensión**: 150-250 palabras

Incluir:
- Objetivo del simulador
- Metodología utilizada
- Algoritmos implementados
- Resultados principales obtenidos
- Conclusiones clave

**Ejemplo**:
```
Este trabajo presenta la implementación de un simulador integrado 
de sistema operativo que combina planificación de procesos y gestión 
de memoria virtual. Se implementaron tres algoritmos de planificación 
(FCFS, SJF, Round Robin) y tres algoritmos de reemplazo de páginas 
(FIFO, LRU, Óptimo). El simulador utiliza hilos de Java para 
representar procesos concurrentes y mecanismos de sincronización 
para coordinar los módulos. Los resultados experimentales demuestran 
que...
```

### 2. INTRODUCCIÓN
**Extensión**: 1-1.5 páginas

Incluir:
- Contexto y motivación
- Importancia de la planificación y memoria virtual
- Objetivos del trabajo
- Organización del documento

**Puntos a cubrir**:
- ¿Por qué es importante simular sistemas operativos?
- ¿Qué problema educativo resuelve?
- ¿Qué se busca demostrar?

### 3. REVISIÓN DE LA LITERATURA
**Extensión**: 1-2 páginas

Incluir:
- Fundamentos de planificación de CPU
  - FCFS, SJF, Round Robin
  - Métricas de desempeño
- Fundamentos de memoria virtual
  - Paginación
  - Algoritmos de reemplazo
- Trabajos relacionados
  - Otros simuladores educativos
  - Estudios comparativos

**Referencias sugeridas**:
- Silberschatz, A., Galvin, P. B., & Gagne, G. (2018). Operating System Concepts
- Tanenbaum, A. S. Modern Operating Systems
- Artículos sobre simuladores educativos de SO

### 4. METODOLOGÍA
**Extensión**: 2-3 páginas

#### 4.1 Diseño del Simulador

**Diagramas a incluir**:

1. **Diagrama de Arquitectura General**
```
┌─────────────────────────────────────────┐
│         Interfaz Gráfica (Swing)        │
└──────────────┬──────────────────────────┘
               │
┌──────────────▼──────────────────────────┐
│        Simulador Principal (Core)        │
└────┬─────────────┬──────────────────┬───┘
     │             │                  │
┌────▼────┐  ┌────▼────┐  ┌──────────▼──┐
│Scheduler│  │ Memory  │  │Sync Manager │
└─────────┘  └─────────┘  └─────────────┘
```

2. **Diagrama de Clases Principal**
- Process
- ProcessThread
- CPUScheduler (interfaz)
  - FCFS
  - SJF
  - RoundRobin
- MemoryManager
- PageReplacement (interfaz)
  - FIFO
  - LRU
  - Optimal

3. **Diagrama de Estados de Proceso**
```
NEW → READY ⇄ RUNNING → TERMINATED
       ↕        ↓
    BLOCKED ←──┘
```

4. **Diagrama de Flujo de Sincronización**
- Planificador solicita proceso
- MemoryManager verifica páginas
- Si no hay páginas → bloquear proceso
- Si hay páginas → ejecutar
- Thread del proceso se ejecuta

#### 4.2 Descripción de Algoritmos

**Para cada algoritmo, incluir**:
- Pseudocódigo
- Complejidad temporal
- Ventajas y desventajas

**Ejemplo - FCFS**:
```
Algorithm FCFS
Input: Queue of processes
Output: Scheduled process

1. Get first process from queue
2. Execute until completion
3. Remove from queue
4. Get next process
```

#### 4.3 Implementación de Sincronización

Describir:
- Uso de semáforos para CPU
- Locks para estructuras compartidas
- Conditions para notificaciones
- Prevención de deadlock

#### 4.4 Casos de Prueba

Diseñar casos para probar:
1. **Caso 1**: Procesos simples sin E/S
2. **Caso 2**: Procesos con E/S
3. **Caso 3**: Alta carga de memoria
4. **Caso 4**: Comparación de algoritmos

### 5. RESULTADOS
**Extensión**: 2-3 páginas

#### 5.1 Configuración Experimental

```
- Hardware: [Especificaciones]
- Software: Java 11, Maven 3.8
- Casos de prueba: 3 conjuntos de procesos
- Métricas medidas: Tiempo espera, turnaround, CPU util., fallos pág.
```

#### 5.2 Resultados por Algoritmo

**Tablas a incluir**:

**Tabla 1: Comparación de Algoritmos de Planificación**
```
| Algoritmo | Tiempo Espera | Tiempo Turnaround | CPU Util. |
|-----------|---------------|-------------------|-----------|
| FCFS      | 15.3 ms      | 28.7 ms          | 87.2%     |
| SJF       | 12.1 ms      | 24.5 ms          | 89.5%     |
| RR (q=3)  | 14.8 ms      | 27.3 ms          | 85.1%     |
```

**Tabla 2: Comparación de Algoritmos de Memoria**
```
| Algoritmo | Fallos Página | Reemplazos | Tiempo Acceso |
|-----------|---------------|------------|---------------|
| FIFO      | 45            | 38         | 125 ms        |
| LRU       | 38            | 31         | 118 ms        |
| Optimal   | 32            | 25         | 112 ms        |
```

#### 5.3 Gráficas

**Gráficas a incluir**:

1. **Gráfico de Gantt**: Secuencia de ejecución
2. **Gráfico de barras**: Comparación tiempos por algoritmo
3. **Gráfico de línea**: Evolución fallos de página
4. **Gráfico de pastel**: Utilización de recursos

#### 5.4 Análisis de Resultados

**Para cada resultado**:
- Interpretación
- Comparación con teoría
- Explicación de diferencias
- Implicaciones prácticas

**Ejemplo**:
```
Los resultados muestran que SJF tiene el mejor tiempo de espera 
promedio (12.1 ms), lo cual concuerda con la teoría que indica 
que SJF es óptimo para minimizar el tiempo de espera. Sin embargo, 
Round Robin proporciona mejor equidad entre procesos, como se 
observa en...
```

### 6. CONCLUSIONES
**Extensión**: 0.5-1 página

Incluir:
- Logros del proyecto
- Objetivos cumplidos
- Aprendizajes clave
- Limitaciones encontradas
- Trabajo futuro

**Estructura**:
```
1. Resumen de lo implementado
2. Validación de objetivos
3. Insights obtenidos
4. Limitaciones del simulador
5. Posibles extensiones
```

## Formato IEEE

### Configuración de Página
- **Columnas**: 2
- **Márgenes**: 
  - Superior/Inferior: 0.75"
  - Izquierda/Derecha: 0.625"
- **Fuente**: Times New Roman
  - Título: 24pt bold
  - Autores: 11pt
  - Cuerpo: 10pt
  - Referencias: 9pt
- **Espaciado**: Simple
- **Figuras/Tablas**: Centradas, numeradas

### Secciones Numeradas
```
I. INTRODUCCIÓN
II. REVISIÓN DE LA LITERATURA
III. METODOLOGÍA
   A. Diseño del Simulador
   B. Algoritmos Implementados
IV. RESULTADOS
   A. Configuración Experimental
   B. Análisis Comparativo
V. CONCLUSIONES
```

### Referencias (Estilo IEEE)
```
[1] A. Silberschatz, P. B. Galvin, and G. Gagne, Operating System 
    Concepts, 10th ed. Hoboken, NJ: Wiley, 2018.

[2] A. S. Tanenbaum and H. Bos, Modern Operating Systems, 4th ed. 
    Upper Saddle River, NJ: Pearson, 2014.

[3] W. Stallings, Operating Systems: Internals and Design Principles, 
    9th ed. Boston: Pearson, 2018.
```

## Checklist de Entregables

- [ ] Informe en PDF (formato IEEE, máx 12 páginas)
- [ ] Código fuente documentado
- [ ] Archivos de prueba (.txt)
- [ ] README.md con instrucciones
- [ ] Screenshots de la interfaz
- [ ] Resultados de pruebas
- [ ] Referencias bibliográficas

## Tips para el Informe

1. **Usa diagramas**: Una imagen vale más que mil palabras
2. **Tablas claras**: Datos organizados y fáciles de leer
3. **Código snippets**: Solo fragmentos relevantes
4. **Resultados reproducibles**: Documenta configuración exacta
5. **Análisis crítico**: No solo describir, también analizar
6. **Comparación teórica**: Relaciona resultados con teoría
7. **Honestidad**: Menciona limitaciones y problemas encontrados

## Estructura de Carpeta Final

```
proyecto-final/
├── informe/
│   ├── informe_final.pdf
│   ├── figuras/
│   │   ├── arquitectura.png
│   │   ├── diagrama_clases.png
│   │   └── graficas_resultados.png
│   └── latex/ (si aplica)
├── codigo/
│   └── simulador-sistemas-operativos/
├── pruebas/
│   ├── procesos.txt
│   ├── procesos_io.txt
│   └── resultados/
│       ├── fcfs_resultados.txt
│       ├── sjf_resultados.txt
│       └── rr_resultados.txt
└── README.md
```

## Recursos Adicionales

- **Plantilla IEEE LaTeX**: https://www.ieee.org/conferences/publishing/templates.html
- **Plantilla IEEE Word**: https://www.ieee.org/content/dam/ieee-org/ieee/web/org/conferences/conference-template-a4.docx
- **Guía de estilo IEEE**: https://journals.ieeeauthorcenter.ieee.org/

## Calificación Esperada

| Componente | Peso |
|------------|------|
| Informe técnico | 40% |
| Código funcional | 30% |
| Análisis de resultados | 20% |
| Presentación/Demo | 10% |
| **Puntos extra E/S** | +2% |
