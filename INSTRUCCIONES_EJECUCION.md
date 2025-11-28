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
javac -d target/classes -sourcepath src/main/java src/main/java/simulador/**/*.java

# 3. Ejecutar el simulador
java -cp target/classes simulador.gui.MainWindow
```

### **Opción 2: Desde IDE (Eclipse, IntelliJ, VSCode)**

1. Abrir el proyecto en tu IDE favorito
2. Localizar la clase `simulador.gui.MainWindow`
3. Ejecutar el método `main()`
4. La interfaz gráfica se abrirá automáticamente

---

## 📖 USO DEL SIMULADOR

### **Paso 1: Cargar Procesos**
1. Click en botón **[Load]**
2. Seleccionar archivo de procesos (`.txt`)
3. Archivos de ejemplo incluidos:
   - `procesos.txt` - Caso básico sin E/S
   - `procesos_io.txt` - Con ráfagas de E/S
   - `procesos_comparacion.txt` - Para comparar algoritmos

### **Paso 2: Configurar Simulación**
1. Seleccionar algoritmo de planificación:
   - FCFS (First Come First Served)
   - SJF (Shortest Job First)
   - Round Robin (especificar quantum)

2. Seleccionar algoritmo de memoria:
   - FIFO (First In First Out)
   - LRU (Least Recently Used)
   - Óptimo (teórico)

3. Configurar parámetros:
   - Número de marcos de memoria (ej: 10)
   - Quantum para Round Robin (ej: 3)

### **Paso 3: Ejecutar Simulación**
1. Click en botón **[Start]**
2. Observar la ejecución en tiempo real:
   - **Diagrama de Gantt:** Muestra qué proceso ejecuta en cada momento
   - **Panel de Memoria:** Estado de los marcos de memoria
   - **Colas de Procesos:** Procesos en READY, RUNNING, BLOCKED
   - **Log de Eventos:** Historial de acciones

### **Paso 4: Ver Resultados**
1. Al finalizar, se muestran las métricas:
   - Tiempo de espera promedio
   - Tiempo de retorno promedio
   - Utilización de CPU
   - Fallos de página totales
   - Reemplazos de página

2. Botón **[Pause]** para pausar la simulación
3. Botón **[Clear]** para limpiar y empezar de nuevo

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
