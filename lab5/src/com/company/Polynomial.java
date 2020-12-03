package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Polynomial {
  private List<Integer> coefficients;

  public Polynomial(List<Integer> coefficients) {
    this.coefficients = coefficients;
  }

  public Polynomial() {
    coefficients = new ArrayList<>();
  }

  public Polynomial(int size) {
    this.coefficients = new ArrayList<>();
    Random random = new Random();
    for (int i = 0; i < size; i++) {
      coefficients.add(random.nextInt(10));
    }
  }

  public List<Integer> getCoefficients() {
    return coefficients;
  }

  public void setCoefficients(List<Integer> coefficients) {
    this.coefficients = coefficients;
  }

  public int getOrder() {
    return coefficients.size();
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = coefficients.size() - 1; i >= 0; i--) {
      stringBuilder.append(coefficients.get(i)).append("x^").append(i).append(" + ");
    }
    stringBuilder.setLength(stringBuilder.length() - 3);
    return stringBuilder.toString();
  }
}
