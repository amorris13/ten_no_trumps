package com.antsapps.tennotrumps;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.antsapps.tennotrumps.backend.Round;
import com.antsapps.tennotrumps.backend.Team;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class TeamSelector extends LinearLayout {
  private static final String TAG = "TeamSelector";

  public interface OnTeamSelectedListener {
    void onTeamSelected(Team team);
  }

  private final OnClickListener mOnTeamButtonClickListener = new OnClickListener() {
    @Override
    public void onClick(View v) {
      ToggleButton thisTeamButton = (ToggleButton) v;
      Team team = teamsMap.get(thisTeamButton);
      if (thisTeamButton.isChecked()) {
        if (mTeam != null) {
          teamsMap.inverse().get(mTeam).setChecked(false);
        }
        mTeam = team;
        mListener.onTeamSelected(team);
      } else {
        mTeam = null;
        mListener.onTeamSelected(null);
      }
    }
  };

  private OnTeamSelectedListener mListener = new OnTeamSelectedListener() {
    @Override
    public void onTeamSelected(Team team) {
      // Empty listener to start with so that we don't have to do null checks.
      // Log if it hasn't been set.
      Log.i(TAG, "OnTeamSelectedListener called when it is null");
    }
  };

  private Round mRound;

  private final BiMap<ToggleButton, Team> teamsMap = HashBiMap.create();

  private Team mTeam;

  public TeamSelector(Context context, AttributeSet attr) {
    super(context, attr);
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.team_selector, this);
  }

  public void init(Round round) {
    mRound = round;
    LinearLayout teamsButtons = (LinearLayout) findViewById(R.id.teamsToggle);
    for (int i = 0; i < teamsButtons.getChildCount(); i++) {
      ToggleButton teamButton = (ToggleButton) teamsButtons.getChildAt(i);
      Team team = mRound.getTeam(i);
      teamsMap.put(teamButton, team);
      AddHand.setText(teamButton, team.getName());
      teamButton.setOnClickListener(mOnTeamButtonClickListener);
    }
  }

  public void setOnTeamSelectedListener(OnTeamSelectedListener listener) {
    mListener = listener;
  }

  public Team getSelectedTeam() {
    return mTeam;
  }
}
