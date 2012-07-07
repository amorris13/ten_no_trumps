package com.antsapps.tennotrumps.backend;

import java.util.Date;

public class Utils {

  public static long compareTo(Date date1, long id1, Date date2, long id2) {
    if (date1 == null) {
      if (date2 == null) {
        return (int) (id1 - id2);
      } else {
        return -1;
      }
    } else {
      if (date2 == null) {
        return 1;
      } else {
        return date1.compareTo(date2);
      }
    }
  }


  private Utils(){
  }
}
