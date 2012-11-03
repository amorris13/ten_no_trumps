package com.antsapps.tennotrumps.backend;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import android.app.backup.BackupManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.common.collect.Maps;

public class DBAdapter extends SQLiteOpenHelper {
  /** The name of the database file on the file system */
  public static final String DATABASE_NAME = "TenNoTrumps.db";
  /**
   * The version of the database that this class understands. Version history:
   * 1: Initial version 2: Adds columns for points for each hand.
   */
  private static final int DATABASE_VERSION = 2;

  public static final String TABLE_MATCHES = "matches";
  public static final String TABLE_ROUNDS = "rounds";
  public static final String TABLE_HANDS = "hands";
  public static final String TABLE_PLAYERS = "players";
  public static final String TABLE_TEAMS = "teams";

  public static final String COLUMN_MATCH_ID = "match_id";
  public static final String COLUMN_PLAYER_ID = "player_id";
  public static final String COLUMN_TEAM_ID = "team_id";
  public static final String COLUMN_ROUND_ID = "round_id";
  public static final String COLUMN_HAND_ID = "hand_id";
  public static final String COLUMN_PLAYER_NAME = "player_name";
  public static final String COLUMN_TEAM_NAME = "team_name";
  public static final String COLUMN_PLAYER_1 = "player_1";
  public static final String COLUMN_PLAYER_2 = "player_2";
  public static final String COLUMN_TEAM_1 = "team_1";
  public static final String COLUMN_TEAM_2 = "team_2";
  public static final String COLUMN_BIDDING_TEAM_ID = "bidding_team_id";
  public static final String COLUMN_BIDDING_PLAYER_ID = "bidding_player_id";
  public static final String COLUMN_BID = "bid";
  public static final String COLUMN_TRICKS_WON = "tricks_won";
  public static final String COLUMN_DATE = "date";
  public static final String COLUMN_POINTS_WINNING_TEAM = "points_winning_team";
  public static final String COLUMN_POINTS_LOSING_TEAM = "points_losing_team";

  private static final String CREATE_HANDS = "CREATE TABLE " + TABLE_HANDS
      + "(" + COLUMN_HAND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " //
      + COLUMN_ROUND_ID + " INTEGER, " //
      + COLUMN_BIDDING_TEAM_ID + " INTEGER, " //
      + COLUMN_BIDDING_PLAYER_ID + " INTEGER, " //
      + COLUMN_BID + " TEXT, " //
      + COLUMN_TRICKS_WON + " INTEGER, " //
      + COLUMN_POINTS_WINNING_TEAM + " INTEGER, " //
      + COLUMN_POINTS_LOSING_TEAM + " INTEGER, " //
      + COLUMN_DATE + " INTEGER)";
  private static final String CREATE_ROUNDS = "CREATE TABLE " + TABLE_ROUNDS
      + "(" + COLUMN_ROUND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      + COLUMN_MATCH_ID + " INTEGER, " //
      + COLUMN_DATE + " INTEGER)";
  private static final String CREATE_MATCHES = "CREATE TABLE " + TABLE_MATCHES
      + "(" + COLUMN_MATCH_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      + COLUMN_TEAM_1 + " INTEGER, " + COLUMN_TEAM_2 + " INTEGER, " //
      + COLUMN_DATE + " INTEGER)";
  private static final String CREATE_TEAMS = "CREATE TABLE " + TABLE_TEAMS
      + "(" + COLUMN_TEAM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      + COLUMN_TEAM_NAME + " TEXT, " + COLUMN_PLAYER_1 + " INTEGER, "
      + COLUMN_PLAYER_2 + " INTEGER)";
  private static final String CREATE_PLAYERS = "CREATE TABLE " + TABLE_PLAYERS
      + "(" + COLUMN_PLAYER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
      + COLUMN_PLAYER_NAME + " TEXT)";

  private final Context mContext;

  /** Constructor */
  public DBAdapter(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    mContext = context;
  }

