package com.company;

import java.util.Random;

public class Matrix {
  public final int rows, columns;
  int[][] matrix;

  public Matrix(int rows, int columns) {
    this.rows = rows;
    this.columns = columns;
    matrix = new int[rows][columns];
  }

  public void populate() {
    Random random = new Random();
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        matrix[i][j] = random.nextInt(10);
      }
    }
  }

  public int get(int row, int column) {
    return matrix[row][column];
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        sb.append(matrix[i][j]).append(" ");
      }
      sb.append("\n");
    }
    return sb.toString();
  }
}
