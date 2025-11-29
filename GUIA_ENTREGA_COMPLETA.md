# GUÍA COMPLETA DE ENTREGA
## Trabajo Final - Sistemas Operativos

---

## 📅 INFORMACIÓN DE ENTREGA

**Fecha límite:** 03/12/2025 a las 12:00 m. (mediodía)
**Método:** Solo UN miembro del grupo sube todos los entregables
**Plataforma:** [Especificar plataforma de entrega - Moodle/Canvas/etc]

---

## 📦 ENTREGABLES REQUERIDOS

### **RESUMEN:**
1. ✅ Informe Técnico (PDF formato IEEE)
2. ✅ Código Fuente (.zip)
3. ✅ Archivos de Prueba (.zip)
4. ✅ Librerías/Dependencias (.zip o documento)
5. ✅ Presentación (.pdf) - para exposición

---

## 1️⃣ INFORME TÉCNICO (.pdf)

### **Preparación:**

**Paso 1: Escribir el contenido**
- Usa la guía `GUIA_INFORME_IEEE.md` como referencia
- Formato IEEE de doble columna
- Máximo 7 páginas de texto + 5 de figuras/referencias = 12 páginas totales

**Paso 2: Crear figuras**
```
Figuras necesarias (mínimo 8):
1. Diagrama de arquitectura del sistema
2. Diagrama de Gantt comparativo (FCFS/SJF/RR)
3. Visualización de memoria (marcos y páginas)
4. Diagrama de secuencia (sincronización)
5. Diagrama de estados de procesos
6. Gráfica de tiempos (Espera/Retorno/Respuesta)
7. Gráfica de fallos de página
8. Screenshot de la GUI funcionando
```

**Herramientas recomendadas:**
- Diagramas: draw.io, Lucidchart, PowerPoint
- Gráficas: Excel, Google Sheets, matplotlib (Python)
- Screenshots: Captura de pantalla del simulador funcionando

**Paso 3: Crear tablas**
```
Tablas necesarias (mínimo 5):
1. Módulos del sistema y responsabilidades
2. Transiciones de estados de procesos
3. Resultados de planificación (FCFS vs SJF vs RR)
4. Resultados de memoria (FIFO vs LRU vs Óptimo)
5. Impacto de E/S en el rendimiento
```

**Paso 4: Generar PDF**
- Exportar desde Word/LaTeX/Overleaf
- Verificar formato de doble columna
- Comprobar que todas las figuras sean legibles
- Verificar numeración de secciones, figuras y tablas

### **Estructura de archivos:**
```
Informe_GrupoX_SistemasOperativos.pdf
└── Contenido:
    - Portada con nombres de integrantes
    - Resumen
    - Introducción
    - Revisión de literatura
    - Metodología (diseño + algoritmos)
    - Resultados (casos de prueba + análisis)
    - Conclusiones
    - Referencias (formato IEEE)
```

### **Checklist:**
- [ ] Portada con nombres completos de integrantes
- [ ] Universidad, curso, fecha
- [ ] Resumen en español (150-250 palabras)
- [ ] 6 secciones completas
- [ ] Mínimo 8 figuras numeradas con caption
- [ ] Mínimo 5 tablas numeradas con caption
- [ ] Mínimo 8 referencias formato IEEE
- [ ] Sin errores ortográficos
- [ ] Máximo 12 páginas totales
- [ ] Formato PDF (no .doc o .docx)

---

## 2️⃣ CÓDIGO FUENTE (.zip)

### **Preparación:**

**Paso 1: Limpiar el proyecto**
```bash
# Eliminar archivos compilados y temporales
rm -rf target/
rm -rf .idea/
rm -rf .vscode/
rm -rf *.class
rm -rf .DS_Store
```

**Paso 2: Verificar estructura**
```
simulador-sistemas-operativos/
├── src/
│   └── main/
│       └── java/
│           └── simulador/
│               ├── core/
│               ├── scheduler/
│               ├── memory/
│               ├── process/
│               ├── sync/
│               ├── io/
│               ├── metrics/
│               ├── utils/
│               └── gui/
├── procesos.txt
├── procesos_io.txt
├── procesos_comparacion.txt
├── config.txt
├── README.md
├── INSTRUCCIONES_EJECUCION.md
├── DEPENDENCIAS.md
├── pom.xml
└── .gitignore
```

