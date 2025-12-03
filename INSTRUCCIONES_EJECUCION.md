# INSTRUCCIONES DE EJECUCIÓN
## Simulador de Sistemas Operativos

---

## 📋 REQUISITOS DEL SISTEMA

### **Software Necesario:**
- **Java:** JDK 11 o superior
- **Sistema Operativo:** Windows, macOS, o Linux
- **RAM:** Mínimo 512 MB
- **Espacio en disco:** 50 MB

---

## 🚀 CÓMO EJECUTAR EL SIMULADOR

### **Opción 1: Ejecutar desde línea de comandos (Recomendado)**

```bash
# 1. Navegar al directorio del proyecto
cd simulador-sistemas-operativos

# 2. Compilar el proyecto (si no está compilado)
javac -d target/classes -sourcepath src/main/java $(find src/main/java -name "*.java")

# 3. Ejecutar el simulador
java -cp target/classes simulador.gui.MainWindow
```

### **Opción 2: Desde IDE (Eclipse, IntelliJ, VSCode)**

1. Abrir el proyecto en tu IDE favorito
2. Localizar la clase `simulador.gui.MainWindow`
3. Ejecutar el método `main()`
4. La interfaz gráfica se abrirá automáticamente

### **Opción 3: Modo headless (sin interfaz gráfica)**

Ideal para generar métricas rápidamente desde consola. Utiliza la clase `simulador.core.HeadlessRunner`.

```bash
# Compilar si aún no lo hiciste
mvn -q clean compile

# Ejecutar la simulación (ejemplo con FCFS y costo de contexto 1)
java -cp target/classes simulador.core.HeadlessRunner FCFS procesos.txt --cs 1

# También puedes usar Maven directamente
mvn exec:java -Dexec.mainClass="simulador.core.HeadlessRunner" -Dexec.args="RR 4 procesos_io.txt --cs 2"
```

---

## 📖 USO DEL SIMULADOR

### **Paso 1: Cargar Procesos**
1. Pulsar el botón **Cargar Procesos**
2. Seleccionar archivo de procesos (`.txt`)
3. Archivos de ejemplo incluidos:
   - `procesos.txt` - Caso básico sin E/S
   - `procesos_io.txt` - Con ráfagas de E/S
   - `procesos_comparacion.txt` - Para comparar algoritmos

### **Paso 2: Configurar Simulación**
1. Seleccionar algoritmo de planificación con el combo **Algoritmo de Planificación**:
   - FCFS (First Come First Served)
   - SJF (Shortest Job First)
   - Round Robin (especificar quantum)

2. Seleccionar algoritmo de memoria en **Algoritmo de Memoria**:
   - FIFO (First In First Out)
   - LRU (Least Recently Used)
   - Óptimo (teórico)

3. Configurar parámetros adicionales:
   - **Marcos de Memoria:** cantidad de marcos físicos disponibles (ej: 10).
   - **Quantum:** solo habilitado para Round Robin (ej: 4).
   - **Costo cambio de contexto:** unidades de tiempo que la CPU permanece ocupada durante un cambio de proceso (ej: 1).

### **Paso 3: Ejecutar Simulación**
1. Pulsar **Iniciar Simulación**.
2. Observar la ejecución en tiempo real:
   - **Diagrama de Gantt:** ahora contiene dos pistas (CPU y E/S) y muestra los cambios de contexto (segmentos grises). Si el timeline es más largo que la ventana, aparece una barra horizontal para desplazarse.
   - **Panel de Memoria:** refleja los marcos ocupados con PID y número de página, actualizados en vivo.
   - **Cola de Procesos:** lista todos los procesos con su estado actual (Nuevo, Listo, Ejecutando, Bloqueado, Terminado).
   - **Log de Eventos y Ráfagas de E/S:** registra las transiciones relevantes para auditoría o análisis posterior.

### **Paso 4: Ver Resultados**
1. Al finalizar automáticamente, el panel **Métricas de Desempeño** muestra:
   - Tiempo de espera, retorno y respuesta por proceso (en milisegundos).
   - Promedios y utilización de CPU considerando tiempo de cambio de contexto.
   - Fallos y reemplazos de página acumulados.
   - Resumen textual del Gantt (CPU y E/S) para documentación.

2. El botón **Detener** permite cortar una simulación en curso. Tras finalizar, usa **Reiniciar** para limpiar la interfaz y preparar nuevos parámetros.

---

## 📂 FORMATO DE ARCHIVOS DE ENTRADA

### **Estructura del archivo `.txt`:**

```
# Comentarios (líneas que empiezan con #)
PID tiempo_llegada ráfagas prioridad páginas

# Ejemplo:
P1 0 CPU(5) 1 3
P2 2 CPU(3) 2 2
P3 4 CPU(8) 3 4
```

### **Campos:**
1. **PID:** Identificador único del proceso (ej: P1, P2, P3)
2. **tiempo_llegada:** Momento en que llega al sistema (número entero)
3. **ráfagas:** Secuencia de operaciones separadas por coma:
   - `CPU(n)` - Ráfaga de CPU de duración n
   - `E/S(n)` - Ráfaga de E/S de duración n
   - Ejemplo: `CPU(4),E/S(3),CPU(5)` - CPU, luego E/S, luego CPU
4. **prioridad:** Número entero (1=alta, números mayores=baja prioridad)
5. **páginas:** Cantidad de páginas que requiere en memoria

### **Ejemplos:**

**Proceso simple (solo CPU):**
```
P1 0 CPU(10) 1 5
```

**Proceso con E/S:**
```
P2 2 CPU(4),E/S(3),CPU(5) 2 4
```

**Múltiples ráfagas:**
```
P3 5 CPU(2),E/S(1),CPU(3),E/S(2),CPU(4) 1 6
```

---

## 🔧 SOLUCIÓN DE PROBLEMAS

### **Error: "Error: Could not find or load main class"**
**Solución:**
```bash
# Asegúrate de estar en el directorio correcto
cd simulador-sistemas-operativos

# Recompila el proyecto
find src/main/java -name "*.java" -print0 | xargs -0 javac -d target/classes -sourcepath src/main/java

# Ejecuta con classpath correcto
java -cp target/classes simulador.gui.MainWindow
```

### **Error: "ClassNotFoundException"**
**Solución:** Verifica que la carpeta `target/classes` contenga los archivos compilados:
```bash
ls target/classes/simulador/gui/MainWindow.class
```

### **Error: "UnsupportedClassVersionError"**
**Solución:** Tu versión de Java es anterior a Java 11. Actualiza Java:
```bash
# Verificar versión
java -version

# Debe mostrar: java version "11" o superior
```

### **La ventana no se abre**
**Solución:**
1. Verifica que el sistema soporte GUI (X11 en Linux)
2. En sistemas sin GUI, usa modo consola (requiere modificación)

---

## 📞 SOPORTE

Para problemas o consultas:
- Email: [tu-email@universidad.edu]
- GitHub Issues: https://github.com/Piero-design/simulador-sistemas-operativos/issues

---

## 👥 AUTORES

[Nombre del Equipo]
- Integrante 1
- Integrante 2
- Integrante 3
- Integrante 4
- Integrante 5

Universidad: [Nombre]
Curso: Sistemas Operativos
Fecha: Diciembre 2025

---

## 📄 LICENCIA

Este proyecto es con fines educativos.
