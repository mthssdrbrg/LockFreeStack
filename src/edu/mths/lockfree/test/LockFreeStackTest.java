package edu.mths.lockfree.test;

import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import edu.mths.lockfree.stack.LockFreeStack;

/**
 * Simple test for the {@link LockFreeStack} which runs an increasing number of
 * threads on a stack instance and prints out averaged values for the number of
 * tries of the {@link AtomicReference#compareAndSet(Object, Object)} method and
 * running times.
 * 
 * @author Mathias Söderberg
 * 
 */
public class LockFreeStackTest {

	// each iteration performs two operations on the stack
	public static final int NR_OF_ITERATIONS = 1000000;
	// total number of runs
	public static final int NR_OF_RUNS = 15;
	// constant value used for converting to s from ms
	public static final Double TO_SECONDS = 1000.0;
	// used to get the average of the runs
	public static final int AVERAGE = 5;
	// number of threads (to start off with)
	private static int nrOfThreads = 1;
	// the stack itself
	private static LockFreeStack<Integer> stack;
	// used for thread pooling
	private static ExecutorService executor;
	// map for the tries per run
	private static TreeMap<Integer, Double> totalTriesMap;
	// map for values of running time per run
	private static TreeMap<Integer, Double> totalRunningTimeMap;

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Running...");

		totalTriesMap = new TreeMap<Integer, Double>();
		totalRunningTimeMap = new TreeMap<Integer, Double>();

		for (int l = 0; l < 5; l++) {
			for (int i = 0; i < NR_OF_RUNS; i++, nrOfThreads++) {
				long start = System.nanoTime();
				stack = new LockFreeStack<Integer>();
				executor = Executors.newFixedThreadPool(nrOfThreads);

				for (int j = 0; j < nrOfThreads; j++) {
					StackRunnable runnable = new StackRunnable(NR_OF_ITERATIONS
							/ nrOfThreads, stack);
					executor.execute(runnable);
				}

				if (NR_OF_ITERATIONS % nrOfThreads > 0) {
					StackRunnable runnable = new StackRunnable(NR_OF_ITERATIONS
							% nrOfThreads, stack);
					executor.execute(runnable);
				}

				executor.shutdown();
				// wait until all threads are finish (polling)
				while (!executor.isTerminated()) {
				}

				long stop = System.nanoTime();

				if (!totalTriesMap.containsKey(nrOfThreads)) {
					totalTriesMap.put(nrOfThreads, (double) stack.getTries());
					totalRunningTimeMap.put(nrOfThreads, (stop - start)
							/ TO_SECONDS);
				} else {
					totalTriesMap.put(nrOfThreads,
							totalTriesMap.get(nrOfThreads) + stack.getTries());
					totalRunningTimeMap.put(nrOfThreads,
							totalRunningTimeMap.get(nrOfThreads)
									+ ((stop - start) / TO_SECONDS));
				}
			}
			// reset the number of threads
			nrOfThreads = 1;
		}

		System.out.format("Threads \t Tries \t\t Running time %n");
		for (Integer i : totalTriesMap.keySet()) {
			int tries = (int) (totalTriesMap.get(i) / AVERAGE);

			System.out.format("%d \t\t %d \t %.4f %n", i, tries,
					totalRunningTimeMap.get(i) / AVERAGE);
		}
	}
}