package com.antsapps.tennotrumps.backend;

interface ScoringSystem {

  int calcBiddersScore(Bid bid, int tricksWonByBiddingTeam);

  int calcNonBiddersScore(Bid bid, int tricksWonByBiddingTeam);
}
