package com.antsapps.tennotrumps;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

import com.antsapps.tennotrumps.backend.Bid;

public class PickerResultSelector extends LinearLayout implements
    ResultSelector {
  private static final String TAG = "PickerResultSelector";

  private int mTricksWon = 0;

  private OnTricksWonSelectedListener mListener = new OnTricksWonSelectedListener() {
    @Override
    public void onTricksWonSelected(int tricksWon) {
      // Empty listener to start with so that we don't have to do null checks.
      // Log if it hasn't been set.
      Log.i(TAG, "OnTricksWonSelectedListener called when it is null");
    }
  };
  private NumberPicker mNumberPicker;

  public PickerResultSelector(Context context, AttributeSet attr) {
    super(context, attr);
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.picker_result_selector, this);
  }

  @Override
  public void init() {
    mNumberPicker = (NumberPicker) findViewById(R.id.tricks_won_picker);

    mNumberPicker.setMinValue(0);
    mNumberPicker.setMaxValue(Bid.TRICKS_PER_HAND);
    mNumberPicker.setWrapSelectorWheel(false);

    mNumberPicker.setValue(0);

    mNumberPicker.setOnValueChangedListener(new OnValueChangeListener() {
      @Override
      public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        mTricksWon = newVal;
        mListener.onTricksWonSelected(mTricksWon);
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
  };
}