**Paso 3: Documentar el código**

Verificar que cada clase tiene JavaDoc:
```java
/**
 * Gestor de memoria virtual que simula paginación.
 * Implementa tabla de páginas por proceso y algoritmos
 * de reemplazo (FIFO, LRU, Óptimo).
 * 
 * @author Equipo X
 * @version 1.0
 * @since 2025-12-03
 */
public class MemoryManager {
    /**
     * Carga n páginas del proceso en memoria.
     * 
     * @param process Proceso que requiere páginas
     * @param numPages Cantidad de páginas a cargar
     * @return true si se cargaron exitosamente, false si falta espacio
     */
    public boolean loadPages(Process process, int numPages) {
        // ...
    }
}
```

**Paso 4: Crear el archivo ZIP**

```bash
# Opción 1: Desde línea de comandos
cd ..
zip -r SimuladorSO_GrupoX.zip simulador-sistemas-operativos/ \
    -x "*/target/*" -x "*/.idea/*" -x "*/.vscode/*" -x "*.class" -x "*.DS_Store"

# Opción 2: Usar interfaz gráfica
# - Click derecho en la carpeta del proyecto
# - "Comprimir" o "Crear archivo ZIP"
```

**Nombre del archivo:**
```
SimuladorSO_GrupoX_CodigoFuente.zip
(donde X = número de grupo)
```

### **Checklist:**
- [ ] Todos los archivos .java incluidos
- [ ] Código documentado con JavaDoc
- [ ] README.md con instrucciones
- [ ] INSTRUCCIONES_EJECUCION.md incluido
- [ ] pom.xml incluido
- [ ] Sin carpetas target/, .idea/, .vscode/
- [ ] Sin archivos .class
- [ ] Tamaño razonable (< 5 MB)

---

## 3️⃣ ARCHIVOS DE ENTRADA (.zip)

### **Preparación:**

**Archivos de prueba ya creados:**
```
archivos_prueba/
├── procesos.txt                    ← Caso básico sin E/S
├── procesos_io.txt                 ← Con ráfagas de E/S
├── procesos_comparacion.txt        ← Para comparar algoritmos
├── config.txt                      ← Configuración (opcional)
└── README_ARCHIVOS.txt             ← Explicación de cada archivo
```

**Crear README_ARCHIVOS.txt:**
```
# ARCHIVOS DE PRUEBA
## Simulador de Sistemas Operativos

## procesos.txt - Caso de Prueba 1
Descripción: Procesos simples sin operaciones de E/S
Objetivo: Comparar algoritmos de planificación (FCFS, SJF, RR)
Procesos: 3 (P1, P2, P3)
Configuración recomendada:
- Marcos de memoria: 10
- Algoritmo planificación: Probar los 3
- Algoritmo memoria: LRU

## procesos_io.txt - Caso de Prueba 2
Descripción: Procesos con ráfagas alternadas de CPU y E/S
Objetivo: Validar manejo de E/S asíncrona (+2 puntos extra)
Procesos: 4 (P1, P2, P3, P4)
Características especiales:
- P1 tiene múltiples ráfagas de E/S
- Demuestra que E/S no bloquea otros procesos
Configuración recomendada:
- Marcos de memoria: 10
- Algoritmo planificación: Round Robin (q=3)
- Algoritmo memoria: LRU

## procesos_comparacion.txt - Caso de Prueba 3
Descripción: Caso de estrés para memoria
Objetivo: Comparar algoritmos de reemplazo de páginas
Procesos: 5 (P1-P5)
Características especiales:
- Total 24 páginas, solo 10 marcos disponibles
- Fuerza múltiples reemplazos de página
Configuración recomendada:
- Marcos de memoria: 10 (probar también con 15)
- Algoritmo planificación: FCFS
- Algoritmo memoria: Probar FIFO, LRU, Óptimo

## config.txt - Configuración por Defecto
Descripción: Archivo de configuración opcional
Parámetros:
- FRAMES=10: Marcos de memoria
- SCHEDULER=RR: Algoritmo de planificación
- QUANTUM=3: Quantum para Round Robin
- MEMORY_ALGORITHM=LRU: Algoritmo de reemplazo

## FORMATO DE ARCHIVOS
PID tiempo_llegada ráfagas prioridad páginas

Ejemplo:
P1 0 CPU(4),E/S(3),CPU(5) 1 4
│  │ │                   │ │
│  │ │                   │ └─ Páginas requeridas
│  │ │                   └─── Prioridad
│  │ └─────────────────────── Ráfagas (CPU y/o E/S)
│  └─────────────────────────── Tiempo de llegada
└────────────────────────────── Identificador

## CÓMO USAR
1. Cargar archivo en el simulador con botón [Load]
2. Configurar parámetros (algoritmos, quantum, marcos)
3. Presionar [Start]
4. Observar ejecución y métricas
```

