package com.company;

import java.util.ArrayList;
import java.util.List;

public class HamiltonianThread extends Thread {
  private final Graph graph;
  public final int startingVertex;
  public double elapsedTime;
  public List<Integer> cycle;
  public boolean finished;

  public HamiltonianThread(Graph graph, int startingVertex) {
    this.graph = graph;
    this.startingVertex = startingVertex;
  }

  @Override
  public void run() {
    List<Integer> path = new ArrayList<>();
    long startTime = System.nanoTime();
    for (int i = 0; i < graph.size; i++) {
      path.add(i, -1);
    }

    path.set(0, startingVertex);
    if (!hamiltonianCycleUtil(graph, path, 1, startingVertex)) {
      finished = false;
      cycle = path;
      long stopTime = System.nanoTime();
      elapsedTime = ((double) stopTime - (double) startTime) / 1_000_000_000.0;
      return;
    }
    long stopTime = System.nanoTime();
    elapsedTime = ((double) stopTime - (double) startTime) / 1_000_000_000.0;
    path.add(startingVertex);
    finished = true;
    cycle = path;
  }

  private boolean canProceed(
      int newVertex, Graph graph, List<Integer> currentPath, int currentPosition) {
    // check if edge exists
    if (graph.graph[currentPath.get(currentPosition - 1)][newVertex] == 0) {
      return false;
    }

    // check if already added
    return currentPath.stream().filter(current -> current == newVertex).count() <= 0;
  }

  private boolean hamiltonianCycleUtil(
      Graph graph, List<Integer> currentPath, int currentPosition, int startingVertex) {
    if (currentPosition == graph.size) {
      // edge from the last included vertex to the first vertex
      return graph.graph[currentPath.get(currentPosition - 1)][currentPath.get(0)] == 1;
    }

    for (int vertex = 0; vertex < graph.size; vertex++) {
      if (vertex != startingVertex && canProceed(vertex, graph, currentPath, currentPosition)) {
        currentPath.set(currentPosition, vertex);
        if (hamiltonianCycleUtil(graph, currentPath, currentPosition + 1, startingVertex)) {
          return true;
        }
        // if not leading to solution
        currentPath.set(currentPosition, -1);
      }
    }

    return false;
  }
}
