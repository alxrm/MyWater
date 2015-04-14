package com.rm.mywater.base;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import com.rm.mywater.database.DrinkHistoryDatabase;
import com.rm.mywater.model.Day;
import com.rm.mywater.util.Prefs;
import com.rm.mywater.util.TimeUtil;

/**
 * Created by alex on 14/04/15.
 */
public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long savedToday = Prefs.getSavedToday();

        if (savedToday != TimeUtil.getToday()) {

            Log.d("SavedToday", "Today = " + savedToday);

            Prefs.clear();

            DrinkHistoryDatabase.addDay(
                    this,
                    new Day(
                            Prefs.getPercent(),
                            TimeUtil.getToday()
                    )
            );

            Prefs.saveToday();

            Prefs.put(Prefs.KEY_DB_DAY_EXISTS, true);

        } else {

            Log.d("SavedToday", "Today == Saved");

            Prefs.put(Prefs.KEY_DB_DAY_EXISTS, true);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);

        // TODO implement toolbar
    }
}
