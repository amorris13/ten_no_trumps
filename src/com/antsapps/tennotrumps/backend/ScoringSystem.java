package com.antsapps.tennotrumps.backend;

import java.util.Map;

import com.antsapps.tennotrumps.backend.Bid.Suit;
import com.antsapps.tennotrumps.backend.Bid.Tricks;
import com.google.common.collect.Maps;

public class ScoringSystem {

  private static final int POINTS_PER_TRICK_WON_BY_LOSING_TEAM = 10;

  private static final int BONUS = 250;

  private boolean mAwardBonus;

  private String mNonBiddingPoints;

  private final Map<Bid.Suit, Integer> mSuitPoints = Maps
      .newEnumMap(Bid.Suit.class);
  private final Map<Bid.Tricks, Integer> mTricksPoints = Maps
      .newEnumMap(Bid.Tricks.class);

  static ScoringSystem createDefaultScoringSystem() {
    return new ScoringSystem(true, "always");
  }

  ScoringSystem(boolean awardBonus, String nonBiddingPoints) {
    mAwardBonus = awardBonus;
    mNonBiddingPoints = nonBiddingPoints;

    mSuitPoints.put(Suit.SPADES, 40);
    mSuitPoints.put(Suit.CLUBS, 60);
    mSuitPoints.put(Suit.DIAMONDS, 80);
    mSuitPoints.put(Suit.HEARTS, 100);
    mSuitPoints.put(Suit.NOTRUMPS, 120);
    mSuitPoints.put(Suit.CLOSEDMISERE, 250);
    mSuitPoints.put(Suit.OPENMISERE, 500);

    mTricksPoints.put(Tricks.ZERO, 0);
    mTricksPoints.put(Tricks.SIX, 0);
    mTricksPoints.put(Tricks.SEVEN, 100);
    mTricksPoints.put(Tricks.EIGHT, 200);
    mTricksPoints.put(Tricks.NINE, 300);
    mTricksPoints.put(Tricks.TEN, 400);
  }

  public int calcBiddersScore(Bid bid, int tricksWonByBiddingTeam) {
    int bidValue = mSuitPoints.get(bid.mSuit) + mTricksPoints.get(bid.mTricks);
    if (bid.isWinningNumberOfTricks(tricksWonByBiddingTeam)) {
      if (mAwardBonus && tricksWonByBiddingTeam == Bid.TRICKS_PER_HAND) {
        return Math.max(BONUS, bidValue);
      }
      return bidValue;
    } else {
      return -bidValue;
    }
  }

  public int calcNonBiddersScore(Bid bid, int tricksWonByBiddingTeam) {
    if (shouldNonBiddersReceivePoints(bid, tricksWonByBiddingTeam)) {
      if (bid.mTricks == Tricks.ZERO) {
        // No points for non bidding team in misere bids.
        return 0;
      } else {
        return (Bid.TRICKS_PER_HAND - tricksWonByBiddingTeam)
            * POINTS_PER_TRICK_WON_BY_LOSING_TEAM;
      }
    } else {
      return 0;
    }
  }

  private boolean shouldNonBiddersReceivePoints(Bid bid,
      int tricksWonByBiddingTeam) {
    return mNonBiddingPoints.equals("always")
        || (mNonBiddingPoints.equals("sometimes") && !bid
            .isWinningNumberOfTricks(tricksWonByBiddingTeam));
  }

  void setAwardBonus(boolean awardBonus) {
    this.mAwardBonus = awardBonus;
  }

  void setNonBiddingPoints(String nonBiddingPoints) {
    this.mNonBiddingPoints = nonBiddingPoints;
  }
}
