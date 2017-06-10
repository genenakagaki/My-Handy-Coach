package com.genenakagaki.myhandycoach;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;

/**
 * Created by gene on 4/19/17.
 */

public class RequiredInputValidator implements View.OnFocusChangeListener {

    private TextInputLayout mInputLayout;
    private TextInputEditText mInput;
    private String mErrorMessage;

    private boolean isValid = false;

    public RequiredInputValidator(
            TextInputLayout inputLayout,
            TextInputEditText input,
            String errorMessage) {
        this.mInputLayout = inputLayout;
        this.mInput = input;
        this.mErrorMessage = errorMessage;

        this.mInput.setOnFocusChangeListener(this);
    }

    public boolean isValid() {
        return isValid;
    }

    public void validate() {
        if (mInput.getText().toString().trim().isEmpty()) {
            isValid = false;
            showError(mErrorMessage);
        } else {
            isValid = true;
        }
    }

    public void showError(String errorMessage) {
        isValid = false;
        mInputLayout.setErrorEnabled(true);
        mInputLayout.setError(errorMessage);
    }

    public void hideError() {
        isValid = true;
        mInputLayout.setErrorEnabled(false);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            mInputLayout.setErrorEnabled(false);
        } else {
            validate();
        }
    }
}
