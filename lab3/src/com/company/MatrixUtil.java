package com.company;

public class MatrixUtil {
  public static int computeElement(Matrix matrixA, int row, Matrix matrixB, int column) {
    int result = 0;
    if (row < matrixA.rows && column < matrixB.columns) {
      for (int i = 0; i < matrixA.columns; i++) {
        for (int j = 0; j < matrixB.rows; j++) {
          result += matrixA.get(row, i) * matrixB.get(j, column);
        }
      }
      return result;
    } else {
      throw new IllegalArgumentException();
    }
  }
}
