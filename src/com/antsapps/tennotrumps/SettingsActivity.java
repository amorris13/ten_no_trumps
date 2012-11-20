package com.antsapps.tennotrumps;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceManager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class SettingsActivity extends SherlockPreferenceActivity implements
    OnSharedPreferenceChangeListener {
  private SharedPreferences mSharedPref;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.preferences);

    mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
    mSharedPref.registerOnSharedPreferenceChangeListener(this);

    ActionBar actionBar = getSupportActionBar();

    updateOrientationSummary();
  }

  @Override
  protected void onDestroy() {
    mSharedPref.unregisterOnSharedPreferenceChangeListener(this);

    super.onDestroy();
  }

  @Override
  public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
      String key) {
    if (key.equals(getString(R.string.pref_points_nonbidders))) {
      updateOrientationSummary();
    }
  }

  private void updateOrientationSummary() {
    ListPreference orientationPref = (ListPreference) findPreference(getString(R.string.pref_points_nonbidders));

    orientationPref.setSummary(orientationPref.getEntry());
  }
}
