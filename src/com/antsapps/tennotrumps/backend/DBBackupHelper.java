package com.antsapps.tennotrumps.backend;

import android.app.backup.FileBackupHelper;
import android.content.Context;

public class DBBackupHelper extends FileBackupHelper {
  public DBBackupHelper(Context context, String dbName) {
    super(context, context.getDatabasePath(dbName).getAbsolutePath());
  }
}
