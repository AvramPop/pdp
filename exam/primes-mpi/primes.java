package com.company;

import mpi.MPI;

import java.util.*;
import java.util.stream.IntStream;

public class Main {

    private static void primes(int maxN, int numberOfProcesses, List<Integer> primesToSqrtN) {
        List<Integer> listOfPrimes = new ArrayList<>();
        for (int i = 1; i < numberOfProcesses; i++) {
            MPI.COMM_WORLD.Send(new Object[]{primesToSqrtN, maxN}, 0, 2, MPI.OBJECT, i, 0);
        }
        Object[] results = new Object[numberOfProcesses - 1];
        for (int i = 1; i < numberOfProcesses; i++) {
            MPI.COMM_WORLD.Recv(results, i - 1, 1, MPI.OBJECT, i, 0);
        }
        for (Object result :
                results) {
            listOfPrimes.addAll((List<Integer>) result);
        }
        Collections.sort(listOfPrimes);
        System.out.println(listOfPrimes.toString());
    }

    private static void worker(int me, int numberOfProcesses) {
        Object[] receivedData = new Object[2];

        MPI.COMM_WORLD.Recv(receivedData, 0, 2, MPI.OBJECT, 0, 0);
        List<Integer> primes = (List<Integer>) receivedData[0];
        Integer maxN = (Integer) receivedData[1];
        Integer sqrt = primes.get(primes.size() - 1);
        int chunk = (maxN - sqrt + 1) / (numberOfProcesses - 1);
        int start = sqrt + chunk * (me - 1);
        int end = sqrt + chunk * me - 1;
        if (me == numberOfProcesses - 1) {
            end = end > maxN ? end : maxN;
        }

        List<Integer> result = new ArrayList<>();
        for (int numberToTest = start; numberToTest <= end; numberToTest++) {
            boolean ok = true;
            for (Integer value : primes) {
                if (numberToTest % value == 0) {
                    ok = false;
                }
            }
            if (ok) {
                result.add(numberToTest);
            }
        }

        MPI.COMM_WORLD.Send(new Object[]{result}, 0, 1, MPI.OBJECT, 0, 0);
    }

    public static void main(String[] args) {
        MPI.Init(args);
        int selfRank = MPI.COMM_WORLD.Rank();
        int numberOfProcesses = MPI.COMM_WORLD.Size();
        if (selfRank == 0) {
            primes(400, numberOfProcesses, Arrays.asList(2, 3, 5, 7, 11, 13, 17, 19));
        } else {
            worker(selfRank, numberOfProcesses);
        }
        MPI.Finalize();
    }
}

