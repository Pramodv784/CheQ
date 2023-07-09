package com.cheq.retail.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.cheq.retail.R;

public class GenericTextWatcher implements TextWatcher {
    private EditText[] editText;
    private View view;
    private Boolean isActivateFlow;

    public GenericTextWatcher(View view, EditText editText[], Boolean isActivateFlow) {
        this.editText = editText;
        this.view = view;
        this.isActivateFlow = isActivateFlow;
    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString();
        switch (view.getId()) {

            case R.id.etOne:
                if (text.length() == 1)
                    editText[1].requestFocus();
                break;
            case R.id.etTwo:
                if (text.length() == 1) {
                    editText[2].requestFocus();
                } else if (text.length() == 0)
                    editText[0].requestFocus();
                break;
            case R.id.etThree:
                if (text.length() == 1) {
                    editText[3].requestFocus();
                } else if (text.length() == 0)
                    editText[1].requestFocus();
                break;
            case R.id.etFour:
                if (text.length() == 1) {
                    editText[4].requestFocus();
                } else if (text.length() == 0)
                    editText[2].requestFocus();
                break;
            case R.id.etFive:
                if (text.length() == 1)
                    editText[5].requestFocus();
                else if (text.length() == 0)
                    editText[3].requestFocus();
                break;
            case R.id.etSix:
                if (text.length() == 1)
                    editText[6].requestFocus();
                else if (text.length() == 0)
                    editText[4].requestFocus();
                break;
            case R.id.etSeven:
                if (text.length() == 1)
                    editText[7].requestFocus();
                else if (text.length() == 0)
                    editText[5].requestFocus();
                break;
            case R.id.etEight:
                if (text.length() == 1)
                    editText[8].requestFocus();
                else if (text.length() == 0)
                    editText[6].requestFocus();
                break;
            case R.id.etNine:
                if (text.length() == 1)
                    editText[9].requestFocus();
                else if (text.length() == 0)
                    editText[7].requestFocus();
                break;
            case R.id.etTen:
                if (text.length() == 1)
                    editText[10].requestFocus();
                else if (text.length() == 0)
                    editText[8].requestFocus();
                break;
            case R.id.etEleven:
                if (text.length() == 1)
                    editText[11].requestFocus();
                else if (text.length() == 0)
                    editText[9].requestFocus();
                break;
            case R.id.etTwelve:
                if (!isActivateFlow) {
                    if (text.length() == 1)
                        editText[12].requestFocus();
                    else if (text.length() == 0)
                        editText[10].requestFocus();
                }
                break;
            case R.id.etThirteen:
                if (text.length() == 1)
                    editText[13].requestFocus();
                else if (text.length() == 0)
                    editText[11].requestFocus();
                break;
            case R.id.etFourteen:
                if (text.length() == 1)
                    editText[14].requestFocus();
                else if (text.length() == 0)
                    editText[12].requestFocus();
                break;
            case R.id.etFifteen:
                if (text.length() == 1)
                    editText[15].requestFocus();
                else if (text.length() == 0)
                    editText[13].requestFocus();
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }
}
