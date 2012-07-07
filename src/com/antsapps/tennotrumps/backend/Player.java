package com.antsapps.tennotrumps.backend;

import com.google.common.base.Objects;

public class Player {
  public static final String ID_TAG = "player_id";

  private long id;
  private final String mName;

  public Player(String name) {
    mName = name;
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

  @Override
  public int hashCode(){
  	return Objects.hashCode(mName);
  }

  @Override
  public boolean equals(Object object){
  	if (object instanceof Player) {
  		Player that = (Player) object;
  		return Objects.equal(this.mName, that.mName);
  	}
  	return false;
  }

  @Override
  public String toString() {
    return "Player [id=" + getId() + ", mName=" + mName + "]";
  }
}
