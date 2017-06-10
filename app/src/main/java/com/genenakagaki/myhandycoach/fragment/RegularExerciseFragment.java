package com.genenakagaki.myhandycoach.fragment;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.genenakagaki.myhandycoach.R;
import com.genenakagaki.myhandycoach.data.RegularExerciseDb;
import com.genenakagaki.myhandycoach.data.model.RegularExercise;

import butterknife.BindView;
import timber.log.Timber;

/**
 * Created by gene on 5/10/17.
 */

public class RegularExerciseFragment extends AbstractExerciseFragment {

    @BindView(R.id.content) RelativeLayout contentLayout;
    @BindView(R.id.sets_progressbar) ProgressBar setsProgressBar;
    @BindView(R.id.rest_progressbar) ProgressBar restProgressBar;
    @BindView(R.id.reps_progressbar) ProgressBar repsProgressBar;
    @BindView(R.id.reps_progressbar_container) LinearLayout repsProgressBarContainer;
    @BindView(R.id.sets_progress_text) TextView setsProgressText;
    @BindView(R.id.reps_progress_text) TextView repsProgressText;
    @BindView(R.id.reps_progress_bottom_text) TextView repsProgressBottomText;
    @BindView(R.id.done_button) Button doneButton;
    @BindView(R.id.complete_layout) LinearLayout completeLayout;

    private RegularExercise mExercise;
    private int mCurrentSet = 0;

    private String mBottomRepsString;

    private CountDownTimer mRestCountDownTimer;
    private CountDownTimer mRepsCountDownTimer;

    @Override
    public void setupExercise() {
        mExercise = RegularExerciseDb.getCurrentExercise(getActivity());
        getActivity().setTitle(mExercise.name);

        final boolean isRepsTimed = mExercise.reps == 0;

        mRestCountDownTimer = new CountDownTimer(mExercise.restDuration * 1000 + 500, 1000) {
            public void onTick(long millisUntilFinished) {
                Timber.d("onTick " + millisUntilFinished);
                int seconds = (int) millisUntilFinished / 1000;
                setProgress(restProgressBar, (seconds - 1) * 100, 1000);
                int minutes = seconds / 60;
                seconds -= minutes * 60;
                repsProgressText.setText(String.format("%02d : %02d", minutes, seconds));
            }

            public void onFinish() {
                restProgressBar.setProgress(restProgressBar.getMax());
                restProgressBar.setVisibility(View.GONE);
                repsProgressBar.setVisibility(View.VISIBLE);

                if (isRepsTimed) {
                    mRepsCountDownTimer.start();
                    repsProgressBottomText.setVisibility(View.INVISIBLE);
                } else {
                    repsProgressBottomText.setText(mBottomRepsString);
                    doneButton.setVisibility(View.VISIBLE);
                }

                playAlarm();
            }
        };


        setsProgressText.setText(getString(R.string.exercise_sets_text, mExercise.sets));

        setsProgressBar.setMax(mExercise.sets * 100);
        setsProgressBar.setProgress(0);

        restProgressBar.setMax(mExercise.restDuration * 100);
        restProgressBar.setProgress(restProgressBar.getMax());

        if (isRepsTimed) {
            repsProgressBar.setMax(mExercise.setDuration * 100);
            repsProgressBar.setProgress(mExercise.setDuration * 100);
            repsProgressBottomText.setText(R.string.exercise_rest_text);
            repsProgressBottomText.setVisibility(View.INVISIBLE);

            mRepsCountDownTimer = new CountDownTimer(mExercise.setDuration * 1000 + 500, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    Timber.d("onTick " + millisUntilFinished);
                    int seconds = (int) millisUntilFinished / 1000;
                    setProgress(repsProgressBar, (seconds - 1) * 100, 1000);
                    int minutes = seconds / 60;
                    seconds -= minutes * 60;
                    repsProgressText.setText(String.format("%02d : %02d", minutes, seconds));
                }

                @Override
                public void onFinish() {
                    // reset repsProgressBar
                    repsProgressBar.setProgress(repsProgressBar.getMax());
                    repsProgressBar.setVisibility(View.GONE);

                    // set up restProgressBar
                    restProgressBar.setVisibility(View.VISIBLE);
                    repsProgressBottomText.setVisibility(View.VISIBLE);
                    updateSetsProgress();

                    playAlarm();
                }
            };

            mRepsCountDownTimer.start();
        } else {
            repsProgressText.setText(Integer.toString(mExercise.reps));
            mBottomRepsString = getString(R.string.exercise_reps_text, mExercise.reps);
            repsProgressBottomText.setText(mBottomRepsString);
            doneButton.setVisibility(View.VISIBLE);
        }
    }

    private void updateSetsProgress() {
        Timber.d("updateSetsProgress");

        mCurrentSet++;
        Timber.d("mCurrentSet " + mCurrentSet);
        setProgress(setsProgressBar, mCurrentSet * 100, 500);
        setsProgressText.setText(getString(R.string.exercise_sets_text, mExercise.sets - mCurrentSet));

        if (mExercise.sets - mCurrentSet == 0) {
            Timber.d("all sets completed");

            completeLayout.setVisibility(View.VISIBLE);
            repsProgressBottomText.setVisibility(View.INVISIBLE);
            repsProgressText.setVisibility(View.GONE);
        } else {
            mRestCountDownTimer.start();
        }
    }

    @Override
    public void cancelTimers() {
        if (mRepsCountDownTimer != null) {
            mRepsCountDownTimer.cancel();
        }
        if (mRestCountDownTimer != null) {
            mRestCountDownTimer.cancel();
        }
    }

    @Override
    public void onClickDoneButton(View v) {
        updateSetsProgress();
        repsProgressBottomText.setText(R.string.exercise_rest_text);
        repsProgressBar.setVisibility(View.GONE);
        doneButton.setVisibility(View.GONE);
        restProgressBar.setVisibility(View.VISIBLE);
    }
}
