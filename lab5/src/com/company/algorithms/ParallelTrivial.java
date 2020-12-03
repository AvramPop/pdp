package com.company.algorithms;

import com.company.Polynomial;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ParallelTrivial {

  public static Polynomial multiply(
      Polynomial polynomial1, Polynomial polynomial2, int numberOfThreads) {
    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
    List<Callable<List<Integer>>> callables = new ArrayList<>();
    for (int i = 0; i < polynomial1.getOrder(); i++) {
      callables.add(new TrivialThread(polynomial1, polynomial2, i));
    }
    try {
      List<Future<List<Integer>>> futures = executorService.invokeAll(callables);
      Polynomial result = addPolynomials(futures, polynomial1.getOrder(), polynomial2.getOrder());
      executorService.shutdown();
      return result;
    } catch (InterruptedException | ExecutionException e) {
      System.err.println("There was a problem while computing");
      return null;
    }
  }

  private static Polynomial addPolynomials(
      List<Future<List<Integer>>> futures, int order1, int order2)
      throws ExecutionException, InterruptedException {
    Polynomial result = new Polynomial();
    for (int i = 0; i < order1 + order2 - 1; i++) {
      result.getCoefficients().add(0);
    }
    for (Future<List<Integer>> partialResult : futures) {
      List<Integer> actualPartialResult = partialResult.get();
      for (int i = 0; i < actualPartialResult.size(); i++) {
        result
            .getCoefficients()
            .set(i, result.getCoefficients().get(i) + actualPartialResult.get(i));
      }
    }
    return result;
  }

  private static class TrivialThread implements Callable<List<Integer>> {
    private final Polynomial polynomial1;
    private final Polynomial polynomial2;
    private final int indexInPolynomial1;

    public TrivialThread(Polynomial polynomial1, Polynomial polynomial2, int indexInPolynomial1) {
      this.polynomial1 = polynomial1;
      this.polynomial2 = polynomial2;
      this.indexInPolynomial1 = indexInPolynomial1;
    }

    @Override
    public List<Integer> call() {
      List<Integer> result = new ArrayList<>();
      for (int i = 0; i < polynomial1.getOrder() + polynomial2.getOrder() - 1; i++) {
        result.add(0);
      }
      for (int j = 0; j < polynomial2.getOrder(); j++) {
        result.set(
            indexInPolynomial1 + j,
            polynomial1.getCoefficients().get(indexInPolynomial1)
                * polynomial2.getCoefficients().get(j));
      }
      return result;
    }
  }
}
