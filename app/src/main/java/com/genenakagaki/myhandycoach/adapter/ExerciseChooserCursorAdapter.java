package com.genenakagaki.myhandycoach.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.genenakagaki.myhandycoach.ExerciseChooserActivity;
import com.genenakagaki.myhandycoach.ExerciseSettingActivity;
import com.genenakagaki.myhandycoach.ExerciseType;
import com.genenakagaki.myhandycoach.R;
import com.genenakagaki.myhandycoach.data.ReactionExerciseDb;
import com.genenakagaki.myhandycoach.data.RegularExerciseDb;
import com.genenakagaki.myhandycoach.data.model.ReactionExercise;
import com.genenakagaki.myhandycoach.data.model.RegularExercise;
import com.genenakagaki.myhandycoach.dialog.EditExerciseDialog;
import com.genenakagaki.myhandycoach.exception.ExerciseNotFoundException;
import com.genenakagaki.myhandycoach.fragment.ExerciseChooserFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.genenakagaki.myhandycoach.data.ExerciseContract.ReactionExerciseEntry;
import static com.genenakagaki.myhandycoach.data.ExerciseContract.RegularExerciseEntry;

/**
 * Created by gene on 4/16/17.
 */

public class ExerciseChooserCursorAdapter extends CursorAdapter {

    private Map<Long, Boolean> mIsSelectedByExerciseId = new HashMap<>();
    private ExerciseType mExerciseType;
    private int mMode;

    public static class ViewHolder {
        @BindView(R.id.exercise_radiobutton) RadioButton mExerciseRadioButton;
        @BindView(R.id.exercise_textview) TextView mExerciseTextButton;
        long mExerciseId;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public ExerciseChooserCursorAdapter(Context context, Cursor c, int flags, ExerciseType exerciseType) {
        super(context, c, flags);
        mMode = ExerciseChooserFragment.MODE_CHOOSE;
        mExerciseType = exerciseType;
    }

