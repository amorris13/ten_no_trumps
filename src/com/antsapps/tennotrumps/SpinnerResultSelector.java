package com.antsapps.tennotrumps;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.antsapps.tennotrumps.backend.Bid;

public class SpinnerResultSelector extends LinearLayout implements
    ResultSelector {

  private static final String TAG = "SpinnerResultSelector";

  private static final String SELECT = "SELECT";

  private int mTricksWon = -1;

  private OnTricksWonSelectedListener mListener = new OnTricksWonSelectedListener() {
    @Override
    public void onTricksWonSelected(int tricksWon) {
      // Empty listener to start with so that we don't have to do null checks.
      // Log if it hasn't been set.
      Log.i(TAG, "OnTricksWonSelectedListener called when it is null");
    }
  };
  private Spinner mSpinner;

  public SpinnerResultSelector(Context context, AttributeSet attr) {
    super(context, attr);
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.spinner_result_selector, this);
  }

  @Override
  public void init() {
    mSpinner = (Spinner) findViewById(R.id.tricks_won_spinner);

    ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
        getContext(), android.R.layout.simple_spinner_item);
    adapter
        .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    adapter.add(SELECT);
    for (int i = 0; i <= Bid.TRICKS_PER_HAND; i++) {
      adapter.add(String.valueOf(Bid.TRICKS_PER_HAND - i));
    }
    mSpinner.setAdapter(adapter);

    mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int pos,
          long id) {
        String string = (String) parent.getItemAtPosition(pos);
        if (string != SELECT) {
          mTricksWon = Integer.valueOf(string);
        } else {
          mTricksWon = -1;
        }
        mListener.onTricksWonSelected(mTricksWon);
      }

      @Override
      public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

      }
    });
  }

  @Override
  public void setOnTricksWonSelectedListener(
      OnTricksWonSelectedListener listener) {
    mListener = listener;
  }

  @Override
  public int getTricksWon() {
    return mTricksWon;
  }

}
