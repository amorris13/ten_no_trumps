package com.antsapps.tennotrumps.backend;

import java.util.List;

import android.graphics.Color;

import com.google.common.collect.ArrayTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;

public enum Bid {
  SIX_SPADES(Tricks.SIX, Suit.SPADES),
  SIX_CLUBS(Tricks.SIX, Suit.CLUBS),
  SIX_DIAMONDS(Tricks.SIX, Suit.DIAMONDS),
  SIX_HEARTS(Tricks.SIX, Suit.HEARTS),
  SIX_NOTRUMPS(Tricks.SIX, Suit.NOTRUMPS),
  SEVEN_SPADES(Tricks.SEVEN, Suit.SPADES),
  SEVEN_CLUBS(Tricks.SEVEN, Suit.CLUBS),
  SEVEN_DIAMONDS(Tricks.SEVEN, Suit.DIAMONDS),
  SEVEN_HEARTS(Tricks.SEVEN, Suit.HEARTS),
  SEVEN_NOTRUMPS(Tricks.SEVEN, Suit.NOTRUMPS),
  EIGHT_SPADES(Tricks.EIGHT, Suit.SPADES),
  EIGHT_CLUBS(Tricks.EIGHT, Suit.CLUBS),
  EIGHT_DIAMONDS(Tricks.EIGHT, Suit.DIAMONDS),
  EIGHT_HEARTS(Tricks.EIGHT, Suit.HEARTS),
  EIGHT_NOTRUMPS(Tricks.EIGHT, Suit.NOTRUMPS),
  NINE_SPADES(Tricks.NINE, Suit.SPADES),
  NINE_CLUBS(Tricks.NINE, Suit.CLUBS),
  NINE_DIAMONDS(Tricks.NINE, Suit.DIAMONDS),
  NINE_HEARTS(Tricks.NINE, Suit.HEARTS),
  NINE_NOTRUMPS(Tricks.NINE, Suit.NOTRUMPS),
  TEN_SPADES(Tricks.TEN, Suit.SPADES),
  TEN_CLUBS(Tricks.TEN, Suit.CLUBS),
  TEN_DIAMONDS(Tricks.TEN, Suit.DIAMONDS),
  TEN_HEARTS(Tricks.TEN, Suit.HEARTS),
  TEN_NOTRUMPS(Tricks.TEN, Suit.NOTRUMPS),
  CLOSEDMISERE(Tricks.ZERO, Suit.CLOSEDMISERE),
  OPENMISERE(Tricks.ZERO, Suit.OPENMISERE);

  public static final String ID_TAG = "bid_id";

  public static final int TRICKS_PER_HAND = 10;

  private static Table<Tricks, Suit, Bid> table = ArrayTable.create(
      Tricks.list, Suit.list);

  static {
    for (Bid bid : Bid.values()) {
      table.put(bid.mTricks, bid.mSuit, bid);
    }
  }

  public final Tricks mTricks;
  public final Suit mSuit;

  Bid(Tricks tricks, Suit suit) {
    mTricks = tricks;
    mSuit = suit;
  }

  public String getSymbol() {
    return mTricks.mSymbol + mSuit.mSymbol;
  }

  public int getColor() {
    return mSuit.mColor;
  }

  public boolean isWinningNumberOfTricks(int tricksWonByBiddingTeam) {
    if (mTricks == Tricks.ZERO) {
      return tricksWonByBiddingTeam == 0;
    } else {
      return tricksWonByBiddingTeam >= mTricks.mNumber;
    }
  }

  public static Bid getBid(Tricks tricks, Suit suit) {
    return table.get(tricks, suit);
  }

  public String toFullString() {
    return (mTricks == Tricks.ZERO) ? mSuit.toFullString() : mTricks
        .toFullString() + " " + mSuit.toFullString();
  }

  public enum Suit {
    SPADES("Spades", "\u2660", Color.BLACK),
    CLUBS("Clubs", "\u2663", Color.BLACK),
    DIAMONDS("Diamonds", "\u2666", Color.RED),
    HEARTS("Hearts", "\u2665", Color.RED),
    NOTRUMPS("No Trumps", "NT", Color.BLACK),
    CLOSEDMISERE("Closed Misere", "CM", Color.BLACK),
    OPENMISERE("Open Misere", "OM", Color.BLACK);

    public static final int NUM_REAL_SUITS = 5;
    public static final List<Suit> list = Lists.newArrayList(SPADES, CLUBS,
        DIAMONDS, HEARTS, NOTRUMPS, CLOSEDMISERE, OPENMISERE);

    public final String mName;
    public final String mSymbol;
    public final int mColor;

    Suit(String name, String symbol, int color) {
      mName = name;
      mSymbol = symbol;
      mColor = color;
    }

    public String toFullString() {
      return mName;
    }
  }

  public enum Tricks {
    ZERO(0, ""),
    SIX(6, "Six"),
    SEVEN(7, "Seven"),
    EIGHT(8, "Eight"),
    NINE(9, "Nine"),
    TEN(10, "Ten");

    public static final List<Tricks> list = Lists.newArrayList(SIX, SEVEN,
        EIGHT, NINE, TEN, ZERO);

    public final int mNumber;
    public final String mName;
    public final String mSymbol;

    Tricks(int number, String name) {
      mNumber = number;
      mName = name;
      mSymbol = number == 0 ? "" : ((Integer) number).toString();
    }

    public String toFullString() {
      return mName;
    }
  }
}
