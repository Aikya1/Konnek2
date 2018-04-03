package com.aikya.konnek2.ui.activities.feedback;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;


import com.aikya.konnek2.ui.activities.base.BaseLoggableActivity;
import com.aikya.konnek2.utils.helpers.EmailHelper;

import butterknife.Bind;

public class FeedbackActivity extends BaseLoggableActivity {

    @Bind(com.aikya.konnek2.R.id.feedback_types_radiogroup)
    RadioGroup feedbackTypesRadioGroup;

    public static void start(Context context) {
        Intent intent = new Intent(context, FeedbackActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getContentResId() {
        return com.aikya.konnek2.R.layout.activity_feedback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initFields();
        setUpActionBarWithUpButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(com.aikya.konnek2.R.menu.done_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case com.aikya.konnek2.R.id.action_done:
                EmailHelper.sendFeedbackEmail(this, getSelectedFeedbackType());
                break;
            default:
                super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void initFields() {
        title = getString(com.aikya.konnek2.R.string.feedback_title);
    }

    private String getSelectedFeedbackType() {
        int radioButtonID = feedbackTypesRadioGroup.getCheckedRadioButtonId();
        RadioButton radioButton = feedbackTypesRadioGroup.findViewById(radioButtonID);
        return radioButton.getText().toString();
    }
}