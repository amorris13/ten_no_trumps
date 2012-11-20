package com.antsapps.tennotrumps.backend;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

public class Round extends OnStateChangedReporter implements Comparable<Round>,
    OnStateChangedListener {
  public static final String ID_TAG = "round_id";

  private static final int WINNING_SCORE = 500;
  private static final int LOSING_SCORE = -500;

  private final List<Hand> mHands = Lists.newArrayList();

  private long id;
  private final Match mMatch;
  private Date mDate;

  // All cached for convenience. These are not necessary to store in the SQLite
  // mDatabase as they are derived from other values.
  private boolean mFinished;
  private Team mWinner;

  public Round(Match match) {
    mMatch = match;
    mDate = new Date(System.currentTimeMillis());
  }

  /**
   * To construct a hand from the mDatabase.
   */
  Round(long id, Match match, Date date){
    this.id = id;
    mMatch = match;
    mDate = date;
  }

  synchronized void addHand(Hand hand) {
    mHands.add(hand);
    hand.addOnStateChangedListener(this);
    updateDate(hand.getDate());
    checkFinished();
    notifyStateChanged();
  }

  synchronized void removeHand(Hand hand) {
    mHands.remove(hand);
    checkFinished();
    notifyStateChanged();
  }

  public synchronized List<Hand> getHands() {
    return Collections.unmodifiableList(mHands);
  }

  public synchronized Hand getHand(long id) {
    for (Hand hand : mHands) {
      if (hand.getId() == id)
        return hand;
    }
    return null;
  }

  public synchronized int getScore(Team team) {
    int score = 0;
    for (Hand hand : mHands) {
      score += hand.getScore(team);
    }
    return score;
  }

  public synchronized int getScoreAfterHand(Team team, Hand uptoHand) {
    int score = 0;
    int indexOfUptoHand = mHands.indexOf(uptoHand);
    for (int i = 0; i <= indexOfUptoHand; i++) {
      score += mHands.get(i).getScore(team);
    }
    return score;
  }

  public synchronized boolean isFinished() {
    return mFinished;
  }

  public synchronized Match getMatch() {
    return mMatch;
  }

  public synchronized Team getWinner() {
    return mWinner;
  }

  public synchronized Date getDate() {
    return mDate != null ? (Date) mDate.clone() : null;
  }

  private Hand getLastHand() {
    return mHands.get(mHands.size() - 1);
  }

  private void checkFinished() {
    mFinished = false;
    hasWon(mMatch.getTeam1());
    hasWon(mMatch.getTeam2());
  }

  private void hasWon(Team team) {
    if (getScore(team) >= WINNING_SCORE
        && getLastHand().getBiddingTeam() == team) {
      mFinished = true;
      mWinner = team;
      return;
    } else if (getScore(team) <= LOSING_SCORE) {
      mFinished = true;
      mWinner = mMatch.getOtherTeam(team);
      return;
    }
  }

  public synchronized boolean hasTeam(Team team) {
    return mMatch.hasTeam(team);
  }

  @Override
  public synchronized int compareTo(Round another) {
    return (int) Utils.compareTo(mDate, id, another.mDate, another.id);
  }

  public synchronized long getId() {
    return id;
  }

  public synchronized void setId(long id) {
    this.id = id;
    notifyStateChanged();
  }

  public synchronized Team getTeam1() {
    return mMatch.getTeam1();
  }

  public synchronized Team getTeam2() {
    return mMatch.getTeam2();
  }

  public synchronized void updateDate(Date date) {
    if(mDate == null || date.compareTo(mDate) > 0) {
      mDate = date;
      mMatch.updateDate(getDate());
      notifyStateChanged();
    }
  }

  @Override
  public synchronized void onStateChanged() {
    checkFinished();
    Collections.sort(mHands);
    notifyStateChanged();
  }

  synchronized void delete() {
    mHands.clear();
    notifyStateChanged();
  }

  public synchronized Team getTeam(int i) {
    switch (i) {
      case 0:
        return getTeam1();
      case 1:
        return getTeam2();
      default:
        return null;
    }
  }

  public synchronized Set<Player> getPlayers() {
    return mMatch.getPlayers();
  }
}
