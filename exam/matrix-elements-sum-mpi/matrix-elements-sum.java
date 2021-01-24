package com.company;

import mpi.MPI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    private static void master(List<List<Integer>> matrix, int numberOfProcesses) {
        List<Integer> flatten = new ArrayList<>();
        matrix.forEach(flatten::addAll);
        int sum = back(flatten, 0, numberOfProcesses);
        System.out.println("Sum = " + sum);
    }


    private static int back(List<Integer> input, int me, int numberOfProcesses) {
//        System.out.println(input.toString());
        if (input.size() == 1) return input.get(0);
        int sum1, sum2;
        if (numberOfProcesses >= 2) {
            int child = me + numberOfProcesses / 2;

            List<Integer> toSend = new ArrayList<>(input.subList(0, input.size() / 2));
            MPI.COMM_WORLD.Send(new Object[]{toSend}, 0, 1, MPI.OBJECT, child, 0);
//            System.out.println("master = parent is: " + me + " child is " + child + " [] " + toSend.toString());
            List<Integer> temp = new ArrayList<>(input.subList(input.size() / 2, input.size()));
            sum1 = back(temp, me, numberOfProcesses / 2);
//            System.out.println("master = parent is " + me + " local sort " + "[]" + temp.toString());
            Object[] receivedData = new Object[1];
            MPI.COMM_WORLD.Recv(receivedData, 0, 1, MPI.OBJECT, child, 0);
            sum2 = (int) receivedData[0];
//            System.out.println(me + " back sum " + sum2);
        } else {
            sum1 = back(input.subList(0, input.size() / 2), me, 1);
            sum2 = back(input.subList(input.size() / 2, input.size()), me, 1);
//            System.out.println(me + " simple sum " + sum);

        }
//        System.out.println(sum);
        return sum1 + sum2;
    }

    private static void worker(int me, int numberOfProcesses) {
        int base = 0;
        int parent = -1;
        int offset = me;
        while (offset > 0) {
            parent = base;
            int mid = numberOfProcesses / 2;
            if (offset < mid) {
                numberOfProcesses = numberOfProcesses / 2;
            } else {
                offset = offset - mid;
                numberOfProcesses = numberOfProcesses / 2 + numberOfProcesses % 2;
                base += mid;
            }
        }
        Object[] receivedData = new Object[1];
        MPI.COMM_WORLD.Recv(receivedData, 0, 1, MPI.OBJECT, parent, 0);
        List<Integer> array = (List<Integer>) receivedData[0];
        int sum = back(array, me, numberOfProcesses);
        MPI.COMM_WORLD.Send(new Object[]{sum}, 0, 1, MPI.OBJECT, parent, 0);
    }

    public static void main(String[] args) {
        MPI.Init(args);
        int selfRank = MPI.COMM_WORLD.Rank();
        int numberOfProcesses = MPI.COMM_WORLD.Size();
        if (selfRank == 0) {
            master(Arrays.asList(Arrays.asList(1, 2, 3), Arrays.asList(4, 5, 6), Arrays.asList(7, 8, 9)), numberOfProcesses);
        } else {
            worker(selfRank, numberOfProcesses);
        }
        MPI.Finalize();
    }
}

