package com.antsapps.tennotrumps;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockActivity;
import com.antsapps.tennotrumps.BidSelector.OnBidSelectedListener;
import com.antsapps.tennotrumps.ResultSelector.OnTricksWonSelectedListener;
import com.antsapps.tennotrumps.TeamSelector.OnTeamSelectedListener;
import com.antsapps.tennotrumps.backend.Application;
import com.antsapps.tennotrumps.backend.Bid;
import com.antsapps.tennotrumps.backend.Hand;
import com.antsapps.tennotrumps.backend.Match;
import com.antsapps.tennotrumps.backend.Round;
import com.antsapps.tennotrumps.backend.Team;
import com.google.common.base.Preconditions;

public class AddHand extends SherlockActivity {
  private Application application;

  private static final String TAG = "AddHand";

  private Round mRound;

  private TeamSelector mTeamSelector;
  private PlayerSelector mPlayerSelector;
  private BidSelector mBidSelector;
  private ResultSelector mResultSelector;
  private ResultsPreview mResultPreview;
  private Button mOKButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    application = Application.getInstance(getApplicationContext());
    if (savedInstanceState != null) {
      mRound = application.getMatch(savedInstanceState.getLong(Match.ID_TAG))
          .getRound(savedInstanceState.getLong(Round.ID_TAG, 0));
    } else {
      Preconditions.checkArgument(getIntent().hasExtra(Match.ID_TAG),
          "Intent to start RoundList must contain a Match.ID_TAG");
      Preconditions.checkArgument(getIntent().hasExtra(Round.ID_TAG),
          "Intent to start RoundList must contain a Round.ID_TAG");
      mRound = application.getMatch(getIntent().getLongExtra(Match.ID_TAG, 0))
          .getRound(getIntent().getLongExtra(Round.ID_TAG, 0));
    }
    setContentView(R.layout.add_hand);

    mTeamSelector = (TeamSelector) findViewById(R.id.team_selector);
    mPlayerSelector = (PlayerSelector) findViewById(R.id.player_selector);
    mBidSelector = (BidSelector) findViewById(R.id.bid_selector);
    mResultSelector = (ResultSelector) findViewById(R.id.result_selector);
    mResultPreview = (ResultsPreview) findViewById(R.id.results_preview);

    mTeamSelector.init(mRound);
    mPlayerSelector.init(mRound);
    mBidSelector.init();
    mResultSelector.init();
    mResultPreview.init(mRound);

    mTeamSelector.setOnTeamSelectedListener(new OnTeamSelectedListener() {
      @Override
      public void onTeamSelected(Team team) {
        mPlayerSelector.onTeamSelected(team);
        checkAndNotifyWhetherFinished();
      }
    });
    mBidSelector.setOnBidSelectedListener(new OnBidSelectedListener() {
      @Override
      public void onBidSelected(Bid bid) {
        checkAndNotifyWhetherFinished();
      }
    });
    mResultSelector
        .setOnTricksWonSelectedListener(new OnTricksWonSelectedListener() {
          @Override
          public void onTricksWonSelected(int tricksWon) {
            checkAndNotifyWhetherFinished();
          }
        });

    initOKButton();
    enableOKButton(false);
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putLong(Match.ID_TAG, mRound.getMatch().getId());
    outState.putLong(Round.ID_TAG, mRound.getId());
  }

  private void enableOKButton(boolean enable) {
    mOKButton.setEnabled(enable);
  }

  private void initOKButton() {
    mOKButton = (Button) findViewById(R.id.ok_button);
  }

  /**
   * Checks whether finished, notifies the results preview to be updated and
   * enables/disables the OK button.
   */
  private boolean checkAndNotifyWhetherFinished() {
    Team biddingTeam = mTeamSelector.getSelectedTeam();
    Bid selectedBid = mBidSelector.getSelectedBid();
    int tricksWon = mResultSelector.getTricksWon();

    mResultPreview.updateHandDetails(biddingTeam, selectedBid, tricksWon);

    if (biddingTeam != null && selectedBid != null && tricksWon != -1) {
      enableOKButton(true);
      return true;
    } else {
      enableOKButton(false);
      return false;
    }
  }

  public void onOKClick(View view) {
    if (!checkAndNotifyWhetherFinished()) {
      Log.w(TAG, "OK Button shouldn't have been clickable.");
      return;
    }

    Hand hand = application.addHand(
        mRound,
        mBidSelector.getSelectedBid(),
        mTeamSelector.getSelectedTeam(),
        mPlayerSelector.getSelectedPlayer(),
        mResultSelector.getTricksWon());

    Log.i(TAG, hand.toString());

    if (mRound.isFinished()) {
      Toast.makeText(getApplicationContext(),
          mRound.getWinner().getName() + " won!", Toast.LENGTH_LONG).show();
    }
    finish();
  }

  public static void setText(ToggleButton toggleButton, String text) {
    toggleButton.setText(text);
    toggleButton.setTextOn(text);
    toggleButton.setTextOff(text);
  }
}
