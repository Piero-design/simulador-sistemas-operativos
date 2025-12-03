package simulador.memory;

import simulador.process.Process;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MemoryManager {
    
    private final int totalFrames;
    private final PageReplacement replacementAlgorithm;
    private final Map<String, PageTable> pageTables; // PID -> PageTable
    private final boolean[] frameOccupied; // Estado de cada marco
    private final Map<Integer, PageEntry> frameToPage; // Marco -> Entrada de página
    private int pageFaults;
    private int pageReplacements;
    private final Lock lock;

    public MemoryManager(int totalFrames, PageReplacement replacementAlgorithm) {
        this.totalFrames = totalFrames;
        this.replacementAlgorithm = replacementAlgorithm;
        this.pageTables = new HashMap<>();
        this.frameOccupied = new boolean[totalFrames];
        this.frameToPage = new HashMap<>();
        this.pageFaults = 0;
        this.pageReplacements = 0;
        this.lock = new ReentrantLock();
    }

    public synchronized void initializeProcess(Process process) {
        PageTable pageTable = new PageTable(process.getPages());
        pageTables.put(process.getPid(), pageTable);
    }

    public synchronized boolean loadPages(Process process, int numPages) {
        lock.lock();
        try {
            PageTable pageTable = pageTables.get(process.getPid());
            if (pageTable == null) {
                initializeProcess(process);
                pageTable = pageTables.get(process.getPid());
            }

            int pagesLoaded = 0;
            for (int pageNum = 0; pageNum < Math.min(numPages, process.getPages()); pageNum++) {
                if (!pageTable.isPageLoaded(pageNum)) {
                    int frame = allocateFrame();
                    if (frame != -1) {
                        pageTable.loadPage(pageNum, frame);
                        frameOccupied[frame] = true;
                        frameToPage.put(frame, new PageEntry(process.getPid(), pageNum));
                        replacementAlgorithm.useFrame(frame);
                        pagesLoaded++;
                    } else {
                        // Reemplazo de página necesario
                        frame = replacementAlgorithm.selectVictimFrame();
                        if (frame != -1) {
                            evictPage(frame);
                            pageTable.loadPage(pageNum, frame);
                            frameOccupied[frame] = true;
                            frameToPage.put(frame, new PageEntry(process.getPid(), pageNum));
                            replacementAlgorithm.useFrame(frame);
                            pageReplacements++;
                            pagesLoaded++;
                        } else {
                            pageFaults++;
                            return false; // No hay espacio disponible
                        }
                    }
                    pageFaults++;
                }
            }
            return true;
        } finally {
            lock.unlock();
        }
    }

    private int allocateFrame() {
        for (int i = 0; i < totalFrames; i++) {
            if (!frameOccupied[i]) {
                return i;
            }
        }
        return -1; // No hay marcos libres
    }

    private void evictPage(int frame) {
        PageEntry entry = frameToPage.get(frame);
        if (entry != null) {
            PageTable pageTable = pageTables.get(entry.pid);
            if (pageTable != null) {
                pageTable.evictPage(entry.pageNumber);
            }
            frameToPage.remove(frame);
        }
        frameOccupied[frame] = false;
    }

    public synchronized void releaseProcessPages(String pid) {
        lock.lock();
        try {
            PageTable pageTable = pageTables.get(pid);
            if (pageTable != null) {
                for (int i = 0; i < pageTable.getTotalPages(); i++) {
                    if (pageTable.isPageLoaded(i)) {
                        int frame = pageTable.getFrame(i);
                        frameOccupied[frame] = false;
                        frameToPage.remove(frame);
                    }
                }
                pageTables.remove(pid);
            }
        } finally {
            lock.unlock();
        }
    }

    public synchronized int getPageFaults() {
        return pageFaults;
    }

    public synchronized int getPageReplacements() {
        return pageReplacements;
    }

    public synchronized int getFreeFrames() {
        int count = 0;
        for (boolean occupied : frameOccupied) {
            if (!occupied) count++;
        }
        return count;
    }

    public synchronized Map<String, PageTable> getPageTables() {
        return new HashMap<>(pageTables);
    }

    public synchronized boolean[] getFrameOccupied() {
        return frameOccupied.clone();
    }

    public synchronized Map<Integer, FrameAllocation> getFrameAllocations() {
        Map<Integer, FrameAllocation> snapshot = new HashMap<>();
        for (Map.Entry<Integer, PageEntry> entry : frameToPage.entrySet()) {
            PageEntry value = entry.getValue();
            snapshot.put(entry.getKey(), new FrameAllocation(value.pid, value.pageNumber));
        }
        return snapshot;
    }

    public String getAlgorithmName() {
        return replacementAlgorithm.getName();
    }

    // Clase interna para representar una tabla de páginas
    public static class PageTable {
        private final int totalPages;
        private final Map<Integer, Integer> pageToFrame; // Número de página -> Marco
        private final Set<Integer> loadedPages;

        public PageTable(int totalPages) {
            this.totalPages = totalPages;
            this.pageToFrame = new HashMap<>();
            this.loadedPages = new HashSet<>();
        }

        public void loadPage(int pageNum, int frame) {
            pageToFrame.put(pageNum, frame);
            loadedPages.add(pageNum);
        }

        public void evictPage(int pageNum) {
            pageToFrame.remove(pageNum);
            loadedPages.remove(pageNum);
        }

        public boolean isPageLoaded(int pageNum) {
            return loadedPages.contains(pageNum);
        }

        public int getFrame(int pageNum) {
            return pageToFrame.getOrDefault(pageNum, -1);
        }

        public int getTotalPages() {
            return totalPages;
        }

        public Set<Integer> getLoadedPages() {
            return new HashSet<>(loadedPages);
        }
    }

    // Clase interna para rastrear qué página está en qué marco
    public static class FrameAllocation {
        private final String pid;
        private final int pageNumber;

        public FrameAllocation(String pid, int pageNumber) {
            this.pid = pid;
            this.pageNumber = pageNumber;
        }

        public String getPid() {
            return pid;
        }

        public int getPageNumber() {
            return pageNumber;
        }
    }

    private static class PageEntry {
        String pid;
        int pageNumber;

        PageEntry(String pid, int pageNumber) {
            this.pid = pid;
            this.pageNumber = pageNumber;
        }
    }
}
