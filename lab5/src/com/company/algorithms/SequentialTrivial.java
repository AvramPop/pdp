package com.company.algorithms;

import com.company.Polynomial;

public class SequentialTrivial {

  public static Polynomial multiply(Polynomial polynomial1, Polynomial polynomial2) {
    Polynomial result = new Polynomial();
    for (int i = 0; i < polynomial1.getOrder() + polynomial2.getOrder() - 1; i++) {
      result.getCoefficients().add(0);
    }
    for (int i = 0; i < polynomial1.getOrder(); i++) {
      for (int j = 0; j < polynomial2.getOrder(); j++) {
        int newValue =
            result.getCoefficients().get(i + j)
                + polynomial1.getCoefficients().get(i) * polynomial2.getCoefficients().get(j);
        result.getCoefficients().set(i + j, newValue);
      }
    }
    return result;
  }
}
