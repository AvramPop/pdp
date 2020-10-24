package com.company;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public class RowThread extends Thread {
  private final List<AbstractMap.SimpleEntry<Integer, Integer>> elements;
  private final int startingRow, startingColumn, numberOfElements;
  private final Matrix matrixA, matrixB, matrixC;

  public RowThread(
      int startingRow,
      int startingColumn,
      int numberOfElements,
      Matrix matrixA,
      Matrix matrixB,
      Matrix matrixC) {
    this.startingRow = startingRow;
    this.startingColumn = startingColumn;
    this.numberOfElements = numberOfElements;
    this.matrixA = matrixA;
    this.matrixB = matrixB;
    this.matrixC = matrixC;
    this.elements = new ArrayList<>();
    populateElements();
  }

  private void populateElements() {
    int i = startingRow, j = startingColumn;
    int count = numberOfElements;
    while (count > 0 && i < matrixC.rows && j < matrixC.columns) {
      elements.add(new AbstractMap.SimpleEntry<>(i, j));
      j++;
      count--;
      if (j == matrixC.rows) {
        j = 0;
        i++;
      }
    }
  }

  @Override
  public void run() {}
}
