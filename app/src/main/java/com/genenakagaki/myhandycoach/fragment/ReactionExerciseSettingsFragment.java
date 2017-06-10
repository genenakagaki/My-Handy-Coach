package com.genenakagaki.myhandycoach.fragment;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.genenakagaki.myhandycoach.ExerciseChooserActivity;
import com.genenakagaki.myhandycoach.ExerciseType;
import com.genenakagaki.myhandycoach.MainActivity;
import com.genenakagaki.myhandycoach.R;
import com.genenakagaki.myhandycoach.data.ReactionExerciseDb;
import com.genenakagaki.myhandycoach.data.model.ReactionExercise;
import com.genenakagaki.myhandycoach.view.DurationPicker;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by gene on 5/10/17.
 */

public class ReactionExerciseSettingsFragment extends AbstractExerciseSettingsFragment {

    @BindView(R.id.exercise_input) TextInputEditText exerciseInput;
    @BindView(R.id.sets_input) EditText setsInput;
    @BindView(R.id.sets_inputlayout) TextInputLayout setsInputLayout;
    @BindView(R.id.reps_input) TextInputEditText repsInput;
    @BindView(R.id.reps_inputlayout) TextInputLayout repsInputLayout;
    @BindView(R.id.choices_input) TextInputEditText choicesInput;
    @BindView(R.id.choices_inputlayout) TextInputLayout choicesInputLayout;
    @BindView(R.id.rest_duration_picker) DurationPicker restDurationPicker;
    @BindView(R.id.duration_picker_bottom) DurationPicker choiceDurationPicker;
    @BindView(R.id.discard_changes_button) Button discardChangesButton;

    private ReactionExercise mReactionExercise;

    @Override
    public void setupView() {
        Timber.d("setupView");
        mReactionExercise = ReactionExerciseDb.getCurrentExercise(getActivity());

        choiceDurationPicker.setEnabledLabel(getString(R.string.reaction_exercise_setting_choice_interval));
        choiceDurationPicker.setSwitchVisibility(View.INVISIBLE);

        exerciseInput.setText(mReactionExercise.name);
        repsInput.setText(Integer.toString(mReactionExercise.reps));
        setsInput.setText(Integer.toString(mReactionExercise.sets));
        choicesInput.setText(Integer.toString(mReactionExercise.choices));
        choiceDurationPicker.setDuration(mReactionExercise.choiceInterval);
        restDurationPicker.setDuration(mReactionExercise.restDuration);
    }

    @Override
    public void saveExerciseSettings() {
        mReactionExercise.reps = Integer.valueOf(repsInput.getText().toString());
        mReactionExercise.sets = Integer.valueOf(setsInput.getText().toString());
        mReactionExercise.choices = Integer.valueOf(choicesInput.getText().toString());
        mReactionExercise.choiceInterval = choiceDurationPicker.getDuration();
        mReactionExercise.restDuration = restDurationPicker.getDuration();
        ReactionExerciseDb.updateExercise(getActivity(), mReactionExercise);
    }

    @Override
    public boolean validateInputs() {
        boolean isValid = true;

        if (Integer.valueOf(setsInput.getText().toString()) == 0) {
            setsInputLayout.setErrorEnabled(true);
            setsInputLayout.setError(getString(R.string.exercise_setting_error_empty_input));
            isValid = false;
        }
        if (Integer.valueOf(repsInput.getText().toString()) == 0) {
            repsInputLayout.setErrorEnabled(true);
            repsInputLayout.setError(getString(R.string.exercise_setting_error_empty_input));
            isValid = false;
        }
        if (Integer.valueOf(choicesInput.getText().toString()) == 0) {
            choicesInputLayout.setErrorEnabled(true);
            choicesInputLayout.setError(getString(R.string.exercise_setting_error_empty_input));
            isValid = false;
        }
        if (choiceDurationPicker.getDuration() == 0) {
            choiceDurationPicker.setErrorEnabled(true);
            isValid = false;
        }
        if (restDurationPicker.getDuration() == 0) {
            restDurationPicker.setErrorEnabled(true);
            isValid = false;
        }

        return isValid;
    }

    @Override
    public void onClickExerciseInput(View v) {
        Timber.d("onClick exerciseInput");

        Intent intent = new Intent(getActivity(), ExerciseChooserActivity.class);
        intent.putExtra(ExerciseChooserActivity.EXTRA_EXERCISE_TYPE, ExerciseType.REACTION);
        startActivity(intent);
    }
}
