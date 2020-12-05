package com.company.algorithms;

import com.company.Polynomial;
import com.company.PolynomialUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ParallelKaratsuba {
  private static final int MAX_DEPTH = 10;

  public static Polynomial multiply(
      Polynomial polynomial1, Polynomial polynomial2, int numberOfThreads, int currentDepth)
      throws ExecutionException, InterruptedException {
    if (currentDepth > MAX_DEPTH) {
      return SequentialKaratsuba.multiply(polynomial1, polynomial2);
    }

    if (polynomial1.getOrder() <= 2 || polynomial2.getOrder() <= 2) {
      return SequentialKaratsuba.multiply(polynomial1, polynomial2);
    }

    int halfArraySize = polynomial1.getOrder() / 2;
    Polynomial polynomial1Low = new Polynomial(halfArraySize);
    polynomial1Low.setCoefficients(polynomial1.getCoefficients().subList(0, halfArraySize));
    Polynomial polynomial1High = new Polynomial(halfArraySize);
    polynomial1High.setCoefficients(
        polynomial1.getCoefficients().subList(halfArraySize, polynomial1.getOrder()));
    Polynomial polynomial2Low = new Polynomial(halfArraySize);
    polynomial2Low.setCoefficients(polynomial2.getCoefficients().subList(0, halfArraySize));
    Polynomial polynomial2High = new Polynomial(halfArraySize);
    polynomial2High.setCoefficients(
        polynomial2.getCoefficients().subList(halfArraySize, polynomial2.getOrder()));
    Polynomial polynomial1LowHigh = PolynomialUtils.add(polynomial1Low, polynomial1High);
    Polynomial polynomial2LowHigh = PolynomialUtils.add(polynomial2Low, polynomial2High);

    ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
    Callable<Polynomial> task1 =
        () ->
            ParallelKaratsuba.multiply(
                polynomial1Low, polynomial2Low, currentDepth + 1, numberOfThreads);
    Callable<Polynomial> task2 =
        () ->
            ParallelKaratsuba.multiply(
                polynomial1High, polynomial2High, currentDepth + 1, numberOfThreads);
    Callable<Polynomial> task3 =
        () ->
            ParallelKaratsuba.multiply(
                polynomial1LowHigh, polynomial2LowHigh, currentDepth + 1, numberOfThreads);

    Future<Polynomial> lowFuture = executor.submit(task1);
    Future<Polynomial> highFuture = executor.submit(task2);
    Future<Polynomial> lowHighFuture = executor.submit(task3);

    executor.shutdown();

    Polynomial productLow = lowFuture.get();
    Polynomial productHigh = highFuture.get();
    Polynomial productLowHigh = lowHighFuture.get();

    executor.awaitTermination(60, TimeUnit.SECONDS);

    // calculate the final result
    Polynomial r1 = PolynomialUtils.shiftRight(productHigh, 2 * halfArraySize);
    Polynomial r2 =
        PolynomialUtils.shiftRight(
            PolynomialUtils.subtract(PolynomialUtils.subtract(productLowHigh, productHigh), productLow), halfArraySize);
    return PolynomialUtils.add(PolynomialUtils.add(r1, r2), productLow);
  }
}