**Crear el ZIP:**
```bash
zip -r SimuladorSO_GrupoX_ArchivosPrueba.zip \
    procesos.txt \
    procesos_io.txt \
    procesos_comparacion.txt \
    config.txt \
    README_ARCHIVOS.txt
```

### **Checklist:**
- [ ] Mínimo 3 archivos de prueba diferentes
- [ ] Formato correcto (verificado con FileParser)
- [ ] README explicando cada archivo
- [ ] Casos cubren diferentes escenarios
- [ ] Nombres descriptivos

---

## 4️⃣ LIBRERÍAS/DEPENDENCIAS

### **Preparación:**

Como el proyecto NO usa librerías externas, entregar documento explicativo:

**Opción 1: Documento PDF**

Crear `Dependencias_GrupoX.pdf` con:
```
# DEPENDENCIAS DEL PROYECTO

## RESUMEN EJECUTIVO
Este proyecto NO requiere librerías externas.
Solo utiliza Java Standard Library (JDK 11+).

## REQUISITO ÚNICO
- Java Development Kit (JDK) 11 o superior

## INSTALACIÓN DE JDK
[Instrucciones para Windows/Mac/Linux]

## VERIFICACIÓN
$ java -version
java version "11.0.x"

$ javac -version
javac 11.0.x

## CONCLUSIÓN
✅ Sin dependencias externas
✅ Sin Maven necesario
✅ Solo JDK 11+ requerido
```

**Opción 2: Incluir DEPENDENCIAS.md en el ZIP**

Ya tienes el archivo `DEPENDENCIAS.md` creado, simplemente:
```bash
zip SimuladorSO_GrupoX_Dependencias.zip DEPENDENCIAS.md
```

### **Checklist:**
- [ ] Documento claro sobre requisitos
- [ ] Instrucciones de instalación de JDK
- [ ] Explicación de que NO hay librerías externas
- [ ] Verificación de versión de Java

---

## 5️⃣ PRESENTACIÓN (.pdf)

### **Preparación:**

**Estructura de la presentación (máximo 20 minutos):**

```
Diapositivas recomendadas:

1. PORTADA (1 slide)
   - Título del proyecto
   - Nombres de integrantes
   - Universidad, curso, fecha

2. INTRODUCCIÓN (2 slides)
   - Objetivos del proyecto
   - Alcance del simulador
   - Tecnologías utilizadas

3. ARQUITECTURA (3 slides)
   - Diagrama de componentes
   - Módulos principales
   - Flujo de ejecución

4. ALGORITMOS DE PLANIFICACIÓN (3 slides)
   - FCFS, SJF, Round Robin
   - Diagramas de Gantt
   - Comparación de resultados

5. GESTIÓN DE MEMORIA (3 slides)
   - Paginación y tabla de páginas
   - FIFO, LRU, Óptimo
   - Comparación de fallos de página

6. SINCRONIZACIÓN (2 slides)
   - Problema de race conditions
   - Solución con locks/semáforos
   - Diagrama de secuencia

7. DEMOSTRACIÓN (4 slides)
   - Screenshots de la GUI
   - Casos de prueba
   - Métricas obtenidas
   - Video corto (si aplica)

8. RESULTADOS (2 slides)
   - Tablas comparativas
   - Gráficas de rendimiento
   - Análisis de resultados

9. CONCLUSIONES (1 slide)
   - Logros del proyecto
   - Lecciones aprendidas
   - Trabajo futuro

10. PREGUNTAS (1 slide)
    - Slide final para preguntas

Total: ~22 slides para 20 minutos
```

**Herramientas:**
- PowerPoint
- Google Slides
- Keynote
- Canva

**Consejos:**
- Más imágenes, menos texto
- Fuente grande (mínimo 24pt)
- Colores contrastantes
- Animaciones sutiles (no excesivas)
- Incluir logos de la universidad

