package com.antsapps.tennotrumps;

import java.util.List;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.antsapps.tennotrumps.backend.Application;
import com.antsapps.tennotrumps.backend.Hand;
import com.antsapps.tennotrumps.backend.Match;
import com.antsapps.tennotrumps.backend.OnStateChangedListener;
import com.antsapps.tennotrumps.backend.Round;
import com.antsapps.tennotrumps.backend.Team;
import com.google.common.base.Preconditions;

public class HandList extends ListActivity implements OnStateChangedListener {

  private Application application;

  private static final String TAG = "HandList";

  private Round mRound;
  private List<Hand> mHands = null;
  private HandArrayAdapter mAdapter;

  private Team team1;

  private Team team2;

  private ListView lv;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    application = Application.getInstance(getApplicationContext());
    Log.i("TAG", "intent that started activity = " + getIntent());
    if (savedInstanceState != null) {
      long matchId = savedInstanceState.getLong(Match.ID_TAG);
      Log.i(TAG, "matchId = " + matchId);
      long roundId = savedInstanceState.getLong(Round.ID_TAG, 0);
      Log.i(TAG, "roundId = " + roundId);
      mRound = application.getMatch(matchId).getRound(roundId);
    } else {
      Preconditions.checkArgument(getIntent().hasExtra(Match.ID_TAG),
          "Intent to start RoundList must contain a Match.ID_TAG");
      Preconditions.checkArgument(getIntent().hasExtra(Round.ID_TAG),
          "Intent to start RoundList must contain a Round.ID_TAG");
      mRound = application.getMatch(getIntent().getLongExtra(Match.ID_TAG, 0))
          .getRound(getIntent().getLongExtra(Round.ID_TAG, 0));
    }
    setContentView(R.layout.hand_list);

