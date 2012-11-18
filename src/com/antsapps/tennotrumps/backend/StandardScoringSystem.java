package com.antsapps.tennotrumps.backend;

import java.util.Map;

import com.antsapps.tennotrumps.backend.Bid.Suit;
import com.antsapps.tennotrumps.backend.Bid.Tricks;
import com.google.common.collect.Maps;

class StandardScoringSystem implements ScoringSystem {

  private static final int POINTS_PER_TRICK_WON_BY_LOSING_TEAM = 10;

  private static final int BONUS = 250;

  private final Map<Bid.Suit, Integer> mSuitPoints = Maps
      .newEnumMap(Bid.Suit.class);
  private final Map<Bid.Tricks, Integer> mTricksPoints = Maps
      .newEnumMap(Bid.Tricks.class);

  StandardScoringSystem() {
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

  @Override
  public int calcBiddersScore(Bid bid, int tricksWonByBiddingTeam) {
    int bidValue = mSuitPoints.get(bid.mSuit) + mTricksPoints.get(bid.mTricks);
    return bid.isWinningNumberOfTricks(tricksWonByBiddingTeam) ? bidValue
        : -bidValue;
  }

  @Override
  public int calcNonBiddersScore(Bid bid, int tricksWonByBiddingTeam) {
    return (Bid.TRICKS_PER_HAND - tricksWonByBiddingTeam)
        * POINTS_PER_TRICK_WON_BY_LOSING_TEAM;
  }
}
