package com.genenakagaki.myhandycoach;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.genenakagaki.myhandycoach.dialog.EditExerciseDialog;
import com.genenakagaki.myhandycoach.fragment.ExerciseChooserFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class ExerciseChooserActivity extends AppCompatActivity
        implements View.OnClickListener, ExerciseChooserFragment.OnModeChangeListener {

    public static final String EXTRA_EXERCISE_TYPE = "EXTRA_EXERCISE_TYPE";
    public static final String STATE_EXERCISE_TYPE = "STATE_EXERCISE_TYPE";
    public static final String FRAGMENT_TAG = "fragment_tag";

    @BindView(R.id.fragment_container) FrameLayout fragmentContainer;
    @BindView(R.id.cancel_button) Button cancelButton;
    @BindView(R.id.fab) FloatingActionButton fab;

    private Unbinder unbinder;

    private ExerciseType mExerciseType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_chooser);
        unbinder = ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cancelButton.setOnClickListener(this);
        fab.setOnClickListener(this);

        if (savedInstanceState == null) {
            mExerciseType = (ExerciseType) getIntent().getSerializableExtra(EXTRA_EXERCISE_TYPE);
            switch (mExerciseType) {
                case REGULAR:
                    getSupportActionBar().setTitle(R.string.regular_exercise);
                    break;
                case REACTION:
                    getSupportActionBar().setTitle(R.string.reaction_exercise);
            }

            Fragment fragment = ExerciseChooserFragment.newInstance(mExerciseType);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, FRAGMENT_TAG)
                    .commit();
        } else {
            mExerciseType = (ExerciseType) savedInstanceState.getSerializable(STATE_EXERCISE_TYPE);
            Fragment fragment = getSupportFragmentManager().getFragment(savedInstanceState, FRAGMENT_TAG);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Timber.d("onSaveInstanceState");
        super.onSaveInstanceState(outState);

        outState.putSerializable(STATE_EXERCISE_TYPE, mExerciseType);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        getSupportFragmentManager().putFragment(outState, FRAGMENT_TAG, fragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
//
//    public void setMode(int mode) {
//        if (mode == ExerciseChooserFragment.MODE_CHOOSE) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            switch (mExerciseType) {
//                case REGULAR:
//                    getSupportActionBar().setTitle(R.string.regular_exercise);
//                    break;
//                case REACTION:
//                    getSupportActionBar().setTitle(R.string.reaction_exercise);
//            }
//            cancelButton.setVisibility(View.GONE);
//
//            ExerciseChooserFragment fragment =
//                    (ExerciseChooserFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
//            if (fragment != null) {
//                fragment.setMode(mode);
//            }
//        } else { // MODE_EDIT
//            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
//            getSupportActionBar().setTitle(null);
//            cancelButton.setVisibility(View.VISIBLE);
//        }
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_button:
                Timber.d("onClick cancel_button");
                onModeChanged(ExerciseChooserFragment.MODE_CHOOSE);
                break;
            case R.id.fab:
                EditExerciseDialog dialog = EditExerciseDialog.newInstance(mExerciseType);
                dialog.show(getSupportFragmentManager(), "");
                break;
        }
    }

    @Override
    public void onModeChanged(int mode) {
        Timber.d("onModeChanged");

        if (mode == ExerciseChooserFragment.MODE_CHOOSE) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            switch (mExerciseType) {
                case REGULAR:
                    getSupportActionBar().setTitle(R.string.regular_exercise);
                    break;
                case REACTION:
                    getSupportActionBar().setTitle(R.string.reaction_exercise);
            }
            cancelButton.setVisibility(View.GONE);

        } else { // MODE_EDIT
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setTitle(null);
            cancelButton.setVisibility(View.VISIBLE);
        }

        ExerciseChooserFragment fragment =
                (ExerciseChooserFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null) {
            fragment.setMode(mode);
        }
    }
}
