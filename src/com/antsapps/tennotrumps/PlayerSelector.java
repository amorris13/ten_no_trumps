package com.antsapps.tennotrumps;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.antsapps.tennotrumps.backend.Player;
import com.antsapps.tennotrumps.backend.Round;
import com.antsapps.tennotrumps.backend.Team;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class PlayerSelector extends LinearLayout {

  private static final String TAG = "PlayerSelector";

  public interface OnPlayerSelectedListener {
    void onPlayerSelected(Player player);
  }

  private final OnClickListener mOnPlayerButtonClickListener = new OnClickListener() {
    @Override
    public void onClick(View view) {
      ToggleButton thisPlayerButton = (ToggleButton) view;
      Player player = playersMap.get(thisPlayerButton);
      if (thisPlayerButton.isChecked()) {
        if (mPlayer != null) {
          playersMap.inverse().get(mPlayer).setChecked(false);
        }
        mPlayer = player;
        mListener.onPlayerSelected(player);
      } else {
        mPlayer = null;
        mListener.onPlayerSelected(null);
      }
    }
  };

  private OnPlayerSelectedListener mListener = new OnPlayerSelectedListener() {

    @Override
    public void onPlayerSelected(Player player) {
      // Empty listener to start with so that we don't have to do null checks.
      // Log if it hasn't been set.
      Log.i(TAG, "OnPlayerSelectedListener called when it is null");
    }
  };

  private Round mRound;

  private final BiMap<ToggleButton, Player> playersMap = HashBiMap.create();

  private Player mPlayer;

  public PlayerSelector(Context context, AttributeSet attr) {
    super(context, attr);
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.player_selector, this);
  }

  public void init(Round round) {
    mRound = round;
    if (!mRound.getTeam1().hasBothPlayers()
        && !mRound.getTeam2().hasBothPlayers()) {
      findViewById(R.id.players).setVisibility(View.GONE);
    } else {
      LinearLayout playersButtons = (LinearLayout) findViewById(R.id.playersToggle);
      for (int i = 0; i < playersButtons.getChildCount(); i++) {
        LinearLayout playersForTeam = (LinearLayout) playersButtons
            .getChildAt(i);
        Team team = mRound.getTeam(i);
        if (team.hasBothPlayers()) {
          for (int j = 0; j < playersForTeam.getChildCount(); j++) {
            ToggleButton playerButton = (ToggleButton) playersForTeam
                .getChildAt(j);
            Player player = team.getPlayer(j);
            playersMap.put(playerButton, player);
            AddHand.setText(playerButton, player.getName());
            playerButton.setEnabled(false);
            playerButton.setOnClickListener(mOnPlayerButtonClickListener);
          }
        } else {
          playersForTeam.setVisibility(View.INVISIBLE);
        }
      }
    }
  }

  public void setOnPlayerSelectedListener(OnPlayerSelectedListener listener) {
    mListener = listener;
  }

  public void onTeamSelected(Team team) {
    if (team == null) {
      mPlayer = null;
      for (ToggleButton button : playersMap.keySet()) {
        button.setEnabled(false);
        button.setChecked(false);
      }
    } else {
      for (Player player : mRound.getPlayers()) {
        if (team.contains(player)) {
          playersMap.inverse().get(player).setEnabled(true);
        } else {
          playersMap.inverse().get(player).setEnabled(false);
        }
      }
    }
  }

  public Player getSelectedPlayer() {
    return mPlayer;
  }
}
