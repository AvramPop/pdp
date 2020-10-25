package com.company;

import java.util.AbstractMap;

public class KThread extends MatrixThread {
  public KThread(
      int startingRow,
      int startingColumn,
      int numberOfElements,
      int K,
      Matrix matrixA,
      Matrix matrixB,
      Matrix matrixC) {
    super(startingRow, startingColumn, numberOfElements, K, matrixA, matrixB, matrixC);
  }

  public void populateElements() {
    int i = startingRow, j = startingColumn;
    int count = numberOfElements;
    while (count > 0 && i < matrixC.rows && j < matrixC.columns) {
      elements.add(new AbstractMap.SimpleEntry<>(i, j));
      count--;
      i += (j + K) / matrixC.columns;
      j = (j + K) % matrixC.columns;
    }
  }
}
