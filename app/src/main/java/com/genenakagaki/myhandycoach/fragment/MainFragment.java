package com.genenakagaki.myhandycoach.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.genenakagaki.myhandycoach.ExerciseType;
import com.genenakagaki.myhandycoach.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

public class MainFragment extends Fragment implements View.OnClickListener {

    public interface OnExerciseTypeClickListener {
        void onExerciseTypeSelected(ExerciseType exerciseType);
    }

    @BindView(R.id.regular_exercise_button) Button regularButton;
    @BindView(R.id.reaction_exercise_button) Button reactionButton;

    private OnExerciseTypeClickListener mListener;

    private Unbinder unbinder;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        regularButton.setOnClickListener(this);
        reactionButton.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnExerciseTypeClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnExerciseTypeClickListener");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.regular_exercise_button:
                Timber.d("onClick regular exercise button");

                mListener.onExerciseTypeSelected(ExerciseType.REGULAR);
                break;
            case R.id.reaction_exercise_button:
                Timber.d("onClick reaction exercise button");

                mListener.onExerciseTypeSelected(ExerciseType.REACTION);
                break;
        }

    }
}
