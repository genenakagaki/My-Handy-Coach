package com.genenakagaki.myhandycoach.task;

import android.content.Context;
import android.os.AsyncTask;

import com.genenakagaki.myhandycoach.ExerciseType;
import com.genenakagaki.myhandycoach.R;
import com.genenakagaki.myhandycoach.RequiredInputValidator;
import com.genenakagaki.myhandycoach.data.ReactionExerciseDb;
import com.genenakagaki.myhandycoach.data.RegularExerciseDb;
import com.genenakagaki.myhandycoach.data.model.RegularExercise;
import com.genenakagaki.myhandycoach.dialog.EditExerciseDialog;
import com.genenakagaki.myhandycoach.exception.ExerciseAlreadyExistsException;

/**
 * Created by gene on 6/5/17.
 */

public class InsertExerciseTask extends AsyncTask<String, Void, Boolean> {

    private EditExerciseDialog mDialog;
    private Context mContext;
    private ExerciseType mExerciseType;

    public InsertExerciseTask(EditExerciseDialog dialog, ExerciseType exerciseType) {
        mDialog = dialog;
        mContext = dialog.getActivity();
        mExerciseType = exerciseType;
    }

    @Override
    protected Boolean doInBackground(String... name) {
        boolean isDuplicateName = false;

        switch (mExerciseType) {
            case REGULAR:
                try {
                    RegularExerciseDb.insertExercise(mContext, name[0]);
                } catch (ExerciseAlreadyExistsException e) {
                    isDuplicateName = true;
                }
                break;
            default: // REACTION
                try {
                    ReactionExerciseDb.insertExercise(mContext, name[0]);
                } catch (ExerciseAlreadyExistsException e) {
                    isDuplicateName = true;
                }
        }
        return isDuplicateName;
    }

    @Override
    protected void onPostExecute(Boolean isDuplicateName) {
        super.onPostExecute(isDuplicateName);

        mDialog.handleDuplicateName(isDuplicateName);
    }
}
