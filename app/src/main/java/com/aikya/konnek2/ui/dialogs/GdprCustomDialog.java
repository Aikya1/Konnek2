package com.aikya.konnek2.ui.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.aikya.konnek2.R;
import com.aikya.konnek2.call.core.models.LoginType;
import com.aikya.konnek2.utils.ToastUtils;

/**
 * Created by rajeev on 20/3/18.
 */

public class GdprCustomDialog extends Dialog implements View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button getStartedBtn, no;
    RadioGroup radioGroup;
    RadioButton selectedRadioButton;
    String selectedOption;

    OnGdprSelected onGdprSelectedCallback;


    protected LoginType loginType;


    public GdprCustomDialog(Activity a) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gdpr);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        initViews();


        getStartedBtn.setOnClickListener(this);
    }

    private void initViews() {
        getStartedBtn = findViewById(R.id.submitbtn);
        radioGroup = findViewById(R.id.radioGroup);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submitbtn:
                if (radioGroup.getCheckedRadioButtonId() == -1) {
                    // no radio buttons are checked
                    ToastUtils.shortToast("Please select an option");
                } else {
                    // one of the radio buttons is checked
                    // get selected radio button from radioGroup
                    int selectedId = radioGroup.getCheckedRadioButtonId();
                    // find the radiobutton by returned id
                    selectedRadioButton = findViewById(selectedId);
                    selectedOption = selectedRadioButton.getText().toString();

                    if (selectedOption.equalsIgnoreCase("yes")) {
                        //User selected YES.. Send to firebase screen.. Verify OTP
                        onGdprSelectedCallback.finish("yes");

                    } else {
                        //User selected NO...No need to verify OTP. Send user directly to profile page.
                        onGdprSelectedCallback.finish("no");
                    }

                }


                break;
        }
    }

    public void setDialogResult(OnGdprSelected dialogResult) {
        onGdprSelectedCallback = dialogResult;
    }


    public interface OnGdprSelected {
        void finish(String result);
    }
}
