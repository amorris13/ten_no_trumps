package com.antsapps.tennotrumps.backend;

import java.util.Collections;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.collect.Lists;

public class Team {
  public static final String ID_TAG = "team_id";

  private long id;

  private final List<Player> mPlayers = Lists.newArrayList(null, null);
  private final String mName;

  public Team(String name) {
    mName = name;
  }

  public Player getPlayer1() {
    return mPlayers.get(0);
  }

  public void setPlayer1(Player player1) {
    mPlayers.set(0, player1);
  }

  public Player getPlayer2() {
    return mPlayers.get(1);
  }

  public void setPlayer2(Player player2) {
    mPlayers.set(1, player2);
  }

  public Player getPlayerWithId(long id) {
    for (Player player : mPlayers) {
      if (player.getId() == id) {
        return player;
      }
    }
    return null;
  }

  public void setPlayer(int i, Player player){
    mPlayers.set(i, player);
  }

  public Player getPlayer(int i) {
    return mPlayers.get(i);
  }

  public String getName() {
    return mName;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public boolean hasBothPlayers() {
    return getPlayer1() != null && getPlayer2() != null;
  }

  public List<Player> getPlayers() {
    if (hasBothPlayers()) {
      return Collections.unmodifiableList(mPlayers);
    } else {
      return Collections.emptyList();
    }
  }

  public boolean contains(Player player) {
    return mPlayers.contains(player);
  }

  @Override
  public int hashCode(){
  	return Objects.hashCode(mName);
  }

  @Override
  public boolean equals(Object object){
  	if (object instanceof Team) {
  		Team that = (Team) object;
  		return Objects.equal(this.mName, that.mName);
  	}
  	return false;
  }
}
