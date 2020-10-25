package com.company;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

public abstract class MatrixThread extends Thread {
  protected final List<AbstractMap.SimpleEntry<Integer, Integer>> elements;
  protected final int startingRow, startingColumn, numberOfElements;
  protected final Matrix matrixA, matrixB, matrixC;
  protected int K;

  public MatrixThread(
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

  public MatrixThread(
      int startingRow,
      int startingColumn,
      int numberOfElements,
      int K,
      Matrix matrixA,
      Matrix matrixB,
      Matrix matrixC) {
    this.startingRow = startingRow;
    this.startingColumn = startingColumn;
    this.numberOfElements = numberOfElements;
    this.matrixA = matrixA;
    this.matrixB = matrixB;
    this.matrixC = matrixC;
    this.K = K;
    this.elements = new ArrayList<>();
    populateElements();
  }

  @Override
  public void run() {
    for (AbstractMap.SimpleEntry<Integer, Integer> element : elements) {
      int row = element.getKey();
      int column = element.getValue();
      matrixC.set(row, column, MatrixUtil.computeElement(matrixA, row, matrixB, column));
    }
  }

  protected void populateElements() {
    throw new IllegalStateException();
  }
}
