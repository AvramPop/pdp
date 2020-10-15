package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Account {
  private double balance;
  private final double startingBalance;
  private final List<Operation> log;
  public final int uid;

  public Lock lock;

  public Account(int startingBalance, int uid) {
    this.balance = startingBalance;
    this.startingBalance = startingBalance;
    this.uid = uid;
    this.log = new ArrayList<>();
    this.lock = new ReentrantLock();
  }

  public double getBalance() {
    return balance;
  }

  public void deposit(double amount, int giverUid, int receiverUid) {
    balance += amount;
    Operation receiveOperation = new Operation(amount, Bank.getNextOperationNumber(), giverUid, receiverUid);
    addLog(receiveOperation);
//    System.out.println(
//        "OPERATION #"
//        + receiveOperation.serialNumber
//        + " modifying +"
//        + amount
//        + " from account #"
//        + receiverUid);
  }

  public void withdraw(double amount, int giverUid, int receiverUid) {
    balance -= amount;
    double giveAmount = amount * -1;
    Operation giveOperation = new Operation(giveAmount, Bank.getNextOperationNumber(), giverUid, receiverUid);
    addLog(giveOperation);
//    System.out.println(
//        "OPERATION #"
//        + giveOperation.serialNumber
//        + " modifying "
//        + giveAmount
//        + " from account #"
//        + giverUid);
  }

  public boolean isAccountConsistent() {
    double result = startingBalance;
    for(Operation operation : log) {
      result += operation.amount;
    }
    return result == balance;
//    return log.stream().map(operation -> operation.amount).reduce(startingBalance, Double::sum) == balance;
  }

  public List<Operation> getLog() {
    return log;
  }

  public void addLog(Operation operation)
  {
    log.add(operation);
  }
}
