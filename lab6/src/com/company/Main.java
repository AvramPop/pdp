package com.company;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Main {

  public static void main(String[] args) {
    int[][] graphMatrix = {
      {0, 1, 0, 0, 0},
      {0, 0, 1, 0, 0},
      {0, 0, 0, 1, 0},
      {0, 0, 0, 0, 1},
      {1, 0, 0, 0, 0},
    };
    Graph graph = new Graph(graphMatrix);
    ExecutorService executorService = Executors.newFixedThreadPool(graph.size);
    List<HamiltonianThread> threads = prepareData(graph, executorService);
    generateSolution(executorService, threads);
  }

  private static List<HamiltonianThread> prepareData(Graph graph, ExecutorService executorService)
  {
    List<HamiltonianThread> threads = new ArrayList<>();
    for (int i = 0; i < graph.size; i++) {
      threads.add(new HamiltonianThread(graph, i));
      executorService.submit(threads.get(i));
    }
    return threads;
  }

  private static void generateSolution(ExecutorService executorService, List<HamiltonianThread> threads)
  {
    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(300, TimeUnit.SECONDS)) {
        executorService.shutdownNow();
      }
      threads = threads.stream()
          .filter(thread -> thread.cycle != null)
          .collect(Collectors.toList());
      threads.sort(Comparator.comparing(t -> t.elapsedTime));
      if (!threads.isEmpty()) {
        System.out.println("Solution found in " + threads.get(0).elapsedTime + "ms");
        System.out.println("The cycle is:");
        threads.get(0).cycle.forEach(v -> System.out.print(v + " "));
        System.out.println();
        System.out.println("starting vertex is: " + threads.get(0).startingVertex);
      }
    } catch (InterruptedException ex) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }
}
