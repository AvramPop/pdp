package com.company;

public class Main {
  private static final int rowsA = 4;
  private static final int colsA = 4;
  private static final int rowsB = 4;
  private static final int colsB = 4;
  private static final int NUMBER_OF_THREADS = 4;

  public static void main(String[] args) {
    Matrix matrixA = new Matrix(rowsA, colsA);
    matrixA.populate();
    Matrix matrixB = new Matrix(rowsB, colsB);
    matrixB.populate();
    System.out.println("Matrix A:");
    System.out.println(matrixA.toString());
    System.out.println("Matrix B:");
    System.out.println(matrixB.toString());

    if (matrixA.rows == matrixB.columns) {
      Matrix matrixC = new Matrix(matrixA.rows, matrixB.columns);
      classicThreadsDriver();
      //            threadPoolDriver();
    } else {
      System.err.println("Can't multiply these matrices");
    }
  }

  private static void threadPoolDriver() {}

  private static void classicThreadsDriver() {}
}
