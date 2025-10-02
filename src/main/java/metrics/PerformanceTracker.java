package metrics;

public final class PerformanceTracker {
    private long comparisons;
    private long swaps;
    private long accesses;
    private long allocations;
    private int  depth;

    private long startNs;
    private long nanos;

    public void reset() {
        comparisons = swaps = accesses = allocations = 0;
        depth = 0;
        startNs = nanos = 0;
    }

    public void start() { startNs = System.nanoTime(); }

    public void stop()  { nanos = System.nanoTime() - startNs; }


    public void incCmp() { comparisons++; }
    public void incSwp() { swaps++; }
    public void incAcc() { accesses++; }
    public void incAlloc() { allocations++; }

    public void setDepth(int depth) { this.depth = depth; }

    public long getComparisons() { return comparisons; }
    public long getSwaps()       { return swaps; }
    public long getAccesses()    { return accesses; }
    public long getAllocations() { return allocations; }
    public long getNanos()       { return nanos; }
    public int  getDepth()       { return depth; }
}