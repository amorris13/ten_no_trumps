package com.antsapps.tennotrumps.backend;

public interface ScoringSystem {

  int calcBiddersScore(Bid bid, int tricksWonByBiddingTeam);

  int calcNonBiddersScore(Bid bid, int tricksWonByBiddingTeam);
}
