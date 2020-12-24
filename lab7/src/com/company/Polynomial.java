package com.company;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Polynomial implements Serializable {
    private List<Integer> coefficients;

    public Polynomial(List<Integer> coefficients) {
        this.coefficients = coefficients;
    }

    public Polynomial() {
        coefficients = new ArrayList<>();
    }

    public Polynomial(int size) {
        this.coefficients = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            coefficients.add(0);
        }
    }

    public void fillWithRandomNumbers() {
        Random random = new Random();
        for (int i = 0; i < coefficients.size(); i++) {
            coefficients.set(i, random.nextInt(10));
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