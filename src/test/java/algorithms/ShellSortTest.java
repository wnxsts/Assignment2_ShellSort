package algorithms;

import metrics.PerformanceTracker;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class ShellSortTest {

    @Test
    void singleAllInOne() {
        ShellSort s = new ShellSort();


        checkSorted(s, new int[0]);                // empty
        checkSorted(s, new int[]{42});             // single
        checkSorted(s, new int[]{5,5,5,5,5});      // duplicates


        int[] sizes = {1, 2, 3, 10, 100, 1000};
        ShellSort.GapScheme[] schemes = {
                ShellSort.GapScheme.SHELL,
                ShellSort.GapScheme.KNUTH,
                ShellSort.GapScheme.SEDGEWICK
        };

        for (ShellSort.GapScheme scheme : schemes) {
            for (int n : sizes) {

                int[] rnd = randomArray(n, 123 + n);
                checkAgainstJdkSort(s, rnd, scheme);


                int[] sorted = randomArray(n, 456 + n);
                Arrays.sort(sorted);
                checkAgainstJdkSort(s, sorted, scheme);


                int[] reversed = sorted.clone();
                reverseInPlace(reversed);
                checkAgainstJdkSort(s, reversed, scheme);

                int[] nearly = sorted.clone();
                randomSwaps(nearly, Math.max(1, n / 10), 789 + n);
                checkAgainstJdkSort(s, nearly, scheme);
            }
        }
    }


    private static void checkSorted(ShellSort s, int[] a) {
        PerformanceTracker t = new PerformanceTracker();
        s.sort(a, ShellSort.GapScheme.KNUTH, t);
        assertTrue(ShellSort.isSorted(a));
    }

    private static void checkAgainstJdkSort(ShellSort s, int[] a, ShellSort.GapScheme scheme) {
        int[] expected = a.clone();
        Arrays.sort(expected);

        PerformanceTracker t = new PerformanceTracker();
        s.sort(a, scheme, t);

        assertArrayEquals(expected, a,
                "Mismatch for scheme=" + scheme + " n=" + a.length);
    }

    private static int[] randomArray(int n, long seed) {
        Random r = new Random(seed);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = r.nextInt();
        return a;
    }

    private static void reverseInPlace(int[] a) {
        for (int i = 0, j = a.length - 1; i < j; i++, j--) {
            int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
        }
    }

    private static void randomSwaps(int[] a, int times, long seed) {
        Random r = new Random(seed);
        int n = a.length;
        for (int k = 0; k < times; k++) {
            int i = r.nextInt(n), j = r.nextInt(n);
            int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
        }
    }
}