package com.antsapps.tennotrumps;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.antsapps.tennotrumps.backend.Application;
import com.antsapps.tennotrumps.backend.Match;
import com.antsapps.tennotrumps.backend.OnStateChangedListener;
import com.antsapps.tennotrumps.backend.Round;
import com.antsapps.tennotrumps.backend.Team;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

public class RoundList extends SherlockListActivity implements OnStateChangedListener {
  private Application application;

  private Match mMatch;
  private List<Round> mRounds = null;
  private RoundArrayAdapter mAdapter;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    application = Application.getInstance(getApplicationContext());
    if (savedInstanceState != null) {
      mMatch = application.getMatch(savedInstanceState.getLong(Match.ID_TAG));
    } else {
      Preconditions.checkArgument(getIntent().hasExtra(Match.ID_TAG),
          "Intent to start RoundList must contain a Match.ID_TAG");
      mMatch = application.getMatch(getIntent().getLongExtra(Match.ID_TAG, 0));
    }

    setContentView(R.layout.round_list);

    ActionBar actionBar = getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

    Team team1 = mMatch.getTeam1();
    Team team2 = mMatch.getTeam2();

    ((TextView) findViewById(R.id.team1name)).setText(team1.getName());
    ((TextView) findViewById(R.id.team1wins)).setText(Integer.toString(mMatch
        .getRoundsWon(team1)));

    ((TextView) findViewById(R.id.team2name)).setText(team2.getName());
    ((TextView) findViewById(R.id.team2wins)).setText(Integer.toString(mMatch
        .getRoundsWon(team2)));

    mRounds = Lists.reverse(mMatch.getRounds());
    mAdapter = new RoundArrayAdapter(this, R.layout.round_list_item, mRounds);
    setListAdapter(mAdapter);

    ListView lv = getListView();
    lv.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id) {
        Round round = mRounds.get(position);
        Intent intent = new Intent(view.getContext(), HandList.class);
        intent.putExtra(Match.ID_TAG, mMatch.getId());
        intent.putExtra(Round.ID_TAG, round.getId());
        startActivity(intent);
      }
    });

    lv.setOnItemLongClickListener(new OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view,
          final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(RoundList.this);
        builder.setCancelable(true);
        builder.setTitle("Delete?");
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Round round = mRounds.get(position);
            application.deleteRound(round);
            dialog.dismiss();
          }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });
        AlertDialog alert = builder.create();
        alert.show();
        return false;
      }
    });

    mMatch.addOnStateChangedListener(this);
  }

  @Override
  public void onStart() {
    super.onStart();
    ArrayAdapter<Round> adapter = (ArrayAdapter<Round>) getListView()
        .getAdapter();
    adapter.notifyDataSetChanged();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putLong(Match.ID_TAG, mMatch.getId());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getSupportMenuInflater();
    inflater.inflate(R.menu.round_list, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    switch (item.getItemId()) {
      case R.id.new_round:
        Intent newRoundIntent = new Intent(getBaseContext(), HandList.class);
        Round round = new Round(mMatch);
        application.addRound(round);
        newRoundIntent.putExtra(Round.ID_TAG, round.getId());
        newRoundIntent.putExtra(Match.ID_TAG, mMatch.getId());
        startActivity(newRoundIntent);
        return true;
      case android.R.id.home:
        // app icon in action bar clicked; go up one level
        Intent goBackIntent = new Intent(this, MatchList.class);
        startActivity(goBackIntent);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private class RoundArrayAdapter extends ArrayAdapter<Round> {

    private final List<Round> rounds;

    public RoundArrayAdapter(Context context,
        int textViewResourceId,
        List<Round> rounds) {
      super(context, textViewResourceId, rounds);
      this.rounds = rounds;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View v = convertView;
      if (v == null) {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.round_list_item, null);
      }

      Team team1 = mMatch.getTeam1();
      Team team2 = mMatch.getTeam2();

      Round r = rounds.get(position);

      TextView team1ScoreView = (TextView) v.findViewById(R.id.team1score);
      team1ScoreView.setText(Integer.toString(r.getScore(team1)));

      TextView team2ScoreView = (TextView) v.findViewById(R.id.team2score);
      team2ScoreView.setText(Integer.toString(r.getScore(team2)));

      styleEntry(team1, team2, r, team1ScoreView, team2ScoreView);

      ((TextView) v.findViewById(R.id.date)).setText(DateUtils
          .getRelativeTimeSpanString(r.getDate() != null ? r.getDate()
              .getTime() : 0, System.currentTimeMillis(),
              DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE));

      return v;
    }

    private void styleEntry(Team team1, Team team2, Round r,
        TextView team1ScoreView, TextView team2ScoreView) {
      if (!r.isFinished()) {
        team1ScoreView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        team2ScoreView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
      } else {
        int winColor = getResources().getColor(R.color.win);
        int loseColor = getResources().getColor(R.color.lose);
        Team winner = r.getWinner();
        team1ScoreView.setTextColor(winner == team1 ? winColor : loseColor);
        team2ScoreView.setTextColor(winner == team2 ? winColor : loseColor);
      }
    }

  }

  public void notifyDataSetChanged() {
    mAdapter.notifyDataSetChanged();
  }

  @Override
  public void onStateChanged() {
    mAdapter.notifyDataSetChanged();
  }
}