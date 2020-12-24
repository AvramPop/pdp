package com.company;

public class SequentialKaratsuba {

    // this only works on polynomials of same length
    public static Polynomial multiply(Polynomial polynomial1, Polynomial polynomial2) {

        // Handle the base case where the polynomial has only one coefficient
        if (polynomial1.getOrder() == 1) {
            Polynomial result = new Polynomial(1);
            result
                    .getCoefficients()
                    .set(0, polynomial1.getCoefficients().get(0) * polynomial2.getCoefficients().get(0));
            return result;
        }

        int halfArraySize = polynomial1.getOrder() / 2;

        // Declare arrays to hold halved factors
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

        // Recursively call method on smaller arrays and construct the low and high parts of the product
        Polynomial productLow = multiply(polynomial1Low, polynomial2Low);
        Polynomial productHigh = multiply(polynomial1High, polynomial2High);

        Polynomial productLowHigh = multiply(polynomial1LowHigh, polynomial2LowHigh);

        Polynomial r1 = PolynomialUtils.shiftRight(productHigh, 2 * halfArraySize);
        Polynomial r2 =
                PolynomialUtils.shiftRight(
                        PolynomialUtils.subtract(
                                PolynomialUtils.subtract(productLowHigh, productHigh), productLow),
                        halfArraySize);

        return PolynomialUtils.add(PolynomialUtils.add(r1, r2), productLow);
    }
}