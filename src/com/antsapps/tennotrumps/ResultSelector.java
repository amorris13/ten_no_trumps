package com.antsapps.tennotrumps;

public interface ResultSelector {

  public interface OnTricksWonSelectedListener {
    void onTricksWonSelected(int tricksWon);
  }

  public abstract void init();

  public abstract void setOnTricksWonSelectedListener(
      OnTricksWonSelectedListener listener);

  public abstract int getTricksWon();

}