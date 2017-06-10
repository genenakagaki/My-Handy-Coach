package com.genenakagaki.myhandycoach.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;

import com.genenakagaki.myhandycoach.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by gene on 4/10/17.
 */

public class DurationPicker extends FrameLayout implements NumberPicker.OnValueChangeListener {

    private TextView labelText;
    private Switch enableSwitch;
    private NumberPicker colonPicker;
    private NumberPicker minutesPicker;
    private NumberPicker secondsPicker;
    private TextView minutesText;
    private TextView secondsText;
    private TextView errorText;

    private static String[] sColonPickerDisplayValues = new String[] {":"};
    private static String[] sPickerDisplayValues;

    private List<View> mViewsToDisable = null;

    private boolean mEnabled = true;
    private boolean mErrorEnabled = false;

    private String mEnabledLabel;
    private String mDisabledLabel;

    public DurationPicker(Context context) {
        super(context);
        initView(context);
    }

    public DurationPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DurationPicker);

        mEnabledLabel = typedArray.getString(R.styleable.DurationPicker_enabled_label);
        mDisabledLabel = typedArray.getString(R.styleable.DurationPicker_disabled_label);
        if (mEnabledLabel != null) {
            setEnabledLabel(mEnabledLabel);
        }

        boolean showSwitch = typedArray.getBoolean(R.styleable.DurationPicker_show_switch, true);
        if (!showSwitch) {
            setSwitchVisibility(View.GONE);
        }

        typedArray.recycle();
    }

    public DurationPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(final Context context) {
        final View rootView = LayoutInflater.from(context).inflate(R.layout.duration_picker, this);

        labelText = ButterKnife.findById(rootView, R.id.label_text);
        enableSwitch = ButterKnife.findById(rootView, R.id.enable_switch);
        colonPicker = ButterKnife.findById(rootView, R.id.colon_picker);
        minutesPicker = ButterKnife.findById(rootView, R.id.minutes_picker);
        secondsPicker = ButterKnife.findById(rootView, R.id.seconds_picker);
        minutesText = ButterKnife.findById(rootView, R.id.minutes_text);
        secondsText = ButterKnife.findById(rootView, R.id.seconds_text);
        errorText = ButterKnife.findById(rootView, R.id.error_text);

        if (sPickerDisplayValues == null) {
            sPickerDisplayValues = new String[60];
            for (int i = 0; i < 60; i++) {
                sPickerDisplayValues[i] = String.format("%02d", i); //display in 2 digits
            }
        }

        colonPicker.setDisplayedValues(sColonPickerDisplayValues);
        minutesPicker.setMaxValue(59);
        minutesPicker.setDisplayedValues(sPickerDisplayValues);
        minutesPicker.setOnValueChangedListener(this);
        secondsPicker.setMaxValue(59);
        secondsPicker.setDisplayedValues(sPickerDisplayValues);
        secondsPicker.setOnValueChangedListener(this);

        enableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Timber.d("time picker switch is checked");

                    mEnabled = true;

                    labelText.setEnabled(true);
                    colonPicker.setEnabled(true);
                    minutesPicker.setEnabled(true);
                    secondsPicker.setEnabled(true);
                    minutesText.setEnabled(true);
                    secondsText.setEnabled(true);

                    if (mViewsToDisable != null) {
                        for (View view: mViewsToDisable) {
                            view.setEnabled(false);
                        }
                    }

                    rootView.setBackgroundResource(R.drawable.background_card_enabled);
                } else {
                    Timber.d("time picker switch is NOT checked");

                    mEnabled = false;

                    labelText.setEnabled(false);
                    colonPicker.setEnabled(false);
                    minutesPicker.setEnabled(false);
                    secondsPicker.setEnabled(false);
                    minutesText.setEnabled(false);
                    secondsText.setEnabled(false);

                    if (mViewsToDisable != null) {
                        for (View view : mViewsToDisable) {
                            view.setEnabled(true);
                        }
                    }

                    rootView.setBackgroundResource(R.drawable.background_duration_picker_disabled);
                }
            }
        });
    }

    public void setEnabledLabel(String label) {
        mEnabledLabel = label;
        if (mEnabled) {
            labelText.setText(mEnabledLabel);
        }
    }

    public void setDisabledLabel(String label) {
        mDisabledLabel = label;
        if (!mEnabled) {
            labelText.setText(mDisabledLabel);
        }
    }

    public void setSwitchVisibility(int visibility) {
        enableSwitch.setVisibility(visibility);
    }

    public void setEnabled(boolean enabled) {
        enableSwitch.setChecked(enabled);
        this.mEnabled = enabled;
    }

    public void setViewsToEnableOnSwitch(View... viewsToDisable) {
        this.mViewsToDisable = new ArrayList<>();
        for (View view: viewsToDisable) {
            this.mViewsToDisable.add(view);
        }
    }

    public int getDuration() {
        int duration = secondsPicker.getValue() + minutesPicker.getValue() * 60;

        return duration;
    }

    public void setDuration(int duration) {
        int minutes = duration / 60;
        int seconds = duration - minutes * 60;
        minutesPicker.setValue(minutes);
        secondsPicker.setValue(seconds);
    }

    public void setErrorEnabled(boolean errorEnabled) {
        this.mErrorEnabled = errorEnabled;
        if (errorEnabled) {
            errorText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if (mErrorEnabled) {
            mErrorEnabled = false;
            errorText.setVisibility(View.GONE);
        }
    }
}
