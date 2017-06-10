package com.genenakagaki.myhandycoach.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.genenakagaki.myhandycoach.ExerciseType;
import com.genenakagaki.myhandycoach.R;
import com.genenakagaki.myhandycoach.RequiredInputValidator;
import com.genenakagaki.myhandycoach.data.ReactionExerciseDb;
import com.genenakagaki.myhandycoach.data.RegularExerciseDb;
import com.genenakagaki.myhandycoach.data.model.ReactionExercise;
import com.genenakagaki.myhandycoach.data.model.RegularExercise;
import com.genenakagaki.myhandycoach.exception.ExerciseAlreadyExistsException;
import com.genenakagaki.myhandycoach.exception.ExerciseNotFoundException;
import com.genenakagaki.myhandycoach.task.InsertExerciseTask;
import com.genenakagaki.myhandycoach.task.UpdateExerciseNameTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;


/**
 * Created by gene on 4/18/17.
 */

public class EditExerciseDialog extends DialogFragment {

    private static final String ARG_EXERCISE_ID = "arg_exercise_id";
    private static final String ARG_EXERCISE_TYPE = "arg_exercise_type";

    private long mExerciseId;
    private ExerciseType mExerciseType;

    private boolean mIsNewExercise;

    private RequiredInputValidator mExerciseNameValidator;

    @BindView(R.id.exercise_name_input) TextInputEditText exerciseNameInput;
    @BindView(R.id.exercise_name_inputlayout) TextInputLayout exerciseNameInputLayout;

    private Unbinder unbinder;

    public static EditExerciseDialog newInstance(ExerciseType exerciseType) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_EXERCISE_TYPE, exerciseType);
        EditExerciseDialog dialog = new EditExerciseDialog();
        dialog.setArguments(args);
        return dialog;
    }

    public static EditExerciseDialog newInstance(ExerciseType exerciseType, long exerciseId) {
        Bundle args = new Bundle();
        args.putLong(ARG_EXERCISE_ID, exerciseId);
        args.putSerializable(ARG_EXERCISE_TYPE, exerciseType);
        EditExerciseDialog dialog = new EditExerciseDialog();
        dialog.setArguments(args);
        return dialog;
    }

    public EditExerciseDialog() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mExerciseType = (ExerciseType) getArguments().getSerializable(ARG_EXERCISE_TYPE);
            mExerciseId = getArguments().getLong(ARG_EXERCISE_ID, -1);

            if (mExerciseId == -1) {
                mIsNewExercise = true;
            } else {
                mIsNewExercise = false;
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_edit_exercise, null);
        unbinder = ButterKnife.bind(this, view);

        mExerciseNameValidator = new RequiredInputValidator(
                exerciseNameInputLayout,
                exerciseNameInput,
                getString(R.string.error_empty));

        String dialogTitle;
        if (mIsNewExercise) {
            dialogTitle = getString(R.string.dialog_title_add);
        } else {
            dialogTitle = getString(R.string.dialog_title_edit);

            try {
                switch (mExerciseType) {
                    case REGULAR:
                        RegularExercise regularExercise =
                                RegularExerciseDb.getExercise(getActivity(), mExerciseId);
                        exerciseNameInput.setText(regularExercise.name);
                        break;
                    case REACTION:
                        ReactionExercise reactionExercise =
                                ReactionExerciseDb.getExercise(getActivity(), mExerciseId);
                        exerciseNameInput.setText(reactionExercise.name);
                        break;
                }
            } catch (ExerciseNotFoundException e) {
                Timber.d(e.getMessage());
                e.printStackTrace();
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(dialogTitle)
                .setView(view)
                .setPositiveButton(R.string.save, null)
                .setNegativeButton(R.string.cancel, null);
        return builder.create();
    }

    @Override
    public void onStart() {
        // super.onStart() is where dialog.show() is called on the underlying dialog,
        // so we set the onClickListener here to prevent dialog from closing after pressing save
        super.onStart();
        final AlertDialog dialog = (AlertDialog)getDialog();
        if(dialog != null)
        {
            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mExerciseNameValidator.validate();

                    if (mExerciseNameValidator.isValid()) {
                        if (mIsNewExercise) {
                            addExercise(dialog);
                        } else {
                            updateExerciseName(dialog);
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void handleDuplicateName(boolean isDuplicateName) {
        if (isDuplicateName) {
            mExerciseNameValidator.showError(getString(R.string.error_duplicate));
        } else {
            getDialog().dismiss();
        }
    }

    private void addExercise(AlertDialog dialog) {
        String exerciseName = exerciseNameInput.getText().toString().trim();

        new InsertExerciseTask(this, mExerciseType).execute(exerciseName);

//        try {
//            switch (mExerciseType) {
//                case REGULAR:
//                    RegularExerciseDb.insertExercise(getActivity(), exerciseName);
//                    break;
//                case REACTION:
//                    ReactionExerciseDb.insertExercise(getActivity(), exerciseName);
//                    break;
//            }
//        } catch (ExerciseAlreadyExistsException e) {
//            Timber.d(e.getMessage());
//
//            mExerciseNameValidator.showError(getString(R.string.error_duplicate));
//        }

//        if (mExerciseNameValidator.isValid()) {
//            dialog.dismiss();
//        }
    }

    private void updateExerciseName(AlertDialog dialog) {
        String exerciseName = exerciseNameInput.getText().toString().trim();

        new UpdateExerciseNameTask(this, mExerciseType).execute(exerciseName);

//        switch (mExerciseType) {
//            case REGULAR:
//                RegularExerciseDb.updateExerciseName(getActivity(), mExerciseId, exerciseName);
//                break;
//            case REACTION:
//                ReactionExerciseDb.updateExerciseName(getActivity(), mExerciseId, exerciseName);
//        }

//        if (mExerciseNameValidator.isValid()) {
//            dialog.dismiss();
//        }
    }

}
