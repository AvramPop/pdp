package com.company;

public class MatrixUtil {
  public static int computeElement(Matrix matrixA, int row, Matrix matrixB, int column) {
    int result = 0;
    if (row < matrixA.rows && column < matrixB.columns) {
      int i = 0;
      while (i < matrixA.columns) {
        result += matrixA.get(row, i) * matrixB.get(i, column);
        i++;
      }
      return result;
    } else {
      throw new IllegalArgumentException();
    }
  }
}
