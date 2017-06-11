package com.genenakagaki.myhandycoach;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;

import com.genenakagaki.myhandycoach.data.AppPreference;
import com.genenakagaki.myhandycoach.fragment.AbstractExerciseFragment;
import com.genenakagaki.myhandycoach.fragment.ReactionExerciseFragment;
import com.genenakagaki.myhandycoach.fragment.RegularExerciseFragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class ExerciseActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_EXERCISE_TYPE = "extra_exercise_type";

    private static final String EXERCISE_FRAGMENT_TAG = "exercise_fragment_tag";
    private static final String ANALYTICS_EXERCISE_RESULT = "exercise_result";

    private ExerciseType mExerciseType;

    @BindView(R.id.activity_exercise) CoordinatorLayout contentLayout;
    @BindView(R.id.toolbar) Toolbar toolbar;

    private Unbinder unbinder;

    private InterstitialAd mInterstitialAd;
    private boolean mIsAdShown = false;

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        unbinder = ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mExerciseType = (ExerciseType) getIntent().getSerializableExtra(EXTRA_EXERCISE_TYPE);

        if (savedInstanceState == null) {
            switch (mExerciseType) {
                case REGULAR:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new RegularExerciseFragment(), EXERCISE_FRAGMENT_TAG)
                            .commit();
                    break;
                case REACTION:
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ReactionExerciseFragment(), EXERCISE_FRAGMENT_TAG)
                            .commit();
            }
        }

        contentLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Adjust views
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    contentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    contentLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                onBackPressed();
            }
        });

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onBackPressed() {
        if (mIsAdShown) {
            Timber.d("onBackPressed mIsAdShown");

            super.onBackPressed();
            return;
        }

        AbstractExerciseFragment fragment =
                (AbstractExerciseFragment) getSupportFragmentManager().findFragmentByTag(EXERCISE_FRAGMENT_TAG);

        if (fragment.isCompleted()) {
            Timber.d("onBackPressed exerciseCompleted");

            AppPreference.setLastExerciseDate(this, Calendar.getInstance().getTime());

            Bundle bundle = new Bundle();
            bundle.putString(ANALYTICS_EXERCISE_RESULT, "Exercise Completed");
            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            super.onBackPressed();
        } else {
            if (mInterstitialAd.isLoaded()) {
                Timber.d("onBackPressed show ad");

                Bundle bundle = new Bundle();
                bundle.putString(ANALYTICS_EXERCISE_RESULT, "Exercise Incomplete");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                mInterstitialAd.show();
                mIsAdShown = true;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
    }
}
