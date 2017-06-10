package com.genenakagaki.myhandycoach;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.genenakagaki.myhandycoach.dialog.EditExerciseDialog;
import com.genenakagaki.myhandycoach.fragment.ExerciseChooserFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ExerciseChooserActivity extends AppCompatActivity {

    public static final String EXTRA_EXERCISE_TYPE = "extra_exercise_type";

    @BindView(R.id.fragment_container) FrameLayout fragmentContainer;

    private Unbinder unbinder;

    private ExerciseType mExerciseType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_chooser);
        unbinder = ButterKnife.bind(this);

        mExerciseType = (ExerciseType) getIntent().getSerializableExtra(EXTRA_EXERCISE_TYPE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditExerciseDialog dialog = EditExerciseDialog.newInstance(mExerciseType);
                dialog.show(getSupportFragmentManager(), "");
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, ExerciseChooserFragment.newInstance(mExerciseType))
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
