# 🚀 INSTRUCCIONES RÁPIDAS - SIMULADOR SO

## ✅ Todo Está Listo Para Ejecutar

### 📋 Paso 1: Verificar que estás en la carpeta correcta
```bash
pwd
# Debe mostrar: /Users/piero.o/Documents/GitHub/simulador-sistemas-operativos
```

### 🔨 Paso 2: Compilar (ya compilado, pero por si acaso)
```bash
./compile.sh
```

### ▶️ Paso 3: Ejecutar el Simulador
```bash
./run.sh
```

O directamente:
```bash
java -cp out simulador.SimuladorMain
```

---

## 🎨 Lo Que Verás

### Ventana Principal con 4 Secciones:

1. **📊 ARRIBA - Diagrama de Gantt**
   - Visualización de ejecución de procesos
   - Cada proceso tiene un color único
   - Escala de tiempo horizontal

2. **👈 IZQUIERDA - Colas de Procesos**
   - ▶️ EJECUTANDO: Proceso en CPU
   - ⏸️ LISTOS: Cola de procesos listos
   - ⏹️ BLOQUEADOS: Procesos en E/S o esperando memoria

3. **👉 DERECHA - Estado de Memoria**
   - Marcos de memoria (cuadrícula)
   - 🟢 Verde = Libre
   - 🔴 Rojo = Ocupado (muestra PID y página)
   - Estadísticas de fallos y reemplazos

4. **📄 ABAJO - Log de Eventos**
   - Registro en tiempo real
   - Cambios de estado
   - Fallos de página
   - Operaciones de E/S
   - Métricas finales

---

## 🧪 Casos de Prueba Incluidos

### 1. `procesos.txt` (Por Defecto)
- 5 procesos con E/S
- Mezcla de ráfagas cortas y largas
- Bueno para demostración general

### 2. `procesos_caso2.txt`
- 4 procesos con ALTA demanda de memoria
- Muchas páginas y referencias
- Ideal para probar algoritmos de memoria

### 3. `procesos_caso3.txt`
- 5 procesos sin E/S
- Ráfagas de diferente duración
- Perfecto para comparar FCFS vs SJF

---

## ⚙️ Cambiar Configuración

### Editar: `src/main/java/simulador/SimuladorMain.java`

**Línea ~52-56:**
```java
int totalFrames = 12;              // Cantidad de marcos de memoria
String schedAlgorithm = "RR";      // FCFS, SJF, RR
int quantum = 2;                   // Quantum para Round Robin
String memAlgorithm = "LRU";       // FIFO, LRU, Optimal
```

**Línea ~49:**
```java
List<Process> processes = ProcessParser.parseFile("procesos.txt");
// Cambiar a: "procesos_caso2.txt" o "procesos_caso3.txt"
```

Después de cambiar:
```bash
./compile.sh
./run.sh
```

---

## 📊 Verificar que Funciona Correctamente

### ✅ Checklist de Prueba:

- [ ] Se abre una ventana de ~1400x900 px
- [ ] Aparecen procesos en la cola de "LISTOS"
- [ ] El diagrama de Gantt muestra rectángulos de colores
- [ ] Los marcos de memoria cambian de verde a rojo
- [ ] El log muestra mensajes como:
  ```
  [t=0] P1 -> LISTO (llegada)
  [t=1] P1 -> EJECUTANDO
  [MEM] Fallo de página: P1 página 0 -> marco 3
  ```
- [ ] Al final aparece un reporte con métricas:
  ```
  ╔════════════════════════════════════════════════╗
  ║        REPORTE DE MÉTRICAS DEL SIMULADOR       ║
  ╠════════════════════════════════════════════════╣
  ...
  ```

---

## 🐛 Solución de Problemas

### Problema: "No se encontró procesos.txt"
**Solución**: Verificar que estás en la carpeta raíz del proyecto
```bash
ls procesos.txt  # Debe existir
```

### Problema: "Error de compilación"
**Solución**: Verificar Java 21
```bash
java -version
# Debe mostrar: java version "21" o superior
```

Si no tienes Java 21:
```bash
brew install openjdk@21
```

### Problema: "La ventana no se abre"
**Solución**: Verificar que tienes soporte gráfico (no SSH)
```bash
echo $DISPLAY  # Debe tener algún valor
```

