package algorithms;

import metrics.PerformanceTracker;

import java.util.ArrayList;
import java.util.Random;
import java.util.TreeSet;

public class ShellSort {

    public enum GapScheme { SHELL, KNUTH, SEDGEWICK }

    public void sort(int[] a, GapScheme scheme, PerformanceTracker t) {
        if (a == null) throw new IllegalArgumentException("array is null");
        if (scheme == null) scheme = GapScheme.KNUTH;

        t.reset();
        t.start();

        int[] gaps = buildGaps(a.length, scheme);
        t.setDepth(gaps.length);

        for (int g = gaps.length - 1; g >= 0; g--) {
            int gap = gaps[g];
            for (int i = gap; i < a.length; i++) {
                // int tmp = a[i];
                int tmp = a[i]; t.incAcc();
                int j = i;

                while (true) {
                    int left = j - gap;
                    if (left < 0) break;

                    int leftVal = a[left]; t.incAcc();
                    t.incCmp();
                    if (leftVal <= tmp) break;

                    a[j] = leftVal; t.incAcc();
                    t.incSwp();
                    j -= gap;
                }
                a[j] = tmp; t.incAcc();
            }
        }

        t.stop();
    }

    private int[] buildGaps(int n, GapScheme scheme) {
        ArrayList<Integer> list = new ArrayList<>();

        switch (scheme) {
            case SHELL: {
                for (int g = n / 2; g > 0; g /= 2) list.add(g);
                break;
            }
            case KNUTH: {
                for (int h = 1; h < n; h = 3 * h + 1) list.add(h);
                break;
            }
            case SEDGEWICK: {
                for (int p = 0; p < 32; p++) {
                    long g1 = (long)(9 * Math.pow(4, p) - 9 * Math.pow(2, p) + 1);
                    long g2 = (long)(Math.pow(4, p + 1) - 3 * Math.pow(2, p + 1) + 1);
                    if (g1 > 0 && g1 < n) list.add((int) g1);
                    if (g2 > 0 && g2 < n) list.add((int) g2);
                    if (g1 >= n && g2 >= n) break;
                }
                break;
            }
        }

        TreeSet<Integer> set = new TreeSet<>(list);
        set.removeIf(x -> x <= 0);
        if (!set.contains(1)) set.add(1);

        int[] gaps = new int[set.size()];
        int i = 0; for (int g : set) gaps[i++] = g;
        return gaps;
    }

    public static int[] randomArray(int n, long seed) {
        Random r = new Random(seed);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = r.nextInt();
        return a;
    }

    public static boolean isSorted(int[] a) {
        for (int i = 1; i < a.length; i++) if (a[i] < a[i - 1]) return false;
        return true;
    }
}