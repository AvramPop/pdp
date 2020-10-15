package com.company;

import java.util.ArrayList;
import java.util.List;

public class Bank {
  private static int operationNumber = 1;
  private static final List<Account> accounts = new ArrayList<>();

  public static void addAccount(Account account) {
    accounts.add(account);
  }

  public static void doOperation(Account giverAccount, Account receiverAccount, int amount) {
    // mechanism to avoid deadlocks
//    System.out.println("Trying operation from account " + giverAccount.uid + " to account " + receiverAccount.uid + " with amount: " + amount);
    if (giverAccount.uid < receiverAccount.uid) {
      giverAccount.lock.lock();
      receiverAccount.lock.lock();
    } else {
      receiverAccount.lock.lock();
      giverAccount.lock.lock();
    }
    if (amount <= giverAccount.getBalance()) {
      giverAccount.withdraw(amount, giverAccount.uid, receiverAccount.uid);
      receiverAccount.deposit(amount, giverAccount.uid, receiverAccount.uid);
    }
    giverAccount.lock.unlock();
    receiverAccount.lock.unlock();
  }

  public static Account getAccount(int uid) {
    for (Account account : accounts) {
      if (account.uid == uid) {
        return account;
      }
    }
    return null;
  }

  public static synchronized int getNextOperationNumber() {
    operationNumber++;
    return operationNumber - 1;
  }

  public static boolean consistencyCheck() {
    boolean result = true;
    for (Account account : accounts) {
      account.lock.lock();
      result = result && account.isAccountConsistent();
      for (Operation operation : account.getLog()) {
        boolean found = false;
        if (operation.giverUid == account.uid) {
          for (int i = 0; i < accounts.get(operation.receiverUid).getLog().size(); i++) {
            if (accounts.get(operation.receiverUid).getLog().get(i).giverUid == operation.giverUid) {
              found = true;
              break;
            }
          }
        } else {
          for (int i = 0; i < accounts.get(operation.giverUid).getLog().size(); i++) {
            if (accounts.get(operation.giverUid).getLog().get(i).receiverUid == operation.receiverUid) {
              found = true;
              break;
            }
          }
        }
        result = result && found;
      }
      account.lock.unlock();
    }
    // accounts.get(operation.giverUid).getLog().stream().anyMatch(log -> log.receiverUid == operation.receiverUid);
    // accounts.get(operation.receiverUid).getLog().stream().anyMatch(log -> log.giverUid == operation.giverUid);
    return result;
  }
}