    ActionBar actionBar = getActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);

    Log.i(TAG, "Round = " + mRound);

    team1 = mRound.getTeam1();
    team2 = mRound.getTeam2();

    ((TextView) findViewById(R.id.team1name)).setText(team1.getName());
    ((TextView) findViewById(R.id.team2name)).setText(team2.getName());

    highlightTitle();

    lv = getListView();

    mHands = mRound.getHands();
    mAdapter = new HandArrayAdapter(this, R.layout.round_list_item, mHands);
    lv.setAdapter(mAdapter);

    lv.setSelection(lv.getCount() - 1);
    lv.setOnItemLongClickListener(new OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view,
          final int position, long id) {
        showDeleteDialog(position);
        return false;
      }

      private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HandList.this);
        builder.setCancelable(true);
        builder.setTitle("Delete?");
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Hand hand = mHands.get(position);
            application.deleteHand(hand);
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
      }
    });

    mRound.addOnStateChangedListener(this);
  }

  private void highlightTitle() {
    highlightTeamName(team1, (TextView) findViewById(R.id.team1name));
    highlightTeamName(team2, (TextView) findViewById(R.id.team2name));
  }

  private void highlightTeamName(Team team, TextView teamNameView){
    if(mRound.isFinished()){
      teamNameView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
      if(mRound.getWinner() == team){
        teamNameView.setTextColor(getResources().getColor(R.color.win));
      } else {
        teamNameView.setTextColor(getResources().getColor(R.color.lose));
      }
    } else {
      teamNameView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
      teamNameView.setTextColor(Color.BLACK);
    }
  }

  @Override
  public void onStart() {
    super.onStart();
    ArrayAdapter<Hand> adapter = (ArrayAdapter<Hand>) getListView().getAdapter();
    adapter.notifyDataSetChanged();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putLong(Match.ID_TAG, mRound.getMatch().getId());
    outState.putLong(Round.ID_TAG, mRound.getId());
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.hand_list, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.new_hand:
        if (mRound.isFinished()) {
          showNewRoundDialog();
        } else {
          Intent newHandIntent = new Intent(getBaseContext(), AddHand.class);
          newHandIntent.putExtra(Round.ID_TAG, mRound.getId());
          newHandIntent.putExtra(Match.ID_TAG, mRound.getMatch().getId());
          startActivity(newHandIntent);
        }
        return true;
      case android.R.id.home:
        // app icon in action bar clicked; go up one level
        Intent intent = new Intent(this, RoundList.class);
        intent.putExtra(Match.ID_TAG, mRound.getMatch().getId());
        startActivity(intent);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private void showNewRoundDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(HandList.this);
    builder.setCancelable(true);
    builder.setTitle("New Round?");
    builder.setInverseBackgroundForced(true);
    builder.setMessage("This round has finished. Start a new round?");
    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        Round round = new Round(mRound.getMatch());
        application.addRound(round);

        Intent newRoundIntent = new Intent(getBaseContext(), HandList.class);
        newRoundIntent.putExtra(Round.ID_TAG, round.getId());
        newRoundIntent.putExtra(Match.ID_TAG, round.getMatch().getId());
        startActivity(newRoundIntent);

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
  }

  private class HandArrayAdapter extends ArrayAdapter<Hand> {

    private final List<Hand> hands;

    public HandArrayAdapter(Context context,
        int textViewResourceId,
        List<Hand> hands) {
      super(context, textViewResourceId, hands);
      this.hands = hands;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View v = convertView;
      if (v == null) {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.hand_list_item, null);
      }

      Team team1 = mRound.getTeam1();
      Team team2 = mRound.getTeam2();

      Hand h = hands.get(position);
      if (h != null) {
        formatBid(v, team1, h, R.id.team1bid, R.id.team1trickswon,
            R.id.team1scoreAfterBid, R.id.team1scorechange,
            position == hands.size() - 1);

        formatBid(v, team2, h, R.id.team2bid, R.id.team2trickswon,
            R.id.team2scoreAfterBid, R.id.team2scorechange,
            position == hands.size() - 1);
      }

      return v;
    }

    private void formatBid(View view, Team team, Hand hand, int bidId,
        int tricksWonId, int scoreAfterBidId, int scoreChangeId, boolean mostRecent) {
      TextView teambid = (TextView) view.findViewById(bidId);
      if (hand.getBiddingTeam() == team) {
        teambid.setText(hand.getBid().getSymbol());
        teambid.setTextColor(hand.getBid().getColor());
      } else {
        teambid.setText("");
      }

      TextView teamScoreAfterBid = (TextView) view
          .findViewById(scoreAfterBidId);
      teamScoreAfterBid.setText(Integer.toString(mRound.getScoreAfterHand(team,
          hand)));
      if (!mostRecent) {
        teamScoreAfterBid.setPaintFlags(teamScoreAfterBid.getPaintFlags()
            | Paint.STRIKE_THRU_TEXT_FLAG);
      }

      TextView teamTricksWon = (TextView) view.findViewById(tricksWonId);
      teamTricksWon.setText("won" + " " + hand.getTricksWon(team));

      TextView teamScoreChange = (TextView) view.findViewById(scoreChangeId);
      teamScoreChange.setText(formatScoreChange(hand.getScore(team)));
      if (hand.getBiddingTeam() == team) {
        int winColor = getResources().getColor(R.color.win);
        int loseColor = getResources().getColor(R.color.lose);
        teamScoreChange.setTextColor(hand.isBidAchieved() ? winColor
            : loseColor);
      } else {
        teamScoreChange.setTextColor(Color.BLACK);
      }
    }

    private String formatScoreChange(int scorechange1) {
      return ((scorechange1 > 0) ? "+" : "") + Integer.toString(scorechange1);
    }
  }

  @Override
  public void onStateChanged() {
    mAdapter.notifyDataSetChanged();
    scrollToBottom();
    highlightTitle();
  }

  private void scrollToBottom() {
    lv = getListView();
    lv.post(new Runnable() {
      @Override
      public void run() {
        lv.setSelection(lv.getCount() - 1);
      }
    });
  }
}
