package com.antsapps.tennotrumps.backend;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.util.Log;

import com.google.common.collect.Lists;

public class Match extends OnStateChangedReporter implements Comparable<Match>,
    OnStateChangedListener {
  public static final String ID_TAG = "match_id";

  private long id;

  private Team mTeam1;
  private Team mTeam2;
  private final List<Round> mRounds = Lists.newArrayList();
  private Date mDate = null;

  private boolean mFinished = false;

  public Match(Team team1, Team team2) {
    mTeam1 = team1;
    mTeam2 = team2;
    mDate = new Date(System.currentTimeMillis());
  }

  /**
   * To construct a hand from the database.
   */
  Match(long id, Team team1, Team team2, Date date) {
    this.id = id;
    mTeam1 = team1;
    mTeam2 = team2;
    mDate = date;
  }

  public Date getDate() {
    Log.i("Match", "mDate: " + mDate);
    return mDate != null ? (Date) mDate.clone() : null;
  }

  /** Assumes only two teams */
  public Team getOtherTeam(Team team) {
    if (team == mTeam1) {
      return mTeam2;
    } else if (team == mTeam2) {
      return mTeam1;
    } else {
      throw new IllegalArgumentException();
    }
  }

  void addRound(Round round) {
    mRounds.add(round);
    round.addOnStateChangedListener(this);
    updateDate(round.getDate());
    notifyStateChanged();
  }

  void removeRound(Round round) {
    mRounds.remove(round);
    checkFinished();
    notifyStateChanged();
  }

  public List<Round> getRounds() {
    return Collections.unmodifiableList(mRounds);
  }

  public Round getRound(long id) {
    for (Round round : mRounds) {
      if (round.getId() == id)
        return round;
    }
    return null;
  }

  public Set<Player> getPlayers() {
    Set<Player> players = new HashSet<Player>();
    players.addAll(mTeam1.getPlayers());
    players.addAll(mTeam2.getPlayers());
    return Collections.unmodifiableSet(players);
  }

  public int getRoundsWon(Team team) {
    int numWon = 0;
    for (Round round : mRounds) {
      if (round.getWinner() == team) {
        numWon++;
      }
    }
    return numWon;
  }

  @Override
  public int compareTo(Match another) {
    return (int) Utils.compareTo(mDate, id, another.mDate, another.id);
  }

  @Override
  public String toString() {
    return "Match [id=" + getId() + ", mDate=" + mDate + "]";
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public boolean hasTeam(Team team) {
    return team == mTeam1 || team == mTeam2;
  }

  public Team getTeam1() {
    return mTeam1;
  }

  public Team getTeam2() {
    return mTeam2;
  }

  public void setTeam(int i, Team team) {
    switch (i) {
      case 0:
        mTeam1 = team;
        notifyStateChanged();
        break;
      case 1:
        mTeam2 = team;
        notifyStateChanged();
        break;
      default:
        break;
    }
  }

  public Team getTeam(int i) {
    switch (i) {
      case 0:
        return mTeam1;
      case 1:
        return mTeam2;
      default:
        return null;
    }
  }

  public void updateDate(Date date) {
    if (mDate == null || date.compareTo(mDate) > 0) {
      mDate = date;
    }
    notifyStateChanged();
    Log.i("Match", "updateDate, mDate is " + mDate == null ? "null"
        : "not null");
  }

  @Override
  public void onStateChanged() {
    Collections.sort(mRounds);
    checkFinished();
    notifyStateChanged();
  }

  void delete() {
    for (Round round : mRounds) {
      round.delete();
    }
    mRounds.clear();
  }

  private void checkFinished() {
    boolean finished = true;
    for (Round round : mRounds) {
      finished &= round.isFinished();
    }
    mFinished = finished;
  }

  public boolean isFinished() {
    return mFinished;
  }
}
