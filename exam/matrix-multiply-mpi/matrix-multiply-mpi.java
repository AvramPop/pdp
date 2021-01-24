package com.company;

import mpi.MPI;

import java.util.*;

public class Main {

    private static void master(List<List<Integer>> matrix1, List<List<Integer>> matrix2, int numberOfProcesses) {
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < matrix1.size(); i++) {
            result.add(new ArrayList<>(Collections.nCopies(matrix2.get(0).size(), 0)));
        }
        int numberOfElementsInMatrix = result.size() * result.get(0).size();
        for (int i = 1; i < MPI.COMM_WORLD.Size(); i++) {
            int index = i - 1;
            int setSize = numberOfElementsInMatrix / (MPI.COMM_WORLD.Size() - 1);
            int startingLine = setSize * index / result.size();
            int startingColumn = setSize * index % result.size();
            if (index == MPI.COMM_WORLD.Size() - 2) setSize += numberOfElementsInMatrix % (MPI.COMM_WORLD.Size() - 1);
            MPI.COMM_WORLD.Send(new Object[]{matrix1, matrix2, startingLine, startingColumn, setSize}, 0, 5, MPI.OBJECT, i, 0);
        }
        for (int i = 1; i < MPI.COMM_WORLD.Size(); i++) {
            Object[] received = new Object[1];
            MPI.COMM_WORLD.Recv(received, 0, 1, MPI.OBJECT, i, 0);
            List<List<Integer>> data = (List<List<Integer>>) received[0];
            for (int j = 0; j < result.size(); j++) {
                for (int k = 0; k < result.get(0).size(); k++) {
                    result.get(j).set(k, result.get(j).get(k) + data.get(j).get(k));
                }
            }
        }
        for (int i = 0; i < result.size(); i++) {
            for (int j = 0; j < result.get(0).size(); j++) {
                System.out.print(result.get(i).get(j) + " ");
            }
            System.out.println();
        }
    }

    private static void worker() {

        Object[] receivedData = new Object[5];
        MPI.COMM_WORLD.Recv(receivedData, 0, 5, MPI.OBJECT, 0, 0);
        List<List<Integer>> matrix1 = (List<List<Integer>>) receivedData[0];
        List<List<Integer>> matrix2 = (List<List<Integer>>) receivedData[1];
        int startingLine = (int) receivedData[2];
        int startingColumn = (int) receivedData[3];
        int setSize = (int) receivedData[4];
        List<List<Integer>> result = new ArrayList<>();
        for (int i = 0; i < matrix1.size(); i++) {
            result.add(new ArrayList<>(Collections.nCopies(matrix2.get(0).size(), 0)));
        }
        int i = startingLine, j = startingColumn;
        int count = setSize;
        List<AbstractMap.SimpleEntry> elements = new ArrayList<>();
        while (count > 0 && i < result.size() && j < result.get(0).size()) {
            elements.add(new AbstractMap.SimpleEntry<>(i, j));
            j++;
            count--;
            if (j == result.size()) {
                j = 0;
                i++;
            }
        }
        for (AbstractMap.SimpleEntry<Integer, Integer> element : elements) {
            int row = element.getKey();
            int column = element.getValue();
            result.get(row).set(column, computeElement(matrix1, row, matrix2, column));
        }
        MPI.COMM_WORLD.Send(new Object[]{result}, 0, 1, MPI.OBJECT, 0, 0);
    }

    public static int computeElement(List<List<Integer>> matrixA, int row, List<List<Integer>> matrixB, int column) {
        int result = 0;
        if (row < matrixA.size() && column < matrixB.get(0).size()) {
            int i = 0;
            while (i < matrixA.get(0).size()) {
                result += matrixA.get(row).get(i) * matrixB.get(i).get(column);
                i++;
            }
            return result;
        } else {
            throw new IllegalArgumentException();
        }
    }

    public static void main(String[] args) {
        MPI.Init(args);
        int selfRank = MPI.COMM_WORLD.Rank();
        int numberOfProcesses = MPI.COMM_WORLD.Size();
        if (selfRank == 0) {
            master(Arrays.asList(Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6)), Arrays.asList(Arrays.asList(5, 6), Arrays.asList(7, 8), Arrays.asList(9, 10)), numberOfProcesses);
        } else {
            worker();
        }
        MPI.Finalize();
    }
}

