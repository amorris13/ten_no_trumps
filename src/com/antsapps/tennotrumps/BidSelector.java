package com.antsapps.tennotrumps;

import com.antsapps.tennotrumps.backend.Bid;

public interface BidSelector {

  public interface OnBidSelectedListener {
    void onBidSelected(Bid bid);
  }

  public void init();

  public void setOnBidSelectedListener(OnBidSelectedListener listener);

  public Bid getSelectedBid();

}