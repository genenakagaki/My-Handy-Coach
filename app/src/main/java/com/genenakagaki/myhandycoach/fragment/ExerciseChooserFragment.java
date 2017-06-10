package com.genenakagaki.myhandycoach.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.genenakagaki.myhandycoach.ExerciseType;
import com.genenakagaki.myhandycoach.adapter.ExerciseChooserCursorAdapter;
import com.genenakagaki.myhandycoach.MainActivity;
import com.genenakagaki.myhandycoach.R;
import com.genenakagaki.myhandycoach.data.ExerciseContract;
import com.genenakagaki.myhandycoach.data.ReactionExerciseDb;
import com.genenakagaki.myhandycoach.data.RegularExerciseDb;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by gene on 4/16/17.
 */

public class ExerciseChooserFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String ARG_EXERCISE_TYPE = "arg_exercise_type";
    private static final int EXERCISE_LOADER = 0;

    private ExerciseType mExerciseType;

    @BindView(R.id.listview) ListView exerciseListView;

    private Unbinder unbinder;


    private ExerciseChooserCursorAdapter exerciseChooserCursorAdapter;

    public static ExerciseChooserFragment newInstance(ExerciseType exerciseType) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_EXERCISE_TYPE, exerciseType);
        ExerciseChooserFragment fragment = new ExerciseChooserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public ExerciseChooserFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mExerciseType = (ExerciseType) getArguments().getSerializable(ARG_EXERCISE_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exercise_chooser, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        long exerciseId;

        switch (mExerciseType) {
            case REGULAR:
                exerciseId = RegularExerciseDb.getCurrentExercise(getActivity()).id;
                break;
            default: // REACTION
                exerciseId = ReactionExerciseDb.getCurrentExercise(getActivity()).id;
                break;
        }

        exerciseChooserCursorAdapter = new ExerciseChooserCursorAdapter(getActivity(), null, 0, exerciseId, mExerciseType);

        exerciseListView.setAdapter(exerciseChooserCursorAdapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Timber.d("onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(EXERCISE_LOADER, null, this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (mExerciseType) {
            case REGULAR:
                return new CursorLoader(
                        getActivity(),
                        ExerciseContract.RegularExerciseEntry.CONTENT_URI,
                        ExerciseContract.RegularExerciseEntry.COLUMNS,
                        null, null, null);
            default: // REACTION
                return new CursorLoader(
                        getActivity(),
                        ExerciseContract.ReactionExerciseEntry.CONTENT_URI,
                        ExerciseContract.ReactionExerciseEntry.COLUMNS,
                        null, null, null);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        exerciseChooserCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        exerciseChooserCursorAdapter.swapCursor(null);

    }


}
