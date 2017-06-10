package com.genenakagaki.myhandycoach.fragment;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;

import com.genenakagaki.myhandycoach.ExerciseChooserActivity;
import com.genenakagaki.myhandycoach.ExerciseType;
import com.genenakagaki.myhandycoach.MainActivity;
import com.genenakagaki.myhandycoach.R;
import com.genenakagaki.myhandycoach.data.RegularExerciseDb;
import com.genenakagaki.myhandycoach.data.model.RegularExercise;
import com.genenakagaki.myhandycoach.exception.ExerciseNotFoundException;
import com.genenakagaki.myhandycoach.view.DurationPicker;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by gene on 5/10/17.
 */

public class RegularExerciseSettingsFragment extends AbstractExerciseSettingsFragment {

    @BindView(R.id.exercise_input) TextInputEditText exerciseInput;
    @BindView(R.id.sets_input) EditText setsInput;
    @BindView(R.id.sets_inputlayout) TextInputLayout setsInputLayout;
    @BindView(R.id.reps_input) TextInputEditText repsInput;
    @BindView(R.id.reps_inputlayout) TextInputLayout repsInputLayout;
    @BindView(R.id.choices_inputlayout) TextInputLayout choicesInputLayout;
    @BindView(R.id.rest_duration_picker) DurationPicker restDurationPicker;
    @BindView(R.id.duration_picker_bottom) DurationPicker setDurationPicker;

    private RegularExercise mRegularExercise;

    @Override
    public void setupView() {
        Timber.d("setupView");
        mRegularExercise = RegularExerciseDb.getCurrentExercise(getActivity());
        try {
            mRegularExercise = RegularExerciseDb.getExercise(getActivity(), mRegularExercise.id);
        } catch (ExerciseNotFoundException e) {
            mRegularExercise = RegularExerciseDb.insertDefaultExercise(getActivity());
        }

        choicesInputLayout.setVisibility(View.GONE);
        setDurationPicker.setViewsToEnableOnSwitch(repsInputLayout);
        setDurationPicker.setEnabledLabel(getString(R.string.regular_set_duration_enabled));
        setDurationPicker.setDisabledLabel(getString(R.string.regular_set_duration_disabled));

        if (mRegularExercise.reps == 0) {
            repsInput.setEnabled(false);
        } else {
            setDurationPicker.setEnabled(false);
        }

        exerciseInput.setText(mRegularExercise.name);
        repsInput.setText(Integer.toString(mRegularExercise.reps));
        setsInput.setText(Integer.toString(mRegularExercise.sets));
        setDurationPicker.setDuration(mRegularExercise.setDuration);
        restDurationPicker.setDuration(mRegularExercise.restDuration);
    }

    @Override
    public void saveExerciseSettings() {
        mRegularExercise.sets = Integer.valueOf(setsInput.getText().toString());
        mRegularExercise.restDuration = restDurationPicker.getDuration();

        if (repsInput.isEnabled()) {
            mRegularExercise.reps = Integer.valueOf(repsInput.getText().toString());
            mRegularExercise.setDuration = 0;
        } else {
            mRegularExercise.reps = 0;
            mRegularExercise.setDuration = setDurationPicker.getDuration();
        }

        RegularExerciseDb.updateExercise(getActivity(), mRegularExercise);
    }

    @Override
    public boolean validateInputs() {
        boolean isValid = true;

        if (Integer.valueOf(setsInput.getText().toString()) == 0) {
            setsInputLayout.setErrorEnabled(true);
            setsInputLayout.setError(getString(R.string.exercise_setting_error_empty_input));
            isValid = false;
        }

        if (restDurationPicker.getDuration() == 0) {
            restDurationPicker.setErrorEnabled(true);
            isValid = false;
        }

        if (repsInput.isEnabled()) {
            if (Integer.valueOf(repsInput.getText().toString()) == 0) {
                repsInputLayout.setErrorEnabled(true);
                repsInputLayout.setError(getString(R.string.exercise_setting_error_empty_input));
                isValid = false;
            }
        } else {
            if (setDurationPicker.getDuration() == 0) {
                setDurationPicker.setErrorEnabled(true);
                isValid = false;
            }
        }

        return isValid;
    }

    @Override
    public void onClickExerciseInput(View v) {
        Timber.d("onClick exerciseInput");

        Intent intent = new Intent(getActivity(), ExerciseChooserActivity.class);
        intent.putExtra(ExerciseChooserActivity.EXTRA_EXERCISE_TYPE, ExerciseType.REGULAR);
        startActivity(intent);
    }
}
