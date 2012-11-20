package com.antsapps.tennotrumps.backend;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

import com.antsapps.tennotrumps.R;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Application extends OnStateChangedReporter implements
    OnStateChangedListener, OnSharedPreferenceChangeListener {
  private static Application instance;

  private final Context mContext;

  /** Should remain sorted */
  private final List<Match> mMatches;

  private final Map<Long, Player> mPlayers;
  private final Map<Long, Team> mTeams;

  private final ScoringSystem mScoringSystem;

  public final DBAdapter mDatabase;

  private Application(Context context) {
    super();
    mContext = context;
    mMatches = Lists.newArrayList();
    mPlayers = Maps.newHashMap();
    mTeams = Maps.newHashMap();

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    mScoringSystem = new ScoringSystem(
        prefs.getBoolean(context.getString(R.string.pref_bonus), true),
        prefs.getString(context.getString(R.string.pref_points_nonbidders), "always"));
    prefs.registerOnSharedPreferenceChangeListener(this);

    mDatabase = new DBAdapter(context);
    mDatabase.initialize(mMatches, mPlayers, mTeams);
    for (Match match : mMatches) {
      match.addOnStateChangedListener(this);
    }
    onStateChanged();
    notifyStateChanged();
  }

  public static Application getInstance(Context context) {
    if (instance == null || instance.mContext != context) {
      instance = new Application(context);
    }
    return instance;
  }

  public ScoringSystem getScoringSystem() {
    return mScoringSystem;
  }

  public void addPlayer(Player player) {
    Preconditions.checkArgument(
        getPlayer(player.getName()) == null,
        "There is already a player with the name " + player.getName());
    mDatabase.addPlayer(player);
    mPlayers.put(player.getId(), player);
    notifyStateChanged();
  }

  public Collection<Player> getPlayers() {
    return mPlayers.values();
  }

  public Player getPlayer(long id) {
    return mPlayers.get(id);
  }

  public Player getPlayer(String playerName) {
    for (Player player : mPlayers.values()) {
      if (player.getName().equals(playerName)) {
        return player;
      }
    }
    return null;
  }

  public void addTeam(Team team) {
    Preconditions.checkArgument(
        getTeam(team.getName()) == null,
        "Team with name " + team.getName() + "already exists");
    mDatabase.addTeam(team);
    mTeams.put(team.getId(), team);
    notifyStateChanged();
  }

  public Collection<Team> getTeams() {
    return mTeams.values();
  }

  public Team getTeam(long id) {
    return mTeams.get(id);
  }

  public Team getTeam(String teamName) {
    for (Team team : mTeams.values()) {
      if (team.getName().equals(teamName)) {
        return team;
      }
    }
    return null;
  }

  public void addMatch(Match match) {
    mDatabase.addMatch(match);
    match.addOnStateChangedListener(this);
    mMatches.add(match);
    notifyStateChanged();
  }

  public void deleteMatch(Match match) {
    ImmutableList<Round> rounds = ImmutableList.copyOf(match.getRounds());
    for(Round round : rounds){
      deleteRound(round);
    }
    match.delete();
    mMatches.remove(match);
    mDatabase.removeMatch(match);
    notifyStateChanged();
  }

  public List<Match> getMatches() {
    return Collections.unmodifiableList(mMatches);
  }

  public Match getMatch(long id) {
    for (Match match : mMatches) {
      if (match.getId() == id) {
        return match;
      }
    }
    return null;
  }

  public void addRound(Round round) {
    Preconditions.checkArgument(
        round.getMatch() != null,
        "Match for round must be specified.");
    mDatabase.addRound(round);
    round.getMatch().addRound(round);
  }

  public void deleteRound(Round round) {
    ImmutableList<Hand> hands = ImmutableList.copyOf(round.getHands());
    for(Hand hand : hands) {
      deleteHand(hand);
    }
    mDatabase.removeRound(round);
    round.getMatch().removeRound(round);
  }

  public Hand addHand(Round round, Bid bid, Team biddingTeam,
      Player biddingPlayer, int tricksWonByBiddingTeam) {
    Hand hand = mDatabase.addHand(
        round,
        bid,
        biddingTeam,
        biddingPlayer,
        tricksWonByBiddingTeam,
        mScoringSystem.calcBiddersScore(bid, tricksWonByBiddingTeam),
        mScoringSystem.calcNonBiddersScore(bid, tricksWonByBiddingTeam));
    round.addHand(hand);
    return hand;
  }

  public void deleteHand(Hand hand) {
    mDatabase.removeHand(hand);
    hand.getRound().removeHand(hand);
  }

  @Override
  public void onStateChanged() {
    Collections.sort(mMatches);
    notifyStateChanged();
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
      String key) {
    if (key.equals(mContext.getString(R.string.pref_bonus))) {
      mScoringSystem.setAwardBonus(sharedPreferences.getBoolean(key, true));
    } else if (key.equals(mContext.getString(R.string.pref_points_nonbidders))) {
      mScoringSystem.setNonBiddingPoints(sharedPreferences.getString(key, "always"));
    }
  }
}
