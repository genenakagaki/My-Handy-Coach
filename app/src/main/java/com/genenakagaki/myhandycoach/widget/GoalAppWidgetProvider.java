package com.genenakagaki.myhandycoach.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.genenakagaki.myhandycoach.MainActivity;
import com.genenakagaki.myhandycoach.R;
import com.genenakagaki.myhandycoach.data.AppPreference;
import com.genenakagaki.myhandycoach.exception.PreferenceNotFoundException;

import java.util.Calendar;
import java.util.Date;

import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by gene on 6/7/17.
 */

public class GoalAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;

        // Perform this loop procedure for each App Widget that belongs to this provider
//        for (int i = 0; i < N; i++) {
//            int appWidgetId = appWidgetIds[i];
//
//            // Get the layout for the App Widget and attach an on-click listener
//            // to the button
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.appwidget_goal);
//
//            try {
//                long dateLong = AppPreference.getLastExerciseDate(context);
//                Date lastUsedDate = new Date(dateLong);
//
//                Calendar calendar = Calendar.getInstance();
//                int currentDay = calendar.get(Calendar.DAY_OF_YEAR);
//                int currentYear = calendar.get(Calendar.YEAR);
//
//                calendar.setTime(lastUsedDate);
//                int lastUsedDay = calendar.get(Calendar.DAY_OF_YEAR);
//                int lastUsedYear = calendar.get(Calendar.YEAR);
//
//                if (currentDay == lastUsedDay && currentYear == lastUsedYear) {
//                    Timber.d("daily goal complete");
//                    views.setViewVisibility(R.id.goal_imcomplete_image_layout, View.GONE);
//                    views.setViewVisibility(R.id.goal_complete_image_layout, View.VISIBLE);
//                } else {
//                    Timber.d("daily goal incomplete");
//                    views.setViewVisibility(R.id.goal_imcomplete_image_layout, View.VISIBLE);
//                    views.setViewVisibility(R.id.goal_complete_image_layout, View.GONE);
//                }
//
//            } catch (PreferenceNotFoundException e) {
//                Timber.d(e.getMessage());
//                e.printStackTrace();
//            }
//
//            // Tell the AppWidgetManager to perform an update on the current app widget
//            appWidgetManager.updateAppWidget(appWidgetId, views);
//
//        }


    }
}
