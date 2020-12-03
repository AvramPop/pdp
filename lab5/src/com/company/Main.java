package com.company;

import com.company.algorithms.Algorithm;
import com.company.algorithms.Method;
import com.company.algorithms.ParallelKaratsuba;
import com.company.algorithms.ParallelTrivial;
import com.company.algorithms.SequentialKaratsuba;
import com.company.algorithms.SequentialTrivial;

public class Main {
  private static final Method METHOD = Method.PARALLEL;
  private static final Algorithm ALGORITHM = Algorithm.TRIVIAL;
  private static final int NUMBER_OF_THREADS = 4;

  public static void main(String[] args) {
    Polynomial polynomial1 = new Polynomial(2);
    System.out.println("Multiplying");
    System.out.println(polynomial1.toString());
    Polynomial polynomial2 = new Polynomial(3);
    System.out.println("with");
    System.out.println(polynomial2.toString());
    long startTime = System.nanoTime();
    run(polynomial1, polynomial2);
    long stopTime = System.nanoTime();
    double totalTime = ((double) stopTime - (double) startTime) / 1_000_000_000.0;
    System.out.println("Elapsed running time: " + totalTime + "s");
  }

  private static void run(Polynomial polynomial1, Polynomial polynomial2) {
    Polynomial result;
    if (METHOD.equals(Method.SEQUENTIAL)) {
      if (ALGORITHM.equals(Algorithm.TRIVIAL)) {
        result = SequentialTrivial.multiply(polynomial1, polynomial2);
      } else {
        result = SequentialKaratsuba.multiply(polynomial1, polynomial2);
      }
    } else {
      if (ALGORITHM.equals(Algorithm.TRIVIAL)) {
        result = ParallelTrivial.multiply(polynomial1, polynomial2, NUMBER_OF_THREADS);
      } else {
        result = ParallelKaratsuba.multiply(polynomial1, polynomial2, NUMBER_OF_THREADS);
      }
    }
    System.out.println("is");
    System.out.println(result.toString());
  }
}
