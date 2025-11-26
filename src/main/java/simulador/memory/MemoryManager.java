package simulador.memory;

import simulador.process.Process;
import java.util.*;

/**
 * Gestor de memoria virtual con paginación
 */
public class MemoryManager {
    
    private final int totalFrames;
    private final Frame[] frames;
    private final PageReplacement replacementAlgorithm;
    private final Map<String, PageTable> pageTables;
    private int pageFaults = 0;
    private int pageReplacements = 0;
    
    public MemoryManager(int totalFrames, PageReplacement algorithm) {
        this.totalFrames = totalFrames;
        this.frames = new Frame[totalFrames];
        this.replacementAlgorithm = algorithm;
        this.pageTables = new HashMap<>();
        
        // Inicializar marcos
        for (int i = 0; i < totalFrames; i++) {
            frames[i] = new Frame(i);
        }
    }
    
    /**
     * Asigna marcos de memoria a un proceso
     */
    public synchronized boolean loadProcessPages(Process process) {
        String pid = process.getPid();
        int requiredPages = process.getPages();
        
        // Crear tabla de páginas si no existe
        if (!pageTables.containsKey(pid)) {
            pageTables.put(pid, new PageTable(pid, requiredPages));
        }
        
        PageTable pageTable = pageTables.get(pid);
        int loadedPages = 0;
        
        // Intentar cargar cada página
        for (int page = 0; page < requiredPages; page++) {
            if (!pageTable.isPageLoaded(page)) {
                if (loadPage(pid, page)) {
                    loadedPages++;
                }
            }
        }
        
        return loadedPages > 0 || pageTable.getAllPagesLoaded();
    }
    
    /**
     * Carga una página específica en memoria
     */
    private synchronized boolean loadPage(String pid, int pageNumber) {
        // Buscar marco libre
        int frameId = findFreeFrame();
        
        if (frameId == -1) {
            // No hay marcos libres, aplicar reemplazo
            frameId = replacementAlgorithm.selectVictimFrame();
            if (frameId == -1) {
                return false; // No se pudo obtener un marco
            }
            
            // Liberar el marco
            Frame victim = frames[frameId];
            if (victim.isOccupied()) {
                PageTable victimTable = pageTables.get(victim.getOwnerPid());
                if (victimTable != null) {
                    victimTable.unloadPage(victim.getPageNumber());
                }
            }
            pageReplacements++;
        }
        
        // Cargar la página en el marco
        frames[frameId].load(pid, pageNumber);
        replacementAlgorithm.useFrame(frameId);
        
        // Actualizar tabla de páginas
        PageTable pageTable = pageTables.get(pid);
        pageTable.loadPage(pageNumber, frameId);
        
        pageFaults++;
        System.out.println("[MEM] Fallo de página: " + pid + " página " + pageNumber + " -> marco " + frameId);
        
        return true;
    }
    
    /**
     * Busca un marco libre
     */
    private int findFreeFrame() {
        for (int i = 0; i < totalFrames; i++) {
            if (!frames[i].isOccupied()) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Libera todos los marcos de un proceso
     */
    public synchronized void releaseProcessMemory(String pid) {
        for (Frame frame : frames) {
            if (frame.isOccupied() && frame.getOwnerPid().equals(pid)) {
                frame.free();
            }
        }
        pageTables.remove(pid);
    }
    
    /**
     * Accede a una página (para LRU)
     */
    public synchronized void accessPage(String pid, int pageNumber) {
        PageTable pageTable = pageTables.get(pid);
        if (pageTable != null && pageTable.isPageLoaded(pageNumber)) {
            int frameId = pageTable.getFrameId(pageNumber);
            if (frameId != -1) {
                replacementAlgorithm.useFrame(frameId);
            }
        }
    }
    
    // ===== Getters y estadísticas =====
    
    public int getPageFaults() {
        return pageFaults;
    }
    
    public int getPageReplacements() {
        return pageReplacements;
    }
    
    public int getTotalFrames() {
        return totalFrames;
    }
    
    public int getFreeFrames() {
        int count = 0;
        for (Frame frame : frames) {
            if (!frame.isOccupied()) count++;
        }
        return count;
    }
    
    public Frame[] getFrames() {
        return frames;
    }
    
    public Map<String, PageTable> getPageTables() {
        return pageTables;
    }
    
    public String getMemoryStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n╔══════════════════════════════════════╗\n");
        sb.append("║      ESTADO DE LA MEMORIA            ║\n");
        sb.append("╠══════════════════════════════════════╣\n");
        sb.append(String.format("║ Total de marcos:        %12d ║\n", totalFrames));
        sb.append(String.format("║ Marcos libres:          %12d ║\n", getFreeFrames()));
        sb.append(String.format("║ Marcos ocupados:        %12d ║\n", totalFrames - getFreeFrames()));
        sb.append(String.format("║ Fallos de página:       %12d ║\n", pageFaults));
        sb.append(String.format("║ Reemplazos:             %12d ║\n", pageReplacements));
        sb.append("╚══════════════════════════════════════╝\n");
        return sb.toString();
    }
    
    /**
     * Clase interna para representar un marco de memoria
     */
    public static class Frame {
        private final int frameId;
        private boolean occupied;
        private String ownerPid;
        private int pageNumber;
        
        public Frame(int frameId) {
            this.frameId = frameId;
            this.occupied = false;
        }
        
        public void load(String pid, int page) {
            this.ownerPid = pid;
            this.pageNumber = page;
            this.occupied = true;
        }
        
        public void free() {
            this.occupied = false;
            this.ownerPid = null;
            this.pageNumber = -1;
        }
        
        public int getFrameId() { return frameId; }
        public boolean isOccupied() { return occupied; }
        public String getOwnerPid() { return ownerPid; }
        public int getPageNumber() { return pageNumber; }
    }
    
    /**
     * Tabla de páginas por proceso
     */
    public static class PageTable {
        private final String pid;
        private final int totalPages;
        private final Map<Integer, Integer> pageToFrame; // página -> marco
        
        public PageTable(String pid, int totalPages) {
            this.pid = pid;
            this.totalPages = totalPages;
            this.pageToFrame = new HashMap<>();
        }
        
        public void loadPage(int page, int frame) {
            pageToFrame.put(page, frame);
        }
        
        public void unloadPage(int page) {
            pageToFrame.remove(page);
        }
        
        public boolean isPageLoaded(int page) {
            return pageToFrame.containsKey(page);
        }
        
        public int getFrameId(int page) {
            return pageToFrame.getOrDefault(page, -1);
        }
        
        public boolean getAllPagesLoaded() {
            return pageToFrame.size() == totalPages;
        }
        
        public String getPid() { return pid; }
        public int getTotalPages() { return totalPages; }
        public int getLoadedPages() { return pageToFrame.size(); }
        
        public Map<Integer, Integer> getPageMappings() {
            return new HashMap<>(pageToFrame);
        }
    }
}
