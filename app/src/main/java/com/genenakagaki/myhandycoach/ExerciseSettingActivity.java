package com.genenakagaki.myhandycoach;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.genenakagaki.myhandycoach.fragment.AbstractExerciseSettingsFragment;
import com.genenakagaki.myhandycoach.fragment.ReactionExerciseSettingsFragment;
import com.genenakagaki.myhandycoach.fragment.RegularExerciseSettingsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ExerciseSettingActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_EXERCISE_TYPE = "EXTRA_EXERCISE_TYPE";
    public static final String EXTRA_EXERCISE_ID = "EXTRA_EXERCISE_ID";
    private static final String FRAGMENT_TAG = "FRAGMENT_TAG";

    private ExerciseType mExerciseType;
    private long mExerciseId;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.start_exercise_button) Button startExerciseButton;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_setting);
        unbinder = ButterKnife.bind(this);

        mExerciseType = (ExerciseType) getIntent().getSerializableExtra(EXTRA_EXERCISE_TYPE);
        mExerciseId = getIntent().getLongExtra(EXTRA_EXERCISE_ID, -1);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        switch (mExerciseType) {
            case REGULAR:
                getSupportActionBar().setTitle(R.string.regular_exercise);
                break;
            case REACTION:
                getSupportActionBar().setTitle(R.string.reaction_exercise);
        }

        startExerciseButton.setOnClickListener(this);

        if (savedInstanceState == null) {
            Fragment fragment;
            switch (mExerciseType) {
                case REGULAR:
                    fragment = new RegularExerciseSettingsFragment();
                    break;
                default:
                    fragment = new ReactionExerciseSettingsFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment, FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == startExerciseButton.getId()) {
            AbstractExerciseSettingsFragment fragment =
                    (AbstractExerciseSettingsFragment) getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);

            boolean isValid = fragment.validateInputs();
            if (isValid) {
                fragment.saveExerciseSettings();

                Intent intent = new Intent(this, ExerciseActivity.class);
                switch (mExerciseType) {
                    case REGULAR:
                        intent.putExtra(ExerciseActivity.EXTRA_EXERCISE_TYPE, ExerciseType.REGULAR);
                        break;
                    case REACTION:
                        intent.putExtra(ExerciseActivity.EXTRA_EXERCISE_TYPE, ExerciseType.REACTION);
                        break;
                }

                startActivity(intent);
            }
        }
    }
}
