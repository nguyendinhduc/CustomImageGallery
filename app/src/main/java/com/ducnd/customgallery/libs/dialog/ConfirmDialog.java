package com.ducnd.customgallery.libs.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.annotation.StyleRes;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.ducnd.customgallery.R;

/**
 * Created by ducnd on 7/5/17.
 */

public class ConfirmDialog extends Dialog implements View.OnClickListener {
    private IMessageDialog mInterf;

    public ConfirmDialog(@NonNull Context context, String content, IMessageDialog interf) {
        super(context);
        mInterf = interf;
        inits(content);
    }

    public ConfirmDialog(@NonNull Context context, @StringRes int content, IMessageDialog interf) {
        super(context);
        mInterf = interf;
        inits(content);
    }

    public ConfirmDialog(@NonNull Context context, @StyleRes int themeResId, String content, IMessageDialog interf) {
        super(context, themeResId);
        mInterf = interf;
        inits(content);
    }

    public ConfirmDialog(@NonNull Context context, @StyleRes int themeResId, @StringRes int content, IMessageDialog interf) {
        super(context, themeResId);
        mInterf = interf;
        inits(content);
    }

    private void inits(String content) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm);
        TextView tvContent = (TextView) findViewById(R.id.tv_content);
        tvContent.setText(content);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    private void inits(@StringRes int content) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm);
        TextView tvContent = (TextView) findViewById(R.id.tv_content);
        tvContent.setText(content);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_cancel:
                dismiss();
                mInterf.onClickCancel();
                break;
            case R.id.btn_ok:
                dismiss();
                mInterf.onClickOk();
                break;
            default:
                break;
        }


    }

    public interface IMessageDialog {
        void onClickOk();

        void onClickCancel();
    }

}