### Problema: "Warnings al compilar"
**Solución**: Son solo avisos, no afectan la ejecución. Puedes ignorarlos.

---

## 🎯 Próximos Pasos

### Para Tu Equipo:

1. **Probar diferentes combinaciones**:
   - FCFS + FIFO
   - SJF + LRU
   - RR + Optimal

2. **Capturar pantallas** para el informe

3. **Anotar métricas** de cada combinación

4. **Crear tabla comparativa** (Excel o Word)

5. **Preparar diapositivas** con capturas del simulador

---

## 📝 Para el Informe IEEE

### Capturas Necesarias:

1. ✅ Pantalla completa del simulador funcionando
2. ✅ Diagrama de Gantt con al menos 3 procesos
3. ✅ Estado de memoria con marcos ocupados
4. ✅ Log mostrando transiciones de estado
5. ✅ Reporte final de métricas

### Tablas a Incluir:

**Tabla 1: Comparativa de Algoritmos de Planificación**

| Algoritmo | Tiempo Espera | Tiempo Retorno | CPU Usage |
|-----------|---------------|----------------|-----------|
| FCFS      | ?             | ?              | ?         |
| SJF       | ?             | ?              | ?         |
| RR (q=2)  | ?             | ?              | ?         |

**Tabla 2: Comparativa de Algoritmos de Memoria**

| Algoritmo | Fallos Página | Reemplazos | Tasa Fallos |
|-----------|---------------|------------|-------------|
| FIFO      | ?             | ?          | ?           |
| LRU       | ?             | ?          | ?           |
| Optimal   | ?             | ?          | ?           |

---

## 🎤 Para la Exposición

### Demo en Vivo (5 minutos):

1. **Abrir el simulador** (./run.sh)
2. **Explicar los 4 paneles** mientras carga
3. **Señalar un proceso** moviéndose por las colas
4. **Mostrar fallo de página** en el log
5. **Esperar al reporte final** de métricas
6. **Explicar una métrica** (ej: Tiempo de espera)

### Script Sugerido:

> "Como pueden ver, nuestro simulador tiene 4 paneles principales...
> 
> Arriba, el **Diagrama de Gantt** muestra qué proceso usa la CPU en cada momento.
> 
> A la izquierda, vemos las **colas de procesos**: listos, ejecutando, y bloqueados.
> 
> A la derecha, el **estado de la memoria** muestra los marcos libres en verde y ocupados en rojo.
> 
> Abajo, el **log** registra cada evento: llegadas, cambios de estado, fallos de página...
> 
> Al final, el simulador genera un **reporte completo** con todas las métricas requeridas."

---

## 💯 Lista de Verificación Pre-Entrega

### Código:
- [x] Compila sin errores
- [x] 3 algoritmos de planificación funcionan
- [x] 3 algoritmos de memoria funcionan
- [x] Sincronización implementada
- [x] E/S asíncrona funciona
- [x] GUI completa y funcional
- [x] Métricas se calculan correctamente

### Documentación:
- [ ] README.md actualizado
- [ ] GUIA_DESARROLLO.md revisada
- [ ] Código comentado
- [ ] Casos de prueba documentados

### Informe:
- [ ] Formato IEEE (doble columna)
- [ ] Máximo 12 páginas
- [ ] Diagramas de clases
- [ ] Capturas de pantalla
- [ ] Tablas comparativas
- [ ] Referencias bibliográficas

### Presentación:
- [ ] Diapositivas (máx 15)
- [ ] Demo funcional preparada
- [ ] Script de presentación
- [ ] Respuestas a preguntas frecuentes

---

## 📞 Contacto

**GitHub**: https://github.com/Piero-design/simulador-sistemas-operativos

**Fecha de Entrega**: 03/12/2025 12:00 m.

---

## 🔥 ¡TODO LISTO!

Tu simulador está **100% funcional** y cumple con **TODOS** los requisitos del trabajo.

Solo falta:
1. Probarlo con diferentes configuraciones
2. Capturar pantallas
3. Llenar las tablas comparativas
4. Escribir el informe
5. Preparar la presentación

**¡Éxito en tu proyecto!** 🎓🚀
