package cli;

import algorithms.ShellSort;
import algorithms.ShellSort.GapScheme;
import metrics.PerformanceTracker;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * BenchmarkRunner:
 *  - По умолчанию запускает матрицу прогонов:
 *      n in {100, 1000, 10000, 100000}
 *      distribution in {random, sorted, reversed, nearly}
 *      scheme in {SHELL, KNUTH, SEDGEWICK}
 *  - Пишет CSV с заголовком:
 *      n,distribution,scheme,comparisons,swaps,accesses,allocations,nanos
 *
 *  Ручной режим (один прогон):
 *    --n 10000 --trials 3 --distribution random --scheme KNUTH --out results.csv
 */
public class BenchmarkRunner {

    // Значения "по умолчанию" для матрицы
    private static final int[] DEFAULT_NS = {100, 1000, 10000, 100000};
    private static final String[] DISTRIBUTIONS = {"random", "sorted", "reversed", "nearly"};
    private static final GapScheme[] SCHEMES = {GapScheme.SHELL, GapScheme.KNUTH, GapScheme.SEDGEWICK};

    public static void main(String[] args) throws Exception {
        Map<String, String> cli = parseArgs(args);

        // Если указан --n, работаем в "ручном" режиме с возможностью указать все флаги
        if (cli.containsKey("n")) {
            int n = Integer.parseInt(cli.getOrDefault("n", "10000"));
            int trials = Integer.parseInt(cli.getOrDefault("trials", "3"));
            String distribution = cli.getOrDefault("distribution", "random").toLowerCase(Locale.ROOT);
            String schemeStr = cli.getOrDefault("scheme", "KNUTH").toUpperCase(Locale.ROOT);
            String out = cli.getOrDefault("out", "results.csv");

            GapScheme scheme = GapScheme.valueOf(schemeStr);
            ensureCsvHeader(out); // создаём файл с заголовком, если его ещё нет

            runOne(out, n, distribution, scheme, trials);
            System.out.println("Done. Wrote -> " + out);
            return;
        }

        // Иначе – матричный прогон
        String out = cli.getOrDefault("out", "results.csv");
        ensureCsvHeader(out);

        for (int n : DEFAULT_NS) {
            for (String distribution : DISTRIBUTIONS) {
                for (GapScheme scheme : SCHEMES) {
                    // по желанию можно увеличить trials для стабильности
                    runOne(out, n, distribution, scheme, 3);
                }
            }
        }
        System.out.println("Done. Wrote -> " + out);
    }

    /**
     * Выполняет несколько прогонов и пишет aggregate (средние по метрикам) в CSV.
     */
    private static void runOne(String outCsv,
                               int n,
                               String distribution,
                               GapScheme scheme,
                               int trials) throws IOException {

        ShellSort sorter = new ShellSort();
        PerformanceTracker t = new PerformanceTracker();

        long sumCmp = 0, sumSwp = 0, sumAcc = 0, sumAlloc = 0, sumNanos = 0;

        for (int tr = 0; tr < trials; tr++) {
            int[] a = makeArray(n, distribution, 123L + tr);

            t.reset();
            sorter.sort(a, scheme, t); // sort внутри вызывает t.start/t.stop (как у тебя)

            sumCmp += t.getComparisons();
            sumSwp += t.getSwaps();
            sumAcc += t.getAccesses();
            sumAlloc += t.getAllocations();
            sumNanos += t.getNanos();
        }

        long avgCmp = sumCmp / trials;
        long avgSwp = sumSwp / trials;
        long avgAcc = sumAcc / trials;
        long avgAlloc = sumAlloc / trials;
        long avgNanos = sumNanos / trials;

        appendCsvLine(outCsv, n, distribution, scheme.name(),
                avgCmp, avgSwp, avgAcc, avgAlloc, avgNanos);
    }

    // ---------- генераторы входных массивов ----------

    private static int[] makeArray(int n, String distribution, long seed) {
        switch (distribution) {
            case "random":
                return randomArray(n, seed);
            case "sorted":
                return sortedArray(n);
            case "reversed":
                return reversedArray(n);
            case "nearly":
                return nearlySortedArray(n, 0.01, seed); // 1% перетасовки
            default:
                throw new IllegalArgumentException("Unknown distribution: " + distribution);
        }
    }

    private static int[] randomArray(int n, long seed) {
        Random r = new Random(seed);
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = r.nextInt();
        return a;
    }

    private static int[] sortedArray(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = i;
        return a;
    }

    private static int[] reversedArray(int n) {
        int[] a = new int[n];
        for (int i = 0; i < n; i++) a[i] = n - i;
        return a;
    }

    /**
     * Почти отсортированный: берём отсортированный и делаем p% случайных swap.
     */
    private static int[] nearlySortedArray(int n, double p, long seed) {
        int[] a = sortedArray(n);
        int swaps = Math.max(1, (int) Math.round(n * p));
        Random r = new Random(seed);
        for (int k = 0; k < swaps; k++) {
            int i = r.nextInt(n);
            int j = r.nextInt(n);
            int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
        }
        return a;
    }

    // ---------- CSV ----------

    private static void ensureCsvHeader(String out) throws IOException {
        Path path = Path.of(out);
        if (!Files.exists(path) || Files.size(path) == 0) {
            try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(path))) {
                pw.println("n,distribution,scheme,comparisons,swaps,accesses,allocations,nanos");
            }
        }
    }

    private static void appendCsvLine(String out,
                                      int n,
                                      String distribution,
                                      String scheme,
                                      long cmp,
                                      long swp,
                                      long acc,
                                      long alloc,
                                      long nanos) throws IOException {
        try (PrintWriter pw = new PrintWriter(Files.newBufferedWriter(
                Path.of(out), java.nio.file.StandardOpenOption.APPEND))) {
            pw.printf(Locale.ROOT, "%d,%s,%s,%d,%d,%d,%d,%d%n",
                    n, distribution, scheme, cmp, swp, acc, alloc, nanos);
        }
    }

    // ---------- CLI parsing ----------

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> m = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String a = args[i];
            if (a.startsWith("--")) {
                String key = a.substring(2);
                String val = "true";
                if (i + 1 < args.length && !args[i + 1].startsWith("--")) {
                    val = args[++i];
                }
                m.put(key, val);
            }
        }
        return m;
    }
}