package com.company;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PolynomialUtils {
  public static Polynomial shiftRight(Polynomial polynomial, int offset) {
    Polynomial result = new Polynomial(polynomial.getOrder() + offset);
    List<Integer> coefficients =
        IntStream.range(0, offset).mapToObj(i -> 0).collect(Collectors.toList());
    coefficients.addAll(polynomial.getCoefficients());
    result.setCoefficients(coefficients);
    return result;
  }

  public static Polynomial add(Polynomial polynomial1, Polynomial polynomial2) {
    int minDegree = Math.min(polynomial1.getOrder(), polynomial2.getOrder());
    int maxDegree = Math.max(polynomial1.getOrder(), polynomial2.getOrder());
    Polynomial result = new Polynomial(maxDegree);
    for (int i = 0; i < minDegree; i++) {
      result
          .getCoefficients()
          .set(i, polynomial1.getCoefficients().get(i) + polynomial2.getCoefficients().get(i));
    }
    Polynomial longest =
        polynomial1.getOrder() > polynomial2.getOrder() ? polynomial1 : polynomial2;
    for (int i = minDegree; i < maxDegree; i++) {
      result.getCoefficients().set(i, longest.getCoefficients().get(i));
    }
    return result;
  }

  public static Polynomial subtract(Polynomial polynomial1, Polynomial polynomial2) {
    int minDegree = Math.min(polynomial1.getOrder(), polynomial2.getOrder());
    int maxDegree = Math.max(polynomial1.getOrder(), polynomial2.getOrder());
    Polynomial result = new Polynomial(maxDegree);
    for (int i = 0; i < minDegree; i++) {
      result
          .getCoefficients()
          .set(i, polynomial1.getCoefficients().get(i) - polynomial2.getCoefficients().get(i));
    }
    Polynomial longest =
        polynomial1.getOrder() > polynomial2.getOrder() ? polynomial1 : polynomial2;
    for (int i = minDegree; i < maxDegree; i++) {
      result.getCoefficients().set(i, longest.getCoefficients().get(i));
    }
    int i = result.getCoefficients().size() - 1;
    while (result.getCoefficients().get(i) == 0 && i > 0) {
      result.getCoefficients().remove(i);
      i--;
    }
    return result;
  }
}
