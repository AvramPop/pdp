package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {
  private static final int rowsA = 5;
  private static final int colsA = 3;
  private static final int rowsB = 3;
  private static final int colsB = 4;
  private static final int NUMBER_OF_THREADS = 4;
  private static final PoolStrategy POOL_STRATEGY = PoolStrategy.CLASSIC;
  private static final GenerationStrategy GENERATION_STRATEGY = GenerationStrategy.KTH;

  public static void main(String[] args) {
    Matrix matrixA = new Matrix(rowsA, colsA);
    matrixA.populate();
    Matrix matrixB = new Matrix(rowsB, colsB);
    matrixB.populate();
    System.out.println("Matrix A:");
    System.out.println(matrixA.toString());
    System.out.println("Matrix B:");
    System.out.println(matrixB.toString());

    if (matrixA.columns == matrixB.rows) {
      Matrix matrixC = new Matrix(matrixA.rows, matrixB.columns);
      long startTime = System.nanoTime();
      run(matrixA, matrixB, matrixC);
      long stopTime = System.nanoTime();
      double totalTime = ((double) stopTime - (double) startTime) / 1_000_000_000.0;
      System.out.println("Elapsed running time: " + totalTime + "s");
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
    ExecutorService executorService =
        new ThreadPoolExecutor(
            NUMBER_OF_THREADS,
            NUMBER_OF_THREADS,
            0L,
            TimeUnit.MILLISECONDS,
            new ArrayBlockingQueue<>(
                NUMBER_OF_THREADS, true)); // Executors.newFixedThreadPool(NUMBER_OF_THREADS);
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
    int numberOfElementsInMatrix = matrixC.rows * matrixC.columns;
    int setSize = numberOfElementsInMatrix / NUMBER_OF_THREADS;
    if (index < numberOfElementsInMatrix % NUMBER_OF_THREADS) setSize++;
    int startingLine = index / matrixC.columns;
    int startingColumn = index % matrixC.columns;
    return new KThread(
        startingLine, startingColumn, setSize, NUMBER_OF_THREADS, matrixA, matrixB, matrixC);
  }

  private static MatrixThread createColumnThread(
      int index, Matrix matrixA, Matrix matrixB, Matrix matrixC) {
    int numberOfElementsInMatrix = matrixC.rows * matrixC.columns;
    int setSize = numberOfElementsInMatrix / NUMBER_OF_THREADS;
    int startingLine = setSize * index % matrixC.rows;
    int startingColumn = setSize * index / matrixC.rows;
    if (index == NUMBER_OF_THREADS - 1) setSize += numberOfElementsInMatrix % NUMBER_OF_THREADS;
    return new ColumnThread(startingLine, startingColumn, setSize, matrixA, matrixB, matrixC);
  }

  private static MatrixThread createRowThread(
      int index, Matrix matrixA, Matrix matrixB, Matrix matrixC) {
    int numberOfElementsInMatrix = matrixC.rows * matrixC.columns;
    int setSize = numberOfElementsInMatrix / NUMBER_OF_THREADS;
    int startingLine = setSize * index / matrixC.rows;
    int startingColumn = setSize * index % matrixC.rows;
    if (index == NUMBER_OF_THREADS - 1) setSize += numberOfElementsInMatrix % NUMBER_OF_THREADS;
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
