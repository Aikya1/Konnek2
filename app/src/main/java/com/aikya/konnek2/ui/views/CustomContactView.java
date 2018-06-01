package com.aikya.konnek2.ui.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.aikya.konnek2.R;


public class CustomContactView extends FrameLayout {


    public CustomContactView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public CustomContactView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CustomContactView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        View view = inflate(getContext(), R.layout.send_contact_view, null);
        addView(view);
    }
}
