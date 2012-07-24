package com.antsapps.tennotrumps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.antsapps.tennotrumps.backend.Application;
import com.antsapps.tennotrumps.backend.Match;
import com.antsapps.tennotrumps.backend.Player;
import com.antsapps.tennotrumps.backend.Round;
import com.antsapps.tennotrumps.backend.Team;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class AddMatch extends SherlockActivity {
  public Application application;

  private Collection<Team> allTeams;
  private Collection<Player> allPlayers;

  private final Map<AutoCompleteTextView, List<AutoCompleteTextView>> playerViews = Maps
      .newHashMap();

  private List<String> teamNames, playerNames;

  private Button createMatch;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    application = Application.getInstance(getApplicationContext());
    setContentView(R.layout.add_match);

    initTeamNames();
    initPlayerNames();

    ArrayAdapter<String> teamsAdapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_dropdown_item_1line, teamNames);
    ArrayAdapter<String> playersAdapter = new ArrayAdapter<String>(this,
        android.R.layout.simple_dropdown_item_1line, playerNames);

    final AutoCompleteTextView team1view = (AutoCompleteTextView) findViewById(R.id.autoCompleteTeam1);
    List<AutoCompleteTextView> team1PlayerViews = Lists.newArrayList();
    team1PlayerViews
        .add((AutoCompleteTextView) findViewById(R.id.autocompletePlayer1i));
    team1PlayerViews
        .add((AutoCompleteTextView) findViewById(R.id.autocompletePlayer1ii));
    playerViews.put(team1view, team1PlayerViews);

    final AutoCompleteTextView team2view = (AutoCompleteTextView) findViewById(R.id.autoCompleteTeam2);
    List<AutoCompleteTextView> team2PlayerViews = Lists.newArrayList();
    team2PlayerViews
        .add((AutoCompleteTextView) findViewById(R.id.autocompletePlayer2i));
    team2PlayerViews
        .add((AutoCompleteTextView) findViewById(R.id.autocompletePlayer2ii));
    playerViews.put(team2view, team2PlayerViews);

    for (final AutoCompleteTextView teamView : playerViews.keySet()) {
      teamView.setAdapter(teamsAdapter);
      for (AutoCompleteTextView playerView : playerViews.get(teamView)) {
        playerView.setAdapter(playersAdapter);
      }

      teamView.addTextChangedListener(new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
            int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
            int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
          String teamName = s.toString();
          Team team = application.getTeam(teamName);
          if (team != null) {
            int i = 0;
            for (AutoCompleteTextView playerView : playerViews.get(teamView)) {
              Player player = team.getPlayer(i);
              if (player != null) {
                playerView.setText(player.getName());
              }
              playerView.setEnabled(false);
              i++;
            }
          } else {
            for (AutoCompleteTextView playerView : playerViews.get(teamView)) {
              playerView.setEnabled(true);
            }
          }
          checkTeamViews();
        }
      });
    }

    createMatch = (Button) findViewById(R.id.createMatch);
    createMatch.setEnabled(false);
  }

  private void checkTeamViews() {
    boolean enabled = true;
    for (AutoCompleteTextView teamView : playerViews.keySet()) {
      enabled &= (teamView.getText().length() > 0);
    }
    createMatch.setEnabled(enabled);
  }

  public void createMatch(View view) {
    if (!(checkUniqueNames())) {
      Toast.makeText(getApplicationContext(),
          "Team and Player names must be unique", Toast.LENGTH_SHORT).show();
      return;
    }

    List<Team> teams = Lists.newArrayList();
    for (AutoCompleteTextView teamView : playerViews.keySet()) {
      Team team = application.getTeam(teamView.getText().toString());
      if (team == null) {
        team = new Team(teamView.getText().toString());
        int i = 0;
        for (AutoCompleteTextView playerView : playerViews.get(teamView)) {
          String playerName = (playerView.getText().toString());
          if (playerName.length() > 0) {
            Player player = application.getPlayer(playerName);
            if (player == null) {
              player = new Player(playerName);
              application.addPlayer(player);
            }
            team.setPlayer(i, player);
          }
          i++;
        }
        application.addTeam(team);
      }
      teams.add(team);
    }

    Match match = new Match(teams.get(0), teams.get(1));

    application.addMatch(match);

    Round round = new Round(match);
    application.addRound(round);

    Log.i("AddMatch", match.toString());

    Intent intent = new Intent(this, HandList.class);
    intent.putExtra(Match.ID_TAG, match.getId());
    intent.putExtra(Round.ID_TAG, round.getId());
    startActivity(intent);
    finish();
  }

  private boolean checkUniqueNames() {
    boolean uniqueTeams = true;
    boolean uniquePlayers = true;
    Set<String> teamSet = Sets.newHashSet();
    Set<String> playerSet = Sets.newHashSet();
    for (AutoCompleteTextView teamView : playerViews.keySet()) {
      uniqueTeams &= teamSet.add(teamView.getText().toString());
      for (AutoCompleteTextView playerView : playerViews.get(teamView)) {
        String playerName = (playerView.getText().toString());
        if (playerName.length() > 0) {
          uniquePlayers &= playerSet.add(playerName);
        }
      }
    }

    return uniqueTeams && uniquePlayers;
  }

  private void initTeamNames() {
    allTeams = application.getTeams();
    teamNames = new ArrayList<String>();
    for (Team team : allTeams) {
      teamNames.add(team.getName());
    }
    Collections.sort(teamNames);
  }

  private void initPlayerNames() {
    allPlayers = application.getPlayers();
    playerNames = new ArrayList<String>();
    for (Player player : allPlayers) {
      playerNames.add(player.getName());
    }
    Collections.sort(playerNames);
  }
}
