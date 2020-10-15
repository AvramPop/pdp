package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * At a bank, we have to keep track of the balance of some accounts. Also, each account has an
 * associated log (the list of records of operations performed on that account). Each operation
 * record shall have a unique serial number, that is incremented for each operation performed in the
 * bank. We have concurrently run transfer operations, to be executed on multiple threads. Each
 * operation transfers a given amount of money from one account to some other account, and also
 * appends the information about the transfer to the logs of both accounts. From time to time, as
 * well as at the end of the program, a consistency check shall be executed. It shall verify that
 * the amount of money in each account corresponds with the operations records associated to that
 * account, and also that all operations on each account appear also in the logs of the source or
 * destination of the transfer.
 *
 * <p>The problems will require to execute a number of independent operations, that operate on
 * shared data.
 * There shall be several threads launched at the beginning, and each thread shall
 * execute a lot of operations. The operations to be executed are to be randomly chosen, and with
 * randomly chosen parameters.
 * The main thread shall wait for all other threads to end and, then,
 * it shall check that the invariants are obeyed.
 * The operations must be synchronized in order to
 * operate correctly. Write, in a documentation, the rules (which mutex what invariants it
 * protects). You shall play with the number of threads and with the granularity of the locking, in
 * order to asses the performance issues.
 * Document what tests have you done, on what hardware
 * platform, for what size of the data, and what was the time consumed.
 */
public class Main {
  private static volatile boolean stopChecking = false;
  public static void main(String[] args) {
    //test();
    int NUMBER_OF_THREADS = 10;
    int NUMBER_OF_ACCOUNTS = 10000;
    int STEPS = 50000;
    List<Thread> threads = new ArrayList<>();
    Random random = new Random();
    createAccounts(NUMBER_OF_ACCOUNTS, random);
    for (int i = 0; i < NUMBER_OF_THREADS; i++) {
      createBankThread(NUMBER_OF_ACCOUNTS, STEPS, threads, random);
    }
    Thread consistencyCheckThread = getConsistencyCheckThread();
    long startTime = System.nanoTime();
    consistencyCheckThread.start();
    for (Thread thread : threads) {
      thread.start();
    }
    for (Thread thread : threads) {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    stopChecking = true;
    try {
      consistencyCheckThread.join();
    }
    catch (InterruptedException e) {
      e.printStackTrace();
    }
    long stopTime = System.nanoTime();
    consistencyCheck(NUMBER_OF_ACCOUNTS, consistencyCheckThread);
    double totalTime = ((double) stopTime - (double) startTime) / 1_000_000_000.0;
    System.out.println("Elapsed running time: " + totalTime + "s");
  }

  private static void test() {
    Account account1 = new Account(100, 0);
    Account account2 = new Account(100, 1);
    Bank.addAccount(account1);
    Bank.addAccount(account2);
    Bank.doOperation(account1, account2, 50);
    System.out.println(Bank.consistencyCheck());
  }

  private static void consistencyCheck(int NUMBER_OF_ACCOUNTS, Thread consistencyCheckThread) {
    System.out.println("---- END GAME ----");
    consistencyCheckThread.interrupt();
    System.out.println("Bank consistency check: " + Bank.consistencyCheck());
    for (int i = 0; i < NUMBER_OF_ACCOUNTS; i++) {
      //System.out.println("Account #" + i + " has an amount of: " + Bank.getAccount(i).getBalance());
    }
  }

  private static Thread getConsistencyCheckThread() {
    return new Thread(
        () -> {
          while (!stopChecking) {
            try {
              Thread.sleep(100);
            }
            catch (InterruptedException e) {
              break;
            }
            System.out.println("Bank consistency check: " + Bank.consistencyCheck());
          }
        });
  }

  private static void createAccounts(int NUMBER_OF_ACCOUNTS, Random random) {
    System.out.println("---- START GAME ----");
    for (int i = 0; i < NUMBER_OF_ACCOUNTS; i++) {
      int initialBalance = random.nextInt(10000);
      Account account = new Account(initialBalance, i);
      //System.out.println("Created account #" + i + " with initial amount of: " + initialBalance);
      Bank.addAccount(account);
    }
  }

  private static void createBankThread(int NUMBER_OF_ACCOUNTS, int STEPS, List<Thread> threads, Random random) {
    Thread thread =
        new Thread(
            () -> {
              for (int j = 0; j < STEPS; j++) {
                int giver, receiver;
                giver = random.nextInt(NUMBER_OF_ACCOUNTS);
                receiver = random.nextInt(NUMBER_OF_ACCOUNTS);
                // find different giver and receiver
                while (giver == receiver) {
                  receiver = random.nextInt(NUMBER_OF_ACCOUNTS);
                }
                int amount = random.nextInt(10000);
                Bank.doOperation(Bank.getAccount(giver), Bank.getAccount(receiver), amount);
              }
            });
    threads.add(thread);
  }
}
