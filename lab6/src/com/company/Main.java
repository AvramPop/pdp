package com.company;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

  public static void main(String[] args) {
    //    int[][] graphMatrix = {
    //      {0, 1, 0, 0, 0},
    //      {0, 0, 1, 0, 0},
    //      {0, 0, 0, 1, 0},
    //      {0, 0, 0, 0, 1},
    //      {0, 0, 0, 0, 0},
    //    };
    int[][] graphMatrix = generateMatrix(500);
    Graph graph = new Graph(graphMatrix);
    ExecutorService executorService = Executors.newFixedThreadPool(graph.size);
    List<HamiltonianThread> threads = prepareData(graph, executorService);
    long startTime = System.nanoTime();
    generateSolution(executorService, threads, startTime);
  }

  private static int[][] generateMatrix(int n) {
    Random random = new Random();
    int[][] matrix = new int[n][n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if (i == j) {
          matrix[i][j] = 0;
        } else {
          matrix[i][j] = random.nextInt(2);
        }
      }
    }
    return matrix;
  }

  private static List<HamiltonianThread> prepareData(Graph graph, ExecutorService executorService) {
    List<HamiltonianThread> threads = new ArrayList<>();
    for (int i = 0; i < graph.size; i++) {
      threads.add(new HamiltonianThread(graph, i));
      executorService.submit(threads.get(i));
    }
    return threads;
  }

  private static void generateSolution(
      ExecutorService executorService, List<HamiltonianThread> threads, long startTime) {
    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(300, TimeUnit.SECONDS)) {
        executorService.shutdownNow();
      }
      if (threads.stream().anyMatch(thread -> thread.finished)) {
        threads = threads.stream().filter(thread -> thread.finished).collect(Collectors.toList());
        threads.sort(Comparator.comparing(t -> t.elapsedTime));
        System.out.println("Solution found in " + threads.get(0).elapsedTime + "s");
        System.out.println("The cycle is:");
        threads.get(0).cycle.forEach(v -> System.out.print(v + " "));
        System.out.println();
        System.out.println("starting vertex is: " + threads.get(0).startingVertex);
      } else {
        long stopTime = System.nanoTime();
        double elapsedTime = ((double) stopTime - (double) startTime) / 1_000_000_000.0;
        System.err.println("No solution found after " + elapsedTime + "s");
      }
    } catch (InterruptedException ex) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