  /**
   * Execute all of the SQL statements in the String[] array
   *
   * @param db
   *          The database on which to execute the statements
   * @param sql
   *          An array of SQL statements to execute
   */
  private void execMultipleSQL(SQLiteDatabase db, String[] sql) {
    for (String s : sql) {
      if (s.trim().length() > 0) {
        db.execSQL(s);
      }
    }
  }

  /** Called when it is time to create the database */
  @Override
  public void onCreate(SQLiteDatabase db) {
    Log.i("DBAdaptor", "onCreate");
    String[] sql = new String[] { CREATE_HANDS, CREATE_ROUNDS, CREATE_MATCHES,
        CREATE_TEAMS, CREATE_PLAYERS };
    db.beginTransaction();
    try {
      // Create tables & test data
      execMultipleSQL(db, sql);
      db.setTransactionSuccessful();
    } catch (SQLException e) {
      Log.e("Error creating tables and debug data", e.toString());
    } finally {
      db.endTransaction();
    }
  }

  /** Called when the database must be upgraded */
  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    Log.w(DATABASE_NAME, "Upgrading database from version " + oldVersion
        + " to " + newVersion);
    if (oldVersion == 1 && newVersion == 2) {
      db.beginTransaction();
      try {
        // Create tables & test data
        execMultipleSQL(db, new String[] {
            "ALTER TABLE " + TABLE_HANDS + " ADD COLUMN "
                + COLUMN_POINTS_WINNING_TEAM,
            "ALTER TABLE " + TABLE_HANDS + " ADD COLUMN "
                + COLUMN_POINTS_LOSING_TEAM });
        db.setTransactionSuccessful();
      } catch (SQLException e) {
        Log.e("Error creating tables and debug data", e.toString());
      } finally {
        db.endTransaction();
      }

      // Fill in details of previous games.

    }
  }

  public void initialize(List<Match> matches, Map<Long, Player> players,
      Map<Long, Team> teams) {
    Log.i("DBAdapter", "initialize");
    // Clear everything
    matches.clear();
    players.clear();
    teams.clear();

    // Do players first.
    Cursor playersCursor = getWritableDatabase().query(
        TABLE_PLAYERS,
        new String[] { COLUMN_PLAYER_ID, COLUMN_PLAYER_NAME },
        null,
        null,
        null,
        null,
        null);
    playersCursor.moveToFirst();
    while (!playersCursor.isAfterLast()) {
      Player player = cursorToPlayer(playersCursor);
      players.put(player.getId(), player);
      playersCursor.moveToNext();
    }
    playersCursor.close();

    // Then do teams.
    Cursor teamsCursor = getWritableDatabase().query(
        TABLE_TEAMS,
        new String[] { COLUMN_TEAM_ID, COLUMN_TEAM_NAME, COLUMN_PLAYER_1,
            COLUMN_PLAYER_2 },
        null,
        null,
        null,
        null,
        null);
    teamsCursor.moveToFirst();
    while (!teamsCursor.isAfterLast()) {
      Team team = cursorToTeam(teamsCursor, players);
      teams.put(team.getId(), team);
      teamsCursor.moveToNext();
    }
    teamsCursor.close();

    // Do matches.
    Map<Long, Match> matchesMap = Maps.newHashMap();
    Cursor matchesCursor = getWritableDatabase().query(
        TABLE_MATCHES,
        new String[] { COLUMN_MATCH_ID, COLUMN_TEAM_1, COLUMN_TEAM_2,
            COLUMN_DATE },
        null,
        null,
        null,
        null,
        null);
    matchesCursor.moveToFirst();
    while (!matchesCursor.isAfterLast()) {
      Match match = cursorToMatch(matchesCursor, teams);
      matches.add(match);
      matchesMap.put(match.getId(), match);
      matchesCursor.moveToNext();
    }
    matchesCursor.close();

    // Do rounds
    Map<Long, Round> rounds = Maps.newHashMap();
    Cursor roundsCursor = getWritableDatabase().query(
        TABLE_ROUNDS,
        new String[] { COLUMN_ROUND_ID, COLUMN_MATCH_ID, COLUMN_DATE },
        null,
        null,
        null,
        null,
        null);
    roundsCursor.moveToFirst();
    while (!roundsCursor.isAfterLast()) {
      Round round = cursorToRound(roundsCursor, matchesMap);
      rounds.put(round.getId(), round);
      roundsCursor.moveToNext();
    }
    roundsCursor.close();

    // Do Hands
    Cursor handsCursor = getWritableDatabase()
        .query(
            TABLE_HANDS,
            new String[] { COLUMN_HAND_ID, COLUMN_ROUND_ID,
                COLUMN_BIDDING_TEAM_ID, COLUMN_BIDDING_PLAYER_ID, COLUMN_BID,
                COLUMN_TRICKS_WON, COLUMN_POINTS_WINNING_TEAM,
                COLUMN_POINTS_LOSING_TEAM, COLUMN_DATE },
            null,
            null,
            null,
            null,
            null);
    handsCursor.moveToFirst();
    while (!handsCursor.isAfterLast()) {
      cursorToHand(handsCursor, rounds, teams, players);
      handsCursor.moveToNext();
    }
    handsCursor.close();

    Collections.sort(matches);
  }

  private static Player cursorToPlayer(Cursor cursor) {
    Player player = new Player(cursor.getString(1));
    player.setId(cursor.getLong(0));
    return player;
  }

  private static Team
      cursorToTeam(Cursor teamsCursor, Map<Long, Player> players) {
    Team team = new Team(teamsCursor.getString(1));
    team.setId(teamsCursor.getLong(0));
    team.setPlayer1(players.get(teamsCursor.getLong(2)));
    team.setPlayer2(players.get(teamsCursor.getLong(3)));
    return team;
  }

  private static Match
      cursorToMatch(Cursor matchesCursor, Map<Long, Team> teams) {
    long id = matchesCursor.getLong(0);
    Team team1 = teams.get(matchesCursor.getLong(1));
    Team team2 = teams.get(matchesCursor.getLong(2));
    Date date = new Date(matchesCursor.getLong(3));
    return new Match(id, team1, team2, date);
  }

  private static Round cursorToRound(Cursor roundsCursor,
      Map<Long, Match> matches) {
    long id = roundsCursor.getLong(0);
    Match match = matches.get(roundsCursor.getLong(1));
    Date date = new Date(roundsCursor.getLong(2));
    Round round = new Round(id, match, date);
    match.addRound(round);
    return round;
  }

  private static Hand cursorToHand(Cursor handsCursor, Map<Long, Round> rounds,
      Map<Long, Team> teams, Map<Long, Player> players) {
    Round round = rounds.get(handsCursor.getLong(1));
    long id = handsCursor.getLong(0);
    Team biddingTeam = teams.get(handsCursor.getLong(2));
    Player biddingPlayer = players.get(handsCursor.getLong(3));
    Bid bid = handsCursor.getString(4).length() == 0 ? null : Bid
        .valueOf(handsCursor.getString(4));
    int tricksWon = handsCursor.getInt(5);
    int pointsWinningTeam = handsCursor.getInt(6);
    int pointsLosingTeam = handsCursor.getInt(7);
    Date date = new Date(handsCursor.getLong(8));
    Hand hand = new Hand(id, round, biddingTeam, biddingPlayer, bid, tricksWon,
        pointsWinningTeam, pointsLosingTeam, date);
    round.addHand(hand);
    return hand;
  }

  public void addHand(Hand hand) {
    long id = getWritableDatabase().insert(
        TABLE_HANDS,
        null,
        createHandValues(hand));
    hand.setId(id);
    requestBackup();
  }

  public void updateHand(Hand hand) {
    getWritableDatabase().update(
        TABLE_HANDS,
        createHandValues(hand),
        COLUMN_HAND_ID + " = " + hand.getId(),
        null);
    requestBackup();
  }

  public void removeHand(Hand hand) {
    getWritableDatabase().delete(
        TABLE_HANDS,
        COLUMN_HAND_ID + " = " + hand.getId(),
        null);
    requestBackup();
  }

  private ContentValues createHandValues(Hand hand) {
    ContentValues values = new ContentValues();
    values.put(COLUMN_ROUND_ID, hand.getRound().getId());
    values.put(COLUMN_BIDDING_TEAM_ID, hand.getBiddingTeam() != null ? hand
        .getBiddingTeam().getId() : -1);
    values.put(COLUMN_BIDDING_PLAYER_ID, hand.getBiddingPlayer() != null ? hand
        .getBiddingPlayer().getId() : -1);
    values.put(COLUMN_BID, hand.getBid() != null ? hand.getBid().toString()
        : "");
    values.put(COLUMN_TRICKS_WON, hand.getTricksWonByBiddingTeam());
    values.put(COLUMN_DATE, hand.getDate().getTime());
    return values;
  }

  public void addRound(Round round) {
    long id = getWritableDatabase().insert(
        TABLE_ROUNDS,
        null,
        createRoundValues(round));
    requestBackup();
    round.setId(id);
  }

  public void updateRound(Round round) {
    getWritableDatabase().update(
        TABLE_ROUNDS,
        createRoundValues(round),
        COLUMN_ROUND_ID + " = " + round.getId(),
        null);
    requestBackup();
  }

  public void removeRound(Round round) {
    getWritableDatabase().delete(
        TABLE_ROUNDS,
        COLUMN_ROUND_ID + " = " + round.getId(),
        null);
    requestBackup();
  }

  private ContentValues createRoundValues(Round round) {
    ContentValues values = new ContentValues();
    values.put(COLUMN_MATCH_ID, round.getMatch().getId());
    values.put(COLUMN_DATE, round.getDate().getTime());
    return values;
  }

  public void addMatch(Match match) {
    long id = getWritableDatabase().insert(
        TABLE_MATCHES,
        null,
        createMatchValues(match));
    match.setId(id);
    requestBackup();
  }

  public void updateMatch(Match match) {
    getWritableDatabase().update(
        TABLE_MATCHES,
        createMatchValues(match),
        COLUMN_MATCH_ID + " = " + match.getId(),
        null);
    requestBackup();
  }

  public void removeMatch(Match match) {
    getWritableDatabase().delete(
        TABLE_MATCHES,
        COLUMN_MATCH_ID + " = " + match.getId(),
        null);
    requestBackup();
  }

  private ContentValues createMatchValues(Match match) {
    ContentValues values = new ContentValues();
    values.put(COLUMN_TEAM_1, match.getTeam1().getId());
    values.put(COLUMN_TEAM_2, match.getTeam2().getId());
    values.put(COLUMN_DATE, match.getDate().getTime());
    return values;
  }

  public void addTeam(Team team) {
    ContentValues values = new ContentValues();
    values.put(COLUMN_TEAM_NAME, team.getName());
    values.put(COLUMN_PLAYER_1, team.getPlayer1() != null ? team
        .getPlayer1().getId() : -1);
    values.put(COLUMN_PLAYER_2, team.getPlayer2() != null ? team
        .getPlayer2().getId() : -1);
    long id = getWritableDatabase().insert(TABLE_TEAMS, null, values);
    team.setId(id);
    requestBackup();
  }

  public void addPlayer(Player player) {
    ContentValues values = new ContentValues();
    values.put(COLUMN_PLAYER_NAME, player.getName());
    long id = getWritableDatabase().insert(TABLE_PLAYERS, null, values);
    player.setId(id);
    requestBackup();
  }

  private void requestBackup() {
    BackupManager bm = new BackupManager(mContext);
    bm.dataChanged();
  }
}
