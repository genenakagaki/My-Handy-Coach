package com.genenakagaki.myhandycoach.adapter;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.genenakagaki.myhandycoach.ExerciseChooserActivity;
import com.genenakagaki.myhandycoach.ExerciseType;
import com.genenakagaki.myhandycoach.MainActivity;
import com.genenakagaki.myhandycoach.R;
import com.genenakagaki.myhandycoach.data.ReactionExerciseDb;
import com.genenakagaki.myhandycoach.data.RegularExerciseDb;
import com.genenakagaki.myhandycoach.data.model.ReactionExercise;
import com.genenakagaki.myhandycoach.data.model.RegularExercise;
import com.genenakagaki.myhandycoach.dialog.DeleteExerciseDialog;
import com.genenakagaki.myhandycoach.dialog.EditExerciseDialog;
import com.genenakagaki.myhandycoach.exception.ExerciseNotFoundException;

import java.util.Calendar;

import butterknife.ButterKnife;
import timber.log.Timber;

import static com.genenakagaki.myhandycoach.R.string.exercise;
import static com.genenakagaki.myhandycoach.data.ExerciseContract.*;

/**
 * Created by gene on 4/16/17.
 */

public class ExerciseChooserCursorAdapter extends CursorAdapter
        implements View.OnTouchListener, View.OnDragListener {

    private static final long DRAG_START_TIME = 100;

    private RadioButton mSelectedRadioButton;
    private long mSelectedExerciseId;
    private ExerciseType mExerciseType;

    private View mDragView;
    private float mDragOffset;
    private boolean mDragging = false;
    private long mTouchStartTime;

    public static class ViewHolder {
        public final LinearLayout mExerciseContainer;
        public final RadioButton mExerciseRadioButton;
        public final LinearLayout mEditLayout;
        public final LinearLayout deleteLayout;

        public ViewHolder(View view) {
            mExerciseContainer = ButterKnife.findById(view, R.id.exercise_container);
            mExerciseRadioButton = ButterKnife.findById(view, R.id.exercise_radiobutton);
            mEditLayout = ButterKnife.findById(view, R.id.edit_layout);
            deleteLayout = ButterKnife.findById(view, R.id.delete_layout);
        }
    }

    public ExerciseChooserCursorAdapter(Context context, Cursor c, int flags, long selectedExerciseId, ExerciseType exerciseType) {
        super(context, c, flags);
        this.mSelectedExerciseId = selectedExerciseId;
        this.mExerciseType = exerciseType;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Timber.d("onTouch");

        final int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                Timber.d("ACTION_DOWN");
                mTouchStartTime = Calendar.getInstance().getTimeInMillis();

                break;
            case MotionEvent.ACTION_MOVE:
                Timber.d("ACTION_MOVE");
                long touchDuration = Calendar.getInstance().getTimeInMillis() - mTouchStartTime;
                if(touchDuration > DRAG_START_TIME && !mDragging) {
                    mDragView = v;
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(new View(v.getContext()));

                    if (Build.VERSION.SDK_INT >= 24) {
                        v.startDragAndDrop(data, shadowBuilder, v.findViewById(R.id.exercise_container), 0);
                    } else {
                        v.startDrag(data, shadowBuilder, v.findViewById(R.id.exercise_container), 0);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                touchDuration = Calendar.getInstance().getTimeInMillis() - mTouchStartTime;
                if (touchDuration < DRAG_START_TIME) {
                    onClick(v.getContext(), v);
                }
                break;
        }
        return true;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        ViewHolder viewHolder = (ViewHolder) mDragView.getTag();

        Context context = v.getContext();

        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                Timber.d("ACTION_DRAG_STARTED");

                mDragging = true;
                mDragOffset = event.getX();
                break;
            case DragEvent.ACTION_DRAG_LOCATION:
                Timber.d("ACTION_DRAG_LOCATION");

                float x = event.getX();

                int margin = (int) (x - mDragOffset);
                int marginLeft = margin;
                int marginRight = 0 - margin;

                Drawable background = viewHolder.mExerciseContainer.getBackground();
                background.setAlpha(200);

                if (marginLeft > 0) { // Dragging to right
                    ButterKnife.findById(mDragView, R.id.content).setBackgroundColor(
                            ContextCompat.getColor(context, R.color.colorPrimary));
                    viewHolder.mEditLayout.setVisibility(View.VISIBLE);
                    viewHolder.deleteLayout.setVisibility(View.INVISIBLE);
                } else {
                    ButterKnife.findById(mDragView, R.id.content).setBackgroundColor(
                            ContextCompat.getColor(context, R.color.error));
                    viewHolder.mEditLayout.setVisibility(View.INVISIBLE);
                    viewHolder.deleteLayout.setVisibility(View.VISIBLE);
                }

                RelativeLayout.LayoutParams layoutParams =
                        (RelativeLayout.LayoutParams) viewHolder.mExerciseContainer.getLayoutParams();
                layoutParams.setMargins(marginLeft, 0, marginRight, 0);
                viewHolder.mExerciseContainer.setLayoutParams(layoutParams);
                break;
            case DragEvent.ACTION_DROP:
                Timber.d("ACTION_DROP");

                background = viewHolder.mExerciseContainer.getBackground();
                background.setAlpha(255);

                layoutParams = (RelativeLayout.LayoutParams) viewHolder.mExerciseContainer.getLayoutParams();

                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((Activity) context).getWindowManager()
                        .getDefaultDisplay()
                        .getMetrics(displayMetrics);
                int width = displayMetrics.widthPixels;

                if (layoutParams.leftMargin > width / 3) {
                    showEditDialog(context, viewHolder);

                } else if (layoutParams.rightMargin > width / 3) {
                    showDeleteDialog(context, viewHolder);

                }
                layoutParams.setMargins(0, 0, 0, 0);

                viewHolder.mExerciseContainer.setLayoutParams(layoutParams);

                mDragging = false;
                break;
            default:
                break;
        }
        return true;
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

    private void showDeleteDialog(Context context, ViewHolder viewHolder) {
        Timber.d("showDeleteDialog");

        final String exerciseName = viewHolder.mExerciseRadioButton.getText().toString();

        try {
            switch (mExerciseType) {
                case REGULAR:
                    RegularExercise regularExercise = RegularExerciseDb.getExerciseByName(context, exerciseName);
                    DeleteExerciseDialog.newInstance(mExerciseType, regularExercise.id)
                            .show(((ExerciseChooserActivity) context).getSupportFragmentManager(), null);
                    break;
                case REACTION:
                    ReactionExercise reactionExercise = ReactionExerciseDb.getExerciseByName(context, exerciseName);
                    DeleteExerciseDialog.newInstance(mExerciseType, reactionExercise.id)
                            .show(((ExerciseChooserActivity) context).getSupportFragmentManager(), null);
                    break;
            }
        } catch (ExerciseNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_exercise_chooser, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        view.setOnTouchListener(this);
        parent.setOnDragListener(this);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        long exerciseId;

        switch (mExerciseType) {
            case REGULAR:
                exerciseId = cursor.getLong(RegularExerciseEntry.INDEX_ID);
                try {
                    RegularExercise exercise = RegularExerciseDb.getExercise(context, exerciseId);
                    Timber.d("Exercise name: " + exercise.name);
                    viewHolder.mExerciseRadioButton.setText(exercise.name);
                } catch (ExerciseNotFoundException e) {
                    Timber.d(e.getMessage());
                    e.printStackTrace();
                }
                break;
            case REACTION:
                exerciseId = cursor.getLong(ReactionExerciseEntry.INDEX_ID);
                try {
                    ReactionExercise exercise = ReactionExerciseDb.getExercise(context, exerciseId);
                    Timber.d("Exercise name: " + exercise.name);
                    viewHolder.mExerciseRadioButton.setText(exercise.name);
                } catch (ExerciseNotFoundException e) {
                    Timber.d(e.getMessage());
                    e.printStackTrace();
                }
                break;
        }

        if (cursor.getLong(RegularExerciseEntry.INDEX_ID) == mSelectedExerciseId) {
            Timber.d("current selected exercise");
            viewHolder.mExerciseRadioButton.setChecked(true);
            mSelectedRadioButton = viewHolder.mExerciseRadioButton;
        }
    }

    public void onClick(Context context, View view) {
        if (mSelectedRadioButton != null) {
            mSelectedRadioButton.setChecked(false);
        }
        mSelectedRadioButton = ((ViewHolder)view.getTag()).mExerciseRadioButton;
        mSelectedRadioButton.setChecked(true);

        try {
            switch (mExerciseType) {
                case REGULAR:
                    mSelectedExerciseId = RegularExerciseDb.getExerciseByName(
                            context,
                            mSelectedRadioButton.getText().toString()).id;

                    RegularExerciseDb.setCurrentExercise(context, mSelectedExerciseId);
                    break;
                case REACTION:
                    mSelectedExerciseId = ReactionExerciseDb.getExerciseByName(
                            context,
                            mSelectedRadioButton.getText().toString()).id;
                    ReactionExerciseDb.setCurrentExercise(context, mSelectedExerciseId);
                    break;
                }
        } catch (ExerciseNotFoundException e) {
            Timber.d(e.getMessage());
            e.printStackTrace();
        }
    }
}