### **Checklist:**
- [ ] Máximo 25 slides
- [ ] Nombres de todos los integrantes
- [ ] Diagramas visuales claros
- [ ] Screenshots del simulador funcionando
- [ ] Gráficas de resultados
- [ ] Conclusiones claras
- [ ] Formato PDF (no .ppt)

---

## 📤 SUBIDA DE ARCHIVOS

### **Estructura final de entrega:**

```
📁 ENTREGA_FINAL_GRUPO_X/
│
├── 📄 Informe_GrupoX_SistemasOperativos.pdf        (Informe técnico)
│
├── 📦 SimuladorSO_GrupoX_CodigoFuente.zip         (Código fuente)
│
├── 📦 SimuladorSO_GrupoX_ArchivosPrueba.zip       (Archivos .txt)
│
├── 📦 SimuladorSO_GrupoX_Dependencias.zip         (Documento de librerías)
│    └── DEPENDENCIAS.md
│
└── 📄 Presentacion_GrupoX_SistemasOperativos.pdf  (Slides)
```

### **Nombres de archivo:**
- Usar formato: `TipoArchivo_GrupoX_NombreDescriptivo.extension`
- Reemplazar "X" con número de grupo
- Sin espacios (usar guión bajo _)
- Sin caracteres especiales (ñ, acentos, etc.)

### **Proceso de subida:**

**Día de entrega (03/12/2025):**

1. **08:00 AM** - Revisión final de todos los archivos
2. **09:00 AM** - Prueba de compilación y ejecución
3. **10:00 AM** - Generar PDFs finales
4. **10:30 AM** - Crear archivos ZIP
5. **11:00 AM** - Subir a plataforma
6. **11:30 AM** - Verificar que se subieron correctamente
7. **12:00 M** - **DEADLINE** ⏰

**Solo UN integrante sube:**
- Designar responsable de subida
- Tener todos los archivos listos
- Verificar conexión a internet estable
- Guardar comprobante de entrega

---

## ✅ CHECKLIST FINAL DE ENTREGA

### **Antes de subir:**
- [ ] Informe PDF completo (máx 12 páginas)
- [ ] Código fuente ZIP (sin target/, .idea/)
- [ ] Archivos de prueba ZIP (mínimo 3 .txt)
- [ ] Dependencias ZIP o PDF
- [ ] Presentación PDF (máx 25 slides)
- [ ] Todos los archivos nombrados correctamente
- [ ] Tamaño total razonable (< 50 MB)
- [ ] Probado que el código compila y ejecuta
- [ ] Revisado por todos los integrantes

### **Después de subir:**
- [ ] Guardar comprobante de entrega
- [ ] Tomar screenshot de confirmación
- [ ] Enviar copia a todos los integrantes
- [ ] Preparar para la exposición (04-10 Dic)

---

## 🎤 PREPARACIÓN PARA EXPOSICIÓN

**Fechas: 04 al 10 de diciembre**
**Duración: Máximo 20 minutos**
**Distribución recomendada:**

```
Integrante 1 (4 min): Introducción + Arquitectura
Integrante 2 (4 min): Planificación de CPU
Integrante 3 (4 min): Gestión de Memoria
Integrante 4 (4 min): Sincronización + E/S
Integrante 5 (4 min): Demostración en vivo + Conclusiones
```

**Recomendaciones:**
- Ensayar al menos 3 veces
- Cronometrar cada sección
- Preparar respuestas a preguntas comunes
- Tener el simulador listo para demo en vivo
- Backup: video de demostración por si falla en vivo

---

## 🆘 CONTACTO DE EMERGENCIA

Si hay problemas el día de entrega:
- Email del profesor: [email]
- WhatsApp del delegado: [número]
- Plataforma de soporte: [link]

---

## 🎯 CRITERIOS DE EVALUACIÓN RECORDATORIO

| Componente | Peso |
|------------|------|
| Desarrollo técnico del simulador | 70% |
| Exposición grupal | 20% |
| Coevaluación (trabajo en equipo) | 10% |

**Enfocarse en:**
- ✅ Funcionamiento correcto del simulador
- ✅ Calidad del código y documentación
- ✅ Análisis comparativo de resultados
- ✅ Claridad en la exposición
- ✅ Demostración práctica

---

**¡ÉXITO EN LA ENTREGA! 🚀**
