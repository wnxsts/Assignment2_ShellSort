Assignment 2: Shell Sort

Author: Samatova Zhanel
Group: SE-2419

⸻

Overview

Algorithm Used:
Shell Sort with multiple gap sequences: Shell’s (n/2, n/4, …), Knuth’s (1, 4, 13, …), and Sedgewick’s (1, 5, 19, …).
Implements in-place sorting using gapped insertion sort across progressively smaller gaps until gap = 1.

Technologies:
Java 17, Maven, JUnit5.

Tracked Metrics:
Comparisons, swaps, array accesses, memory allocations, execution time.

⸻

Project Structure
<img width="461" height="716" alt="image" src="https://github.com/user-attachments/assets/a11c056a-84d8-4995-bede-5bfc16c3141e" />



⸻

How to Build & Run

mvn clean test
# Run benchmark
mvn exec:java -Dexec.mainClass=cli.BenchmarkRunner

Generates results in CSV format.

⸻

Explanation for the Allocations

The allocations column is part of the required performance metrics. In Shell Sort, all operations are performed directly inside the array without creating new arrays. Only a few integers (temporary variables, gap arrays) are allocated during gap sequence generation, and these are negligible compared to input size.
Thus, allocations are constant and near zero in practice, confirming that Shell Sort is memory-efficient and in-place.

⸻

Recurrence Analysis

Shell Sort complexity depends heavily on the chosen gap sequence:
	•	Shell’s sequence:
	•	Worst case: Θ(n²)
	•	Average: O(n²)
	•	Best case: Ω(n log n) (for nearly sorted inputs)
	•	Knuth’s sequence:
	•	More efficient gaps: average ≈ Θ(n^(3/2))
	•	Best case: Ω(n log n)
	•	Worst case: O(n^(3/2))
	•	Sedgewick’s sequence (1986):
	•	Best case: Ω(n log n)
	•	Average: Θ(n^(4/3))
	•	Worst case: O(n^(4/3))
	•	Most efficient among tested schemes.

Empirical results confirm that Sedgewick’s sequence achieves lower runtime and fewer comparisons compared to Shell and Knuth sequences.

⸻

Graphs and Results
<img width="1014" height="463" alt="image" src="https://github.com/user-attachments/assets/ee09ade0-9602-46a7-9e5e-5a03859d20cd" />
<img width="901" height="421" alt="image" src="https://github.com/user-attachments/assets/142fc3d1-6a49-4113-bf92-7e0a45d2eda9" />
<img width="815" height="545" alt="image" src="https://github.com/user-attachments/assets/dcc9fa5a-bd3e-4f65-af05-1256c4980c45" />



Time vs n



Runtime increases with input size n = 100 … 100,000.
	•	Shell’s gaps: slowest, close to quadratic growth.
	•	Knuth’s gaps: improved, sub-quadratic.
	•	Sedgewick’s gaps: fastest, close to Θ(n^(4/3)).

Comparisons vs n


	•	Shell’s sequence performs the most comparisons.
	•	Knuth reduces comparisons significantly.
	•	Sedgewick minimizes comparisons, especially on large n.

Swaps vs n


	•	Similar trends: Sedgewick has the fewest swaps.
	•	Random and reversed inputs require more swaps than sorted/nearly sorted.

Accesses vs n


	•	Access patterns scale with comparisons and swaps.
	•	Sedgewick again shows the most efficient access counts.

⸻

Architecture Notes
	•	In-place gapped insertion sort (no recursion).
	•	Flexible GapScheme enum (SHELL, KNUTH, SEDGEWICK).
	•	PerformanceTracker monitors operations and writes to CSV.
	•	CLI runner allows testing with multiple input distributions and sizes.
	•	Consistent structure with Heap Sort project for direct comparison.

⸻

JVM & Runtime Notes
	•	JVM warm-up effects noticeable for small n.
	•	Benchmarks include warm-up trials to stabilize measurements.
	•	Garbage Collector overhead negligible (no extra memory allocated).
	•	Consistent trends across random, sorted, reversed, nearly sorted.

⸻

Summary
	•	Shell Sort’s complexity is highly dependent on the gap sequence.
	•	Sedgewick gaps provide the best balance of time and operations, confirming theory.
	•	Shell’s gaps are outdated and result in quadratic behavior.
	•	Metrics and plots validate theoretical predictions:
	•	Shell: O(n²) worst case
	•	Knuth: O(n^(3/2))
	•	Sedgewick: O(n^(4/3))
	•	Algorithm remains in-place and memory-efficient.


