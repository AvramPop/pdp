package com.company;

public class Operation {
  public final double amount;
  public final int serialNumber;
  public final int giverUid;
  public final int receiverUid;

  public Operation(double amount, int serialNumber, int giverUid, int receiverUid) {
    this.amount = amount;
    this.serialNumber = serialNumber;
    this.giverUid = giverUid;
    this.receiverUid = receiverUid;
  }
}