    private void showEditDialog(Context context, ViewHolder viewHolder) {
        Timber.d("showEditDialog");

        try {
            switch (mExerciseType) {
                case REGULAR:
                    RegularExercise regularExercise = RegularExerciseDb.getExerciseByName(
                            context, viewHolder.mExerciseRadioButton.getText().toString());
                    EditExerciseDialog.newInstance(mExerciseType, regularExercise.id).show(
                            ((ExerciseChooserActivity) context).getSupportFragmentManager(), null);
                    break;
                case REACTION:
                    ReactionExercise reactionExercise = ReactionExerciseDb.getExerciseByName(
                            context, viewHolder.mExerciseRadioButton.getText().toString());
                    EditExerciseDialog.newInstance(mExerciseType, reactionExercise.id).show(
                            ((ExerciseChooserActivity) context).getSupportFragmentManager(), null);
                    break;
            }
        } catch (ExerciseNotFoundException e) {
            Timber.d(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_exercise_chooser, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        if (mMode == ExerciseChooserFragment.MODE_CHOOSE) {
            viewHolder.mExerciseRadioButton.setVisibility(View.GONE);
        } else {
            viewHolder.mExerciseRadioButton.setVisibility(View.VISIBLE);
        }

        switch (mExerciseType) {
            case REGULAR:
                viewHolder.mExerciseId = cursor.getLong(RegularExerciseEntry.INDEX_ID);
                try {
                    RegularExercise exercise = RegularExerciseDb.getExercise(context, viewHolder.mExerciseId);
                    Timber.d("Exercise name: " + exercise.name);
                    viewHolder.mExerciseTextButton.setText(exercise.name);
                } catch (ExerciseNotFoundException e) {
                    Timber.d(e.getMessage());
                    e.printStackTrace();
                }
                break;
            case REACTION:
                viewHolder.mExerciseId = cursor.getLong(ReactionExerciseEntry.INDEX_ID);
                try {
                    ReactionExercise exercise = ReactionExerciseDb.getExercise(context, viewHolder.mExerciseId);
                    Timber.d("Exercise name: " + exercise.name);
                    viewHolder.mExerciseTextButton.setText(exercise.name);
                } catch (ExerciseNotFoundException e) {
                    Timber.d(e.getMessage());
                    e.printStackTrace();
                }
                break;
        }
    }

    public void setMode(int mode) {
        mMode = mode;
        mIsSelectedByExerciseId.clear();
    }

    public List<Long> getSelectedIds() {
        List<Long> selectedIds = new ArrayList<>();

        for (Map.Entry<Long, Boolean> entry: mIsSelectedByExerciseId.entrySet()) {
            if (entry.getValue()) {
                selectedIds.add(entry.getKey());
            }
        }

        return selectedIds;
    }

    public void onClick(Context context, View view) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        if (mMode == ExerciseChooserFragment.MODE_CHOOSE) {
            Timber.d("onClick Mode: Choose");
            switch (mExerciseType) {
                case REGULAR:
                    RegularExerciseDb.setCurrentExercise(context, viewHolder.mExerciseId);
                    break;
                case REACTION:
                    ReactionExerciseDb.setCurrentExercise(context, viewHolder.mExerciseId);
                    break;
            }
            Intent intent = new Intent(view.getContext(), ExerciseSettingActivity.class);
            intent.putExtra(ExerciseSettingActivity.EXTRA_EXERCISE_TYPE, mExerciseType);
            intent.putExtra(ExerciseSettingActivity.EXTRA_EXERCISE_ID, viewHolder.mExerciseId);
            view.getContext().startActivity(intent);
        } else {
            Timber.d("onClick Mode: Edit");
            Boolean selected = mIsSelectedByExerciseId.get(viewHolder.mExerciseId);
            if (selected == null) {
                selected = true;
            } else {
                selected = !selected;
            }

            mIsSelectedByExerciseId.put(viewHolder.mExerciseId, selected);
            viewHolder.mExerciseRadioButton.setChecked(selected);
        }

//        try {
//            switch (mExerciseType) {
//                case REGULAR:
//                    mIsSelectedByExerciseId = RegularExerciseDb.getExerciseByName(
//                            context,
//                            mSelectedRadioButton.getText().toString()).id;
//
//                    RegularExerciseDb.setCurrentExercise(context, mIsSelectedByExerciseId);
//                    break;
//                case REACTION:
//                    mIsSelectedByExerciseId = ReactionExerciseDb.getExerciseByName(
//                            context,
//                            mSelectedRadioButton.getText().toString()).id;
//                    ReactionExerciseDb.setCurrentExercise(context, mIsSelectedByExerciseId);
//                    break;
//                }
//        } catch (ExerciseNotFoundException e) {
//            Timber.d(e.getMessage());
//            e.printStackTrace();
//        }
    }


//        implements View.OnTouchListener, View.OnDragListener {

//    private static final long DRAG_START_TIME = 100;


//    private View mDragView;
//    private float mDragOffset;
//    private boolean mDragging = false;
//    private long mTouchStartTime;

    //    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        Timber.d("onTouch");
//
//        final int action = MotionEventCompat.getActionMasked(event);
//
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                Timber.d("ACTION_DOWN");
//                mTouchStartTime = Calendar.getInstance().getTimeInMillis();
//
//                break;
//            case MotionEvent.ACTION_MOVE:
//                Timber.d("ACTION_MOVE");
//                long touchDuration = Calendar.getInstance().getTimeInMillis() - mTouchStartTime;
//                if(touchDuration > DRAG_START_TIME && !mDragging) {
//                    mDragView = v;
//                    ClipData data = ClipData.newPlainText("", "");
//                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(new View(v.getContext()));
//
//                    if (Build.VERSION.SDK_INT >= 24) {
//                        v.startDragAndDrop(data, shadowBuilder, v.findViewById(R.id.exercise_container), 0);
//                    } else {
//                        v.startDrag(data, shadowBuilder, v.findViewById(R.id.exercise_container), 0);
//                    }
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                touchDuration = Calendar.getInstance().getTimeInMillis() - mTouchStartTime;
//                if (touchDuration < DRAG_START_TIME) {
//                    onClick(v.getContext(), v);
//                }
//                break;
//        }
//        return true;
//    }

    //    @Override
//    public boolean onDrag(View v, DragEvent event) {
//        ViewHolder viewHolder = (ViewHolder) mDragView.getTag();
//
//        Context context = v.getContext();
//
//        switch (event.getAction()) {
//            case DragEvent.ACTION_DRAG_STARTED:
//                Timber.d("ACTION_DRAG_STARTED");
//
//                mDragging = true;
//                mDragOffset = event.getX();
//                break;
//            case DragEvent.ACTION_DRAG_LOCATION:
//                Timber.d("ACTION_DRAG_LOCATION");
//
//                float x = event.getX();
//
//                int margin = (int) (x - mDragOffset);
//                int marginLeft = margin;
//                int marginRight = 0 - margin;
//
//                Drawable background = viewHolder.mExerciseContainer.getBackground();
//                background.setAlpha(200);
//
//                if (marginLeft > 0) { // Dragging to right
//                    ButterKnife.findById(mDragView, R.id.content).setBackgroundColor(
//                            ContextCompat.getColor(context, R.color.colorPrimary));
//                    viewHolder.mEditLayout.setVisibility(View.VISIBLE);
//                    viewHolder.deleteLayout.setVisibility(View.INVISIBLE);
//                } else {
//                    ButterKnife.findById(mDragView, R.id.content).setBackgroundColor(
//                            ContextCompat.getColor(context, R.color.error));
//                    viewHolder.mEditLayout.setVisibility(View.INVISIBLE);
//                    viewHolder.deleteLayout.setVisibility(View.VISIBLE);
//                }
//
//                RelativeLayout.LayoutParams layoutParams =
//                        (RelativeLayout.LayoutParams) viewHolder.mExerciseContainer.getLayoutParams();
//                layoutParams.setMargins(marginLeft, 0, marginRight, 0);
//                viewHolder.mExerciseContainer.setLayoutParams(layoutParams);
//                break;
//            case DragEvent.ACTION_DROP:
//                Timber.d("ACTION_DROP");
//
//                background = viewHolder.mExerciseContainer.getBackground();
//                background.setAlpha(255);
//
//                layoutParams = (RelativeLayout.LayoutParams) viewHolder.mExerciseContainer.getLayoutParams();
//
//                DisplayMetrics displayMetrics = new DisplayMetrics();
//                ((Activity) context).getWindowManager()
//                        .getDefaultDisplay()
//                        .getMetrics(displayMetrics);
//                int width = displayMetrics.widthPixels;
//
//                if (layoutParams.leftMargin > width / 3) {
//                    showEditDialog(context, viewHolder);
//
//                } else if (layoutParams.rightMargin > width / 3) {
//                    showDeleteDialog(context, viewHolder);
//
//                }
//                layoutParams.setMargins(0, 0, 0, 0);
//
//                viewHolder.mExerciseContainer.setLayoutParams(layoutParams);
//
//                mDragging = false;
//                break;
//            default:
//                break;
//        }
//        return true;
//    }
}
