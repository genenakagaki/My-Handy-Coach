package com.genenakagaki.myhandycoach.fragment;

import android.animation.ObjectAnimator;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.genenakagaki.myhandycoach.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by gene on 5/10/17.
 */

public abstract class AbstractExerciseFragment extends Fragment implements View.OnClickListener {

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

    private Unbinder unbinder;

    private Ringtone mAlarm;

    public AbstractExerciseFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mAlarm = RingtoneManager.getRingtone(getActivity(), notification);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exercise, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        contentLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Adjust views
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    contentLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    contentLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                int size = setsProgressBar.getWidth();
                setsProgressBar.setLayoutParams(new RelativeLayout.LayoutParams(size, size));

                size = repsProgressBar.getWidth();
                repsProgressBar.setLayoutParams(new RelativeLayout.LayoutParams(size, size));
                restProgressBar.setLayoutParams(new RelativeLayout.LayoutParams(size, size));

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        (int) (size / 1.1f),
                        (int) (size / 1.1f));
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                layoutParams.setMargins(0, 0, 0, 0);
                doneButton.setLayoutParams(layoutParams);
                completeLayout.setLayoutParams(layoutParams);

                layoutParams = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, (int) (size / 5f), 0, 0);
                repsProgressBarContainer.setLayoutParams(layoutParams);
            }
        });

        setupExercise();

        doneButton.setOnClickListener(this);
        completeLayout.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        cancelTimers();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == doneButton.getId()) {
            onClickDoneButton(v);

        } else if (v.getId() == completeLayout.getId()) {
            getActivity().onBackPressed();
        }
    }

    public void setProgress(ProgressBar progressBar, int progress, long duration) {
        if(android.os.Build.VERSION.SDK_INT >= 11){
            // will update the "progress" propriety of seekbar until it reaches progress
            ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", progress);
            animation.setDuration(duration);
            animation.setInterpolator(new LinearInterpolator());
            animation.start();
        } else {
            progressBar.setProgress(progress);
        }
    }

    public boolean isCompleted() {
        return completeLayout.getVisibility() == View.VISIBLE;
    }

    public void pause() {

    }

    public void playAlarm() {
        mAlarm.play();
    }


    public abstract void setupExercise();
    public abstract void cancelTimers();
    public abstract void onClickDoneButton(View v);

}
