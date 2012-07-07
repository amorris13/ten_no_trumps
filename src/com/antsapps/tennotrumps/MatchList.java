package com.antsapps.tennotrumps;

import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.antsapps.tennotrumps.backend.Application;
import com.antsapps.tennotrumps.backend.Match;
import com.antsapps.tennotrumps.backend.OnStateChangedListener;
import com.antsapps.tennotrumps.backend.Team;
import com.google.common.collect.Lists;

public class MatchList extends ListActivity implements OnStateChangedListener {
  private Application application;

  private List<Match> mMatches = null;
  private TableArrayAdapter mAdapter;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.match_list);
    application = Application.getInstance(getApplicationContext());

    mMatches = Lists.reverse(application.getMatches());
    mAdapter = new TableArrayAdapter(this, R.layout.match_list_item, mMatches);
    setListAdapter(mAdapter);

    ListView lv = getListView();
    lv.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position,
          long id) {
        Match match = mMatches.get(position);
        Intent intent = new Intent(view.getContext(), RoundList.class);
        intent.putExtra(Match.ID_TAG, match.getId());
        startActivity(intent);
      }
    });

    lv.setOnItemLongClickListener(new OnItemLongClickListener() {
      @Override
      public boolean onItemLongClick(AdapterView<?> parent, View view,
          final int position, long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MatchList.this);
        builder.setCancelable(true);
        builder.setTitle("Delete?");
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            Match match = mMatches.get(position);
            application.deleteMatch(match);
            dialog.dismiss();
          }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
          }
        });
        AlertDialog alert = builder.create();
        alert.show();
        return false;
      }
    });

    application.addOnStateChangedListener(this);
  }

  @Override
  public void onStart() {
    super.onStart();
    TableArrayAdapter adapter = (TableArrayAdapter) getListView().getAdapter();
    adapter.notifyDataSetChanged();
  }

  @Override
  protected void onResume() {
    super.onResume();
    TableArrayAdapter adapter = (TableArrayAdapter) getListView().getAdapter();
    adapter.notifyDataSetChanged();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.match_list, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle item selection
    switch (item.getItemId()) {
      case R.id.new_match:
        Intent intent = new Intent(getBaseContext(), AddMatch.class);
        startActivity(intent);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  private class TableArrayAdapter extends ArrayAdapter<Match> {

    private final List<Match> matches;

    public TableArrayAdapter(Context context,
        int textViewResourceId,
        List<Match> matches) {
      super(context, textViewResourceId, matches);
      this.matches = matches;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      View v = convertView;
      if (v == null) {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = vi.inflate(R.layout.match_list_item, null);
      }

      Match m = matches.get(position);
      if (m != null) {
        Team team1 = m.getTeam1();
        ((TextView) v.findViewById(R.id.team1wins)).setText(Integer.toString(m
            .getRoundsWon(team1)));
        ((TextView) v.findViewById(R.id.team1name)).setText(team1.getName());

        Team team2 = m.getTeam2();
        ((TextView) v.findViewById(R.id.team2wins)).setText(Integer.toString(m
            .getRoundsWon(team2)));
        ((TextView) v.findViewById(R.id.team2name)).setText(team2.getName());

        highlightIfFinished(v, m);

        ((TextView) v.findViewById(R.id.lastplayed)).setText(DateUtils
            .getRelativeTimeSpanString(m.getDate() != null ? m.getDate()
                .getTime() : 0, System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS, DateUtils.FORMAT_ABBREV_RELATIVE));
      }

      return v;

    }

    private void highlightIfFinished(View v, Match m) {
      if(!m.isFinished()){
        setBold(v, R.id.team1wins);
        setBold(v, R.id.team2wins);
      } else {
        setNormal(v, R.id.team1wins);
        setNormal(v, R.id.team2wins);
      }
    }

    private void setBold(View v, int id) {
      ((TextView) v.findViewById(id)).setTypeface(Typeface.DEFAULT,
          Typeface.BOLD);
    }

    private void setNormal(View v, int id) {
      ((TextView) v.findViewById(id)).setTypeface(Typeface.DEFAULT,
          Typeface.NORMAL);
    }
  }

  @Override
  public void onStateChanged() {
    Log.i("MatchList", "onStateChanged called");
    mAdapter.notifyDataSetChanged();
  }
}