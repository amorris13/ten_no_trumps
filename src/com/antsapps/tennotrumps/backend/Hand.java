package com.antsapps.tennotrumps.backend;

import java.util.Date;

import com.google.common.base.Objects;

public class Hand extends OnStateChangedReporter implements Comparable<Hand> {
  public static final String ID_TAG = "hand_id";

  static final String TAG = "Hand";

  private long id;

  private final Round mRound;
  private Team mBiddingTeam;
  private Player mBiddingPlayer;
  private Bid mBid;
  private int mTricksWonByBiddingTeam = -1;
  private final Date mDate;
  private final int mPointsBiddingTeam;
  private final int mPointsNonBiddingTeam;

  // cached.
  private final boolean bidAchieved;

  // For initialising from mDatabase only!
  Hand(long id,
      Round round,
      Team biddingTeam,
      Player biddingPlayer,
      Bid bid,
      int tricksWon,
      int pointsBiddingTeam,
      int pointsNonBiddingTeam,
      Date date) {
    super();
    this.id = id;
    mRound = round;
    mBiddingTeam = biddingTeam;
    mBiddingPlayer = biddingPlayer;
    mBid = bid;
    mTricksWonByBiddingTeam = tricksWon;
    mPointsBiddingTeam = pointsBiddingTeam;
    mPointsNonBiddingTeam = pointsNonBiddingTeam;
    mDate = date;

    bidAchieved = isBidAchieved();
  }

  public synchronized Team getBiddingTeam() {
    return mBiddingTeam;
  }

  public synchronized void setBiddingTeam(Team biddingTeam) {
    if (biddingTeam != null && !mRound.hasTeam(biddingTeam)) {
      throw new IllegalArgumentException(
          "biddingTeam is not participating in this round");
    }
    mBiddingTeam = biddingTeam;
  }

  public synchronized Player getBiddingPlayer() {
    return mBiddingPlayer;
  }

  public synchronized void setBiddingPlayer(Player biddingPlayer) {
    if (biddingPlayer != null
        && !mRound.getMatch().getPlayers().contains(biddingPlayer)) {
      throw new IllegalArgumentException(
          "biddingPlayer is not participating in this round");
    }
    mBiddingPlayer = biddingPlayer;
  }

  public synchronized Bid getBid() {
    return mBid;
  }

  public synchronized void setBid(Bid bid) {
    mBid = bid;
  }

  public synchronized int getTricksWonByBiddingTeam() {
    return mTricksWonByBiddingTeam;
  }

  public synchronized void
      setTricksWonByBiddingTeam(int tricksWonByBiddingTeam) {
    mTricksWonByBiddingTeam = tricksWonByBiddingTeam;
  }

  public synchronized Round getRound() {
    return mRound;
  }

  public synchronized boolean isBidAchieved() {
    if (mBid == null) {
      return false;
    }
    return mBid.isWinningNumberOfTricks(mTricksWonByBiddingTeam);
  }

  public synchronized Team getWinningTeam() {
    if (mBid.isWinningNumberOfTricks(mTricksWonByBiddingTeam)) {
      return mBiddingTeam;
    } else {
      return mRound.getMatch().getOtherTeam(mBiddingTeam);
    }
  }

  public synchronized int getScore(Team team) {
    if (!mRound.hasTeam(team)) {
      throw new IllegalArgumentException(
          "biddingTeam is not participating in this round");
    }
    return team == mBiddingTeam ? mPointsBiddingTeam : mPointsNonBiddingTeam;
  }

  public synchronized long getId() {
    return id;
  }

  public synchronized void setId(long id) {
    this.id = id;
  }

  public synchronized Date getDate() {
    return mDate != null ? (Date) mDate.clone() : null;
  }

  public synchronized void setDate(long date) {
    mDate.setTime(date);
  }

  @Override
  public synchronized int compareTo(Hand another) {
    return (int) Utils.compareTo(mDate, id, another.mDate, another.id);
  }

  @Override
  public String toString() {
    return Objects.toStringHelper(this).add("id", id)
        .add("mBiddingTeam", mBiddingTeam)
        .add("mBiddingPlayer", mBiddingPlayer).add("mBid", mBid)
        .add("mTricksWonByBiddingTeam", mTricksWonByBiddingTeam)
        .add("mDate", mDate).add("bidAchieved", bidAchieved).toString();
  }

  public int getTricksWon(Team team) {
    if (mBiddingTeam == null) {
      return -1;
    } else {
      if (team == mBiddingTeam) {
        return mTricksWonByBiddingTeam;
      } else {
        return Bid.TRICKS_PER_HAND - mTricksWonByBiddingTeam;
      }
    }
  }
}
