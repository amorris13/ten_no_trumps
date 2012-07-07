package com.antsapps.tennotrumps;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.antsapps.tennotrumps.backend.Bid;
import com.antsapps.tennotrumps.backend.Bid.Suit;
import com.antsapps.tennotrumps.backend.Bid.Tricks;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class ButtonsBidSelector extends LinearLayout implements BidSelector {

  private static final String TAG = "ButtonsBidSelector";

  private OnBidSelectedListener mListener = new OnBidSelectedListener() {
    @Override
    public void onBidSelected(Bid bid) {
      // Empty listener to start with so that we don't have to do null checks.
      // Log if it hasn't been set.
      Log.i(TAG, "OnBidSelectedListener called when it is null");
    }
  };

  private final OnClickListener mOnBidButtonClickListener = new OnClickListener() {
    @Override
    public void onClick(View view) {
      ToggleButton thisBidButton = (ToggleButton) view;
      Bid bid = bidsMap.get(thisBidButton);
      if (thisBidButton.isChecked()) {
        if (mBid != null) {
          bidsMap.inverse().get(mBid).setChecked(false);
        }
        mBid = bid;
        mListener.onBidSelected(bid);
      } else {
        mBid = null;
        mListener.onBidSelected(null);
      }
    }
  };

  private Bid mBid;

  private final BiMap<ToggleButton, Bid> bidsMap = HashBiMap.create();

  public ButtonsBidSelector(Context context, AttributeSet attr) {
    super(context, attr);
    LayoutInflater inflater = (LayoutInflater) context
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    inflater.inflate(R.layout.buttons_bid_selector, this);
  }

  @Override
  public void init() {
    LinearLayout bidButtons = (LinearLayout) findViewById(R.id.bidsToggle);
    for (int row = 0; row < bidButtons.getChildCount(); row++) {
      LinearLayout bidButtonRow = (LinearLayout) bidButtons.getChildAt(row);
      for (int col = 0; col < bidButtonRow.getChildCount(); col++) {
        ToggleButton bidButton = (ToggleButton) bidButtonRow.getChildAt(col);
        Bid bid = getBid(row, col);
        bidsMap.put(bidButton, bid);
        setText(bidButton, bid.mTricks == Tricks.ZERO ? bid.toFullString()
            : bid.getSymbol());
        bidButton.setTextColor(bid.getColor());
        bidButton.setOnClickListener(mOnBidButtonClickListener);
      }
    }
  }

  private Bid getBid(int row, int col) {
    Tricks tricks = Tricks.list.get(row);
    Suit suit;
    if (tricks == Tricks.ZERO) {
      suit = Suit.list.get(Suit.NUM_REAL_SUITS + col);
    } else {
      suit = Suit.list.get(col);
    }
    return Bid.getBid(tricks, suit);
  }

  @Override
  public void setOnBidSelectedListener(OnBidSelectedListener listener) {
    mListener = listener;
  }

  @Override
  public Bid getSelectedBid() {
    return mBid;
  }

  public static void setText(ToggleButton toggleButton, String text) {
    toggleButton.setText(text);
    toggleButton.setTextOn(text);
    toggleButton.setTextOff(text);
  }
}
