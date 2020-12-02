package com.company;

public class Graph {
  public final int[][] graph;
  public final int size;

  public Graph(int[][] graph) {
    this.graph = graph;
    size = graph[0].length;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < graph[0].length; i++) {
      for (int j = 0; j < graph[i].length; j++) {
        stringBuilder.append(graph[i][j]).append(" ");
      }
      stringBuilder.append("\n");
    }
    return stringBuilder.toString();
  }
}
