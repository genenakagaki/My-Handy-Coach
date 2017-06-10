package com.genenakagaki.myhandycoach.fragment;

import android.os.Build;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.genenakagaki.myhandycoach.R;
import com.genenakagaki.myhandycoach.data.ReactionExerciseDb;
import com.genenakagaki.myhandycoach.data.model.ReactionExercise;

import java.util.Locale;
import java.util.Random;

import butterknife.BindView;
import timber.log.Timber;

import static android.speech.tts.TextToSpeech.QUEUE_FLUSH;

/**
 * Created by gene on 5/11/17.
 */

public class ReactionExerciseFragment extends AbstractExerciseFragment {

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

    private ReactionExercise mExercise;

    private int mCurrentSet = 0;
    private int mCurrentRep = 1;

    TextToSpeech mTextToSpeech;

    private CountDownTimer mRestCountDownTimer;
    private CountDownTimer mRepsCountDownTimer;

    @Override
    public void setupExercise() {
        mExercise = ReactionExerciseDb.getCurrentExercise(getActivity());
        getActivity().setTitle(mExercise.name);

        mTextToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                int randomChoice = getRandomChoice();
                speakText(Integer.toString(randomChoice));
                repsProgressText.setText(Integer.toString(randomChoice));
                mRepsCountDownTimer.start();
            }
        });

        mTextToSpeech.setLanguage(Locale.US);

        setsProgressText.setText(getString(R.string.exercise_sets_text, mExercise.sets));

        setsProgressBar.setMax(mExercise.sets * 100);
        setsProgressBar.setProgress(0);

        restProgressBar.setMax(mExercise.restDuration * 100);
        restProgressBar.setProgress(restProgressBar.getMax());

        repsProgressBar.setMax(mExercise.choiceInterval * mExercise.reps * 100);
        repsProgressBar.setProgress(repsProgressBar.getMax());
        repsProgressBottomText.setText(getString(R.string.exercise_reps_text, mExercise.reps-1));

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
                repsProgressBottomText.setText(getString(R.string.exercise_reps_text, mExercise.reps-1));

                int randomChoice = getRandomChoice();
                speakText(Integer.toString(randomChoice));
                repsProgressText.setText(Integer.toString(randomChoice));
                mRepsCountDownTimer.start();
            }
        };

        mRepsCountDownTimer = new CountDownTimer(mExercise.choiceInterval * 1000 + 100, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Timber.d("onTick " + millisUntilFinished);

                int repProgress = (mExercise.reps - mCurrentRep) * mExercise.choiceInterval;
                int seconds = (int) millisUntilFinished / 1000 + repProgress;
                setProgress(repsProgressBar, (seconds - 1) * 100, 1000);
            }

            @Override
            public void onFinish() {
                mCurrentRep++;
                if (mExercise.reps >= mCurrentRep) {
                    int randomChoice = getRandomChoice();
                    speakText(Integer.toString(randomChoice));
                    repsProgressText.setText(Integer.toString(randomChoice));
                    repsProgressBottomText.setText(
                            getString(R.string.exercise_reps_text, mExercise.reps - mCurrentRep));
                    mRepsCountDownTimer.start();
                } else {
                    // reset repsProgressBar
                    repsProgressBar.setProgress(repsProgressBar.getMax());

                    mCurrentRep = 1;
                    repsProgressBar.setVisibility(View.GONE);

                    // set up restProgressBar
                    restProgressBar.setVisibility(View.VISIBLE);
                    repsProgressBottomText.setText(R.string.exercise_rest_text);

                    updateSetsProgress();
                }
            }
        };

    }

    @Override
    public void cancelTimers() {
        if (mRepsCountDownTimer != null) {
            mRepsCountDownTimer.cancel();
        }
        if (mRestCountDownTimer != null) {
            mRestCountDownTimer.cancel();
        }
        if(mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
        }
    }

    @Override
    public void onClickDoneButton(View v) {

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

    private int getRandomChoice() {
        Timber.d("getRandomChoice");

        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int min = 1;
        return rand.nextInt((mExercise.choices - min) + 1) + min;
    }

    private void speakText(String text) {
        Timber.d("speakText");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mTextToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            mTextToSpeech.speak(text, QUEUE_FLUSH, null);
        }
    }
}
