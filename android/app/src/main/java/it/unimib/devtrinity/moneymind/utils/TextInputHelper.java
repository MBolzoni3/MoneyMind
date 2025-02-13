package it.unimib.devtrinity.moneymind.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

public class TextInputHelper {

    public static final String LOGIN_NAME_REGEX = "[a-zA-Z]+";
    public static final String LOGIN_EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    public static final String LOGIN_PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*()]).{8,}$";
    public static final String ENTITY_NAME_REGEX = "[a-zA-Z0-9 ]+";

    public static boolean validateField(TextInputLayout textInputLayout, String editText, String emptyError, String formatError, String regex) {
        if (editText == null || textInputLayout == null) return false;

        String input = editText.trim();

        if (input.isEmpty()) {
            setError(textInputLayout, emptyError);
            return false;
        } else if (regex != null && !regex.isEmpty() && !input.matches(regex)) {
            setError(textInputLayout, formatError);
            return false;
        } else {
            clearError(textInputLayout);
            return true;
        }
    }

    public static boolean validateField(TextInputLayout textInputLayout, EditText editText, String emptyError, String formatError, String regex) {
        return validateField(textInputLayout, editText.getText().toString(), emptyError, formatError, regex);
    }

    public static void addValidationWatcher(TextInputLayout textInputLayout, EditText editText, String emptyError, String formatError, String regex) {
        if (editText == null || textInputLayout == null) return;

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateField(textInputLayout, editText, emptyError, formatError, regex);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public static void setError(TextInputLayout textInputLayout, String errorMessage) {
        if (textInputLayout != null) {
            textInputLayout.setError(errorMessage);
            textInputLayout.setErrorEnabled(true);
        }
    }

    public static void clearError(TextInputLayout textInputLayout) {
        if (textInputLayout != null) {
            textInputLayout.setError(null);
            textInputLayout.setErrorEnabled(false);
        }
    }

}
