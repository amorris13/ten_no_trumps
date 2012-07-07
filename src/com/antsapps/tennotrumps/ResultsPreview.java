package com.antsapps.tennotrumps;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.antsapps.tennotrumps.backend.Bid;
import com.antsapps.tennotrumps.backend.Round;
import com.antsapps.tennotrumps.backend.Team;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ResultsPreview extends LinearLayout {

  private static final String TAG = "ResultsPreview";

  private final BiMap<TextView, Team> teamsMap = HashBiMap.create();

  private final LinearLayout mLayout;

  public ResultsPreview(Context context, AttributeSet attr) {
    super(context, attr);
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.results_preview, this);
    mLayout = (LinearLayout) findViewById(R.id.result);
  }

  public void init(Round round) {
    for (int i = 0; i < mLayout.getChildCount(); i++) {
      TextView teamText = (TextView) mLayout.getChildAt(i);
      Team team = round.getTeam(i);
      teamsMap.put(teamText, team);
    }
    updateHandDetails(null, null, -1);
  }

  public void updateHandDetails(Team biddingTeam, Bid bid, int tricksWon) {
    if (biddingTeam != null && bid != null && tricksWon != -1) {
      for (TextView textView : teamsMap.keySet()) {
        Team teamForTextView = teamsMap.get(textView);
        if (biddingTeam == teamForTextView) {
          int points = bid.getScore(true, tricksWon);
          textView.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
          if (points > 0) {
            textView.setText("+" + points);
            textView.setTextColor(getResources().getColor(R.color.win));
          } else {
            textView.setText(String.valueOf(points));
            textView.setTextColor(getResources().getColor(R.color.lose));
          }
        } else {
          int points = bid.getScore(false, tricksWon);
          textView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
          if (points > 0) {
            textView.setText("+" + points);
            textView.setTextColor(Color.BLACK);
          } else {
            textView.setText(String.valueOf(points));
            textView.setTextColor(Color.BLACK);
          }
        }

      }
    } else {
      for (TextView textView : teamsMap.keySet()) {
        textView.setText("-");
        textView.setTextColor(Color.GRAY);
        textView.setTypeface(Typeface.DEFAULT, Typeface.NORMAL);
      }
    }
  }
}
