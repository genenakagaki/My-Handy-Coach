package com.genenakagaki.myhandycoach.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.genenakagaki.myhandycoach.ExerciseType;
import com.genenakagaki.myhandycoach.R;
import com.genenakagaki.myhandycoach.data.ReactionExerciseDb;
import com.genenakagaki.myhandycoach.data.RegularExerciseDb;

import java.util.List;

/**
 * Created by gene on 4/29/17.
 */

public class DeleteExerciseDialog extends DialogFragment {

    public static final String ARG_EXERCISE_TYPE = "arg_exercise_type";
    public static final String ARG_EXERCISE_ID = "arg_exercise_id";

    private ExerciseType mExerciseType;
    private long[] mSelectedExerciseIds;

    public static DeleteExerciseDialog newInstance(ExerciseType exerciseType, List<Long> selectedExerciseIds) {
        // Convert List<Long> to long[]
        long[] selectedExerciseIdsArray = new long[selectedExerciseIds.size()];
        for (int i = 0; i < selectedExerciseIds.size(); i++) {
            selectedExerciseIdsArray[i] = selectedExerciseIds.get(i);
        }

        Bundle args = new Bundle();
        args.putSerializable(ARG_EXERCISE_TYPE, exerciseType);
        args.putLongArray(ARG_EXERCISE_ID, selectedExerciseIdsArray);
        DeleteExerciseDialog fragment = new DeleteExerciseDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public DeleteExerciseDialog() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mExerciseType = (ExerciseType) getArguments().getSerializable(ARG_EXERCISE_TYPE);
            mSelectedExerciseIds = getArguments().getLongArray(ARG_EXERCISE_ID);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        String exerciseName;
//        try {
//            switch (mExerciseType) {
//                case REGULAR:
//                    exerciseName = RegularExerciseDb.getExercise(getActivity(), mSelectedExerciseIds).name;
//                    break;
//                default: // REACTION
//                    exerciseName = ReactionExerciseDb.getExercise(getActivity(), mSelectedExerciseIds).name;
//                    break;
//            }
//        } catch (ExerciseNotFoundException e) {
//            Timber.d(e.getMessage());
//            e.printStackTrace();
//            return null;
//        }
//
//        String title = getString(R.string.dialog_title_delete, exerciseName);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.delete_dialog_title_selected)
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteSelectedExercises();
                    }
                })
                .setNegativeButton(R.string.cancel, null);
        final AlertDialog alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(getActivity(), R.color.error));
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
            }
        });
        return alertDialog;
    }

    private void deleteSelectedExercises() {
        switch (mExerciseType) {
            case REGULAR:
                for (long exerciseId : mSelectedExerciseIds) {
                    RegularExerciseDb.deleteExercise(getActivity(), exerciseId);
                }
                break;
            case REACTION:
                for (long exerciseId : mSelectedExerciseIds) {
                    ReactionExerciseDb.deleteExercise(getActivity(), exerciseId);
                }
        }
    }

}
