package com.genenakagaki.myhandycoach;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.genenakagaki.myhandycoach.dialog.EditExerciseDialog;
import com.genenakagaki.myhandycoach.fragment.ExerciseChooserFragment;
import com.genenakagaki.myhandycoach.fragment.MainFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, MainFragment.OnExerciseTypeClickListener, ExerciseChooserFragment.OnModeChangeListener {

    private static final String EXERCISE_CHOOSER_FRAGMENT_TAG = "EXERCISE_CHOOSER_FRAGMENT_TAG";

    private boolean mIsTablet;
    private ExerciseType mExerciseType;

    @BindView(R.id.toolbar) Toolbar toolbar;
    @BindView(R.id.cancel_button) Button cancelButton;
    @BindView(R.id.add_exercise_fab) FloatingActionButton addExerciseFab;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
        setContentView(R.layout.activity_main);
        unbinder = ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        if (findViewById(R.id.fragment_container) == null) {
            Timber.d("Device is tablet");
            mIsTablet = true;
            mExerciseType = ExerciseType.REGULAR;

            addExerciseFab.setVisibility(View.VISIBLE);
            addExerciseFab.setOnClickListener(this);
            cancelButton.setOnClickListener(this);
        } else {
            Timber.d("Device is handset");
            mIsTablet = false;
        }

        if (savedInstanceState == null) {
            if (mIsTablet) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container_left, new MainFragment())
                        .replace(
                                R.id.fragment_container_right,
                                ExerciseChooserFragment.newInstance(mExerciseType),
                                EXERCISE_CHOOSER_FRAGMENT_TAG)
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new MainFragment())
                        .commit();
            }
        }

        // TODO: remove
        ButterKnife.setDebug(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_exercise_fab:
                EditExerciseDialog dialog = EditExerciseDialog.newInstance(mExerciseType);
                dialog.show(getSupportFragmentManager(), "");
                break;
            case R.id.cancel_button:
                onModeChanged(ExerciseChooserFragment.MODE_CHOOSE);
        }
    }

    @Override
    public void onExerciseTypeSelected(ExerciseType exerciseType) {
        mExerciseType = exerciseType;

        if (mIsTablet) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container_right, ExerciseChooserFragment.newInstance(mExerciseType))
                    .commit();
        } else {
            switch (exerciseType) {
                case REGULAR:
                    Intent intent = new Intent(this, ExerciseChooserActivity.class);
                    intent.putExtra(ExerciseChooserActivity.EXTRA_EXERCISE_TYPE, exerciseType);
                    startActivity(intent);
                    break;
                case REACTION:
                    intent = new Intent(this, ExerciseChooserActivity.class);
                    intent.putExtra(ExerciseChooserActivity.EXTRA_EXERCISE_TYPE, exerciseType);
                    startActivity(intent);
                    break;
            }
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

        ExerciseChooserFragment fragment = (ExerciseChooserFragment) getSupportFragmentManager()
                .findFragmentByTag(EXERCISE_CHOOSER_FRAGMENT_TAG);
        if (fragment != null) {
            fragment.setMode(mode);
        }
    }
}
