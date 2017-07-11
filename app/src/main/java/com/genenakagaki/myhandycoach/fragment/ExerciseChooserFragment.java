package com.genenakagaki.myhandycoach.fragment;

import android.content.Context;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.genenakagaki.myhandycoach.ExerciseType;
import com.genenakagaki.myhandycoach.R;
import com.genenakagaki.myhandycoach.adapter.ExerciseChooserCursorAdapter;
import com.genenakagaki.myhandycoach.data.ExerciseContract;
import com.genenakagaki.myhandycoach.dialog.DeleteExerciseDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import timber.log.Timber;

/**
 * Created by gene on 4/16/17.
 */

public class ExerciseChooserFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public interface OnModeChangeListener {
        void onModeChanged(int mode);
    }

    private static final String ARG_EXERCISE_TYPE = "arg_exercise_type";
    private static final int EXERCISE_LOADER = 0;
    public static final int MODE_CHOOSE = 0;
    public static final int MODE_EDIT = 1;

    private ExerciseType mExerciseType;
    private ExerciseChooserCursorAdapter mExerciseChooserCursorAdapter;
    private Menu mMenu;
    private OnModeChangeListener mListener;

    @BindView(R.id.listview) ListView exerciseListView;

    private Unbinder unbinder;

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

        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mExerciseType = (ExerciseType) getArguments().getSerializable(ARG_EXERCISE_TYPE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exercise_chooser, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        mExerciseChooserCursorAdapter = new ExerciseChooserCursorAdapter(getActivity(), null, 0, mExerciseType);

        exerciseListView.setAdapter(mExerciseChooserCursorAdapter);
        exerciseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mExerciseChooserCursorAdapter.onClick(getActivity(), view);
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnModeChangeListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnModeChangeListener");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        Timber.d("onActivityCreated");
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(EXERCISE_LOADER, null, this);

        if (savedInstanceState != null) {
            mExerciseType = (ExerciseType) savedInstanceState.getSerializable(ARG_EXERCISE_TYPE);
            mExerciseChooserCursorAdapter = new ExerciseChooserCursorAdapter(getActivity(), null, 0, mExerciseType);

            exerciseListView.setAdapter(mExerciseChooserCursorAdapter);
            exerciseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mExerciseChooserCursorAdapter.onClick(getActivity(), view);
                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putSerializable(ARG_EXERCISE_TYPE, mExerciseType);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_exercise_chooser, menu);

        for(int i = 0; i < menu.size(); i++){
            Drawable drawable = menu.getItem(i).getIcon();
            if(drawable != null) {
                drawable.mutate();
                drawable.setColorFilter(
                        ContextCompat.getColor(getActivity(), android.R.color.white),
                        PorterDuff.Mode.SRC_ATOP);
            }
        }

        mMenu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                mListener.onModeChanged(MODE_EDIT);
//                setMode(ExerciseChooserActivity.MODE_EDIT);
                return true;
            case R.id.action_delete:
                List<Long> selectedIds = mExerciseChooserCursorAdapter.getSelectedIds();
                DeleteExerciseDialog.newInstance(mExerciseType, selectedIds)
                        .show(getActivity().getSupportFragmentManager(), null);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setMode(int mode) {
        if (mode == MODE_CHOOSE) {
            mExerciseChooserCursorAdapter.setMode(MODE_CHOOSE);
            getLoaderManager().restartLoader(EXERCISE_LOADER, null, this);

            mMenu.findItem(R.id.action_delete).setVisible(false);
            mMenu.findItem(R.id.action_edit).setVisible(true);
        } else {
//            ((ExerciseChooserActivity)getActivity()).setMode(MODE_EDIT);
            mExerciseChooserCursorAdapter.setMode(MODE_EDIT);
            getLoaderManager().restartLoader(EXERCISE_LOADER, null, this);

            mMenu.findItem(R.id.action_edit).setVisible(false);
            mMenu.findItem(R.id.action_delete).setVisible(true);
        }
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
        mExerciseChooserCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mExerciseChooserCursorAdapter.swapCursor(null);

    }


}
