package com.yunge.im.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.DialogCompat;

import com.yunge.im.R;


public class CallCancelDialog extends Dialog {

    public CallCancelDialog(@NonNull Context context) {
        super(context);
    }

    public CallCancelDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CallCancelDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.call_alert_dialog);
        View view = findViewById(R.id.cancel_Hz);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (clickListener != null){
                    clickListener.onClick(v);
                    dismiss();
                }
            }
        });
    }

    private View.OnClickListener clickListener;

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }
}
