package com.genenakagaki.myhandycoach.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.genenakagaki.myhandycoach.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by gene on 5/10/17.
 */

public abstract class AbstractExerciseSettingsFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.exercise_input) TextInputEditText exerciseInput;
    @BindView(R.id.discard_changes_button) Button discardChangesButton;

    private Unbinder unbinder;

    public AbstractExerciseSettingsFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Timber.d("onCreateView");

        View rootView = inflater.inflate(R.layout.fragment_exercise_settings, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        setupView();

        exerciseInput.setOnClickListener(this);
        discardChangesButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Timber.d("onSaveInstanceState");
        saveExerciseSettings();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Timber.d("onDestroyView");
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == exerciseInput.getId()) {
            onClickExerciseInput(v);

        } else if (v.getId() == discardChangesButton.getId()) {
            Timber.d("onClick discardChangesButton");

            // recreate fragment without saving changes
            setupView();
        }
    }

    public abstract void setupView();
    public abstract void saveExerciseSettings();
    public abstract boolean validateInputs();
    public abstract void onClickExerciseInput(View v);
}
