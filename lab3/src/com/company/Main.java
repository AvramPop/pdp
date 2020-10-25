package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
  private static final int rowsA = 3;
  private static final int colsA = 3;
  private static final int rowsB = 3;
  private static final int colsB = 3;
  private static final int NUMBER_OF_THREADS = 4;
  private static final PoolStrategy POOL_STRATEGY = PoolStrategy.THREAD_POOL;
  private static final GenerationStrategy GENERATION_STRATEGY = GenerationStrategy.COLUMNS;

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
      run(matrixA, matrixB, matrixC);
    } else {
      System.err.println("Can't multiply these matrices");
    }
  }

  private static void run(Matrix matrixA, Matrix matrixB, Matrix matrixC) {
    switch (POOL_STRATEGY) {
      case CLASSIC:
        classicThreadsDriver(matrixA, matrixB, matrixC);
        break;

      case THREAD_POOL:
        threadPoolDriver(matrixA, matrixB, matrixC);
        break;

      default:
        System.err.println("Invalid pool strategy");
        break;
    }
  }

  private static void threadPoolDriver(Matrix matrixA, Matrix matrixB, Matrix matrixC) {
    ExecutorService executorService = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    switch (GENERATION_STRATEGY) {
      case ROWS:
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
          executorService.submit(createRowThread(i, matrixA, matrixB, matrixC));
        }
        break;

      case COLUMNS:
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
          executorService.submit(createColumnThread(i, matrixA, matrixB, matrixC));
        }
        break;

      case KTH:
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
          executorService.submit(createKthThread(i, matrixA, matrixB, matrixC));
        }
        break;

      default:
        System.err.println("Invalid generation strategy");
        break;
    }
    executorService.shutdown();
    try {
      if (!executorService.awaitTermination(300, TimeUnit.SECONDS)) {
        executorService.shutdownNow();
      }
      System.out.println("Matrix C:");
      System.out.println(matrixC.toString());
    } catch (InterruptedException ex) {
      executorService.shutdownNow();
      Thread.currentThread().interrupt();
    }
  }

  private static MatrixThread createKthThread(
      int index, Matrix matrixA, Matrix matrixB, Matrix matrixC) {
    throw new IllegalStateException(); // TODO implement this
  }

  private static MatrixThread createColumnThread(
      int index, Matrix matrixA, Matrix matrixB, Matrix matrixC) {
    int numberOfElementsInMatrix = matrixC.rows * matrixC.columns;
    int setSize = numberOfElementsInMatrix / NUMBER_OF_THREADS;
    int startingLine = setSize * index % matrixC.rows;
    int startingColumn = setSize * index / matrixC.rows;
    if (index == NUMBER_OF_THREADS - 1) setSize += numberOfElementsInMatrix % NUMBER_OF_THREADS;
    //        System.out.println("ColumnThread #" + index + " line = " + startingLine + " column = "
    // +
    //     startingColumn + " els = " + setSize);
    return new ColumnThread(startingLine, startingColumn, setSize, matrixA, matrixB, matrixC);
  }

  private static MatrixThread createRowThread(
      int index, Matrix matrixA, Matrix matrixB, Matrix matrixC) {
    int numberOfElementsInMatrix = matrixC.rows * matrixC.columns;
    int setSize = numberOfElementsInMatrix / NUMBER_OF_THREADS;
    int startingLine = setSize * index / matrixC.rows;
    int startingColumn = setSize * index % matrixC.rows;
    if (index == NUMBER_OF_THREADS - 1) setSize += numberOfElementsInMatrix % NUMBER_OF_THREADS;
    //    System.out.println("RowThread #" + index + " line = " + startingLine + " column = " +
    // startingColumn + " els = " + setSize);
    return new RowThread(startingLine, startingColumn, setSize, matrixA, matrixB, matrixC);
  }

  private static void classicThreadsDriver(Matrix matrixA, Matrix matrixB, Matrix matrixC) {
    List<Thread> threads = new ArrayList<>();
    switch (GENERATION_STRATEGY) {
      case ROWS:
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
          threads.add(createRowThread(i, matrixA, matrixB, matrixC));
        }
        break;

      case COLUMNS:
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
          threads.add(createColumnThread(i, matrixA, matrixB, matrixC));
        }
        break;

      case KTH:
        for (int i = 0; i < NUMBER_OF_THREADS; i++) {
          threads.add(createKthThread(i, matrixA, matrixB, matrixC));
        }
        break;

      default:
        System.err.println("Invalid generation strategy");
        break;
    }
    for (Thread thread : threads) {
      thread.start();
    }
    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    System.out.println("Matrix C:");
    System.out.println(matrixC.toString());
  }
}
