package com.antsapps.tennotrumps.backend;

import android.app.backup.BackupAgentHelper;

public class DBBackupAgent extends BackupAgentHelper {

  private static final String DATABASE = "mDatabase";

  @Override
  public void onCreate() {
    addHelper(DATABASE, new DBBackupHelper(this, DBAdapter.DATABASE_NAME));
  }

}
