package com.company;

import mpi.MPI;
import mpi.Status;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private static void killAll(int numberOfProcess) {
        for (int i = 1; i < numberOfProcess; ++i) {
            MPI.COMM_WORLD.Send(new int[]{0}, 0, 1, MPI.INT, i, 2);
        }
    }

    private static void master(int n, int numberOfProcesses) {
        List<Integer> solution = new ArrayList<>();
        int count = back(solution, n, 0, numberOfProcesses);
        System.out.println("Count = " + count);
        killAll(numberOfProcesses);
    }


    private static int back(List<Integer> solution, int n, int me, int numberOfProcesses) {
//        System.out.println("doing back on [] " + solution.toString());
        if (solution.size() == n) {
            if (solution.get(0) == 1) {
                System.out.println("solution: " + solution.toString());
                return 1;
            }
            return 0;
        }
        int sum = 0;
        int child = me + numberOfProcesses / 2;
        if (numberOfProcesses >= 2 && child < numberOfProcesses) {
            List<Integer> toSend = new ArrayList<>(solution);
//            System.out.println("master = parent is: " + me + " child is " + child + " [] " + toSend.toString());
            MPI.COMM_WORLD.Send(new int[]{1}, 0, 1, MPI.INT, child, 2);
            MPI.COMM_WORLD.Send(new Object[]{toSend}, 0, 1, MPI.OBJECT, child, 0);
            List<Integer> temp = new ArrayList<>(solution);
//            System.out.println("master = parent is " + me + " local sort " + "[]" + temp.toString());
            for (int i = 0; i < n; i += 2) {
                if (temp.contains(i)) continue;
                temp.add(i);
                sum += back(temp, n, me, numberOfProcesses / 2);
                temp.remove(temp.size() - 1);
            }
            Object[] receivedData = new Object[1];
            MPI.COMM_WORLD.Recv(receivedData, 0, 1, MPI.OBJECT, child, 0);
            sum += (int) receivedData[0];
        } else {
            for (int i = 0; i < n; i++) {
                if (solution.contains(i)) continue;
                solution.add(i);
                sum += back(solution, n, me, 1);
                solution.remove(solution.size() - 1);
            }
        }
//        System.out.println(sum);
        return sum;
    }

    private static void worker(int n, int me, int numberOfProcesses) {
        while (true) {
            int[] alive = new int[1];
            MPI.COMM_WORLD.Recv(alive, 0, 1, MPI.INT, MPI.ANY_SOURCE, 2);
            if (alive[0] == 0) {
                break;
            }
            Object[] receivedData = new Object[1];
            Status status = MPI.COMM_WORLD.Recv(receivedData, 0, 1, MPI.OBJECT, MPI.ANY_SOURCE, 0);
            int parent = status.source;
            List<Integer> array = (List<Integer>) receivedData[0];
            int sum = 0;
            for (int i = 1; i < n; i += 2) {
                if (array.contains(i)) continue;
                array.add(i);
                sum += back(array, n, me, numberOfProcesses); // should this be numberOFProcesses/2?
                array.remove(array.size() - 1);
            }
            MPI.COMM_WORLD.Send(new Object[]{sum}, 0, 1, MPI.OBJECT, parent, 0);
        }
    }

    public static void main(String[] args) {
        MPI.Init(args);
        int selfRank = MPI.COMM_WORLD.Rank();
        int numberOfProcesses = MPI.COMM_WORLD.Size();
        int n = 5;
        if (selfRank == 0) {
            master(n, numberOfProcesses);
        } else {
            worker(n, selfRank, numberOfProcesses);
        }
        MPI.Finalize();
    }
}
