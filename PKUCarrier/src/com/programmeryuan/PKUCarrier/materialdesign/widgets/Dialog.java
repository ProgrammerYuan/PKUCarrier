package com.programmeryuan.PKUCarrier.materialdesign.widgets;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.programmeryuan.PKUCarrier.R;

public class Dialog extends android.app.Dialog {

    String message;
    TextView messageTextView;
    String title;
    TextView titleTextView;
    RelativeLayout customViewContainer;
    LinearLayout buttonContainer;
    ButtonFlat buttonAccept;
    ButtonFlat buttonCancel;
    ButtonFlat buttonNeutral;
    View customView;
    View.OnClickListener onAcceptButtonClickListener;
    View.OnClickListener onCancelButtonClickListener;
    View.OnClickListener onNeutralButtonClickListener;
    int main_color;

    public Dialog(Context context, String title, String message) {
//		super(context, android.R.style.Theme_Translucent);
        super(context, R.style.AnimDialog);
        this.message = message;
        this.title = title;
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

    }

    public Dialog(Context context, String title, String message, int color) {
        this(context, title, message);
        main_color = context.getResources().getColor(color);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);


        this.titleTextView = (TextView) findViewById(R.id.dialog_title);
        setTitle(title);
//        setCancelable(true);
//        setCanceledOnTouchOutside(true);
        this.messageTextView = (TextView) findViewById(R.id.dialog_message);
        setMessage(message);
        customViewContainer = (RelativeLayout) findViewById(R.id.dialog_custom);
        buttonContainer = (LinearLayout) findViewById(R.id.button_container);
        if (customView != null) {
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            customViewContainer.addView(customView, p);
            customViewContainer.setVisibility(View.VISIBLE);
            messageTextView.setVisibility(View.GONE);
        }

        this.buttonAccept = (ButtonFlat) findViewById(R.id.button_accept);
        buttonAccept.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                dismiss();
                if (onAcceptButtonClickListener != null)
                    onAcceptButtonClickListener.onClick(v);
            }
        });
        this.buttonCancel = (ButtonFlat) findViewById(R.id.button_cancel);

        buttonCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (onCancelButtonClickListener != null)
                    onCancelButtonClickListener.onClick(v);
            }
        });
        buttonNeutral = (ButtonFlat) findViewById(R.id.button_neutral);
        buttonNeutral.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
                if (onNeutralButtonClickListener != null)
                    onNeutralButtonClickListener.onClick(v);
            }
        });
        if (main_color != 0) {
            buttonAccept.getTextView().setTextColor(main_color);
//            buttonCancel.getTextView().setTextColor(main_color);
        }

    }

    // GETERS & SETTERS

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        if (message == null || message.isEmpty())
            messageTextView.setVisibility(View.GONE);
        else {
            messageTextView.setVisibility(View.VISIBLE);
            messageTextView.setText(message);
        }
        messageTextView.setText(message);
//        customView.setVisibility(View.GONE);
//        messageTextView.setVisibility(View.VISIBLE);
    }

    public TextView getMessageTextView() {
        return messageTextView;
    }

    public void setMessageTextView(TextView messageTextView) {
        this.messageTextView = messageTextView;
    }

    public String getTitle() {
        return title;
    }

    public void setCustomView(View c) {

        customView = c;


    }

    public void setTitle(String title) {
        this.title = title;
        if (title == null || title.isEmpty())
            titleTextView.setVisibility(View.GONE);
        else {
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setText(title);
        }
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public void setTitleTextView(TextView titleTextView) {
        this.titleTextView = titleTextView;
    }

    public void setButtonAcceptText(String text) {
        if(buttonAccept != null)
            buttonAccept.setText(text);
    }

    public void setButtonAcceptVisibility(int visibility){
        if(buttonAccept != null)
            buttonAccept.setVisibility(visibility);
    }

    public ButtonFlat getButtonAccept() {
        return buttonAccept;
    }

    public void setButtonAccept(ButtonFlat buttonAccept) {
        this.buttonAccept = buttonAccept;
    }

    public void setButtonCancelText(String text) {
        if(buttonAccept != null)
            buttonCancel.setText(text);
    }

    public void setButtonCancelVisibility(int visibility){
        if(buttonCancel != null)
            buttonCancel.setVisibility(visibility);
    }

    public ButtonFlat getButtonCancel() {
        return buttonCancel;
    }

    public void setButtonCancel(ButtonFlat buttonCancel) {
        this.buttonCancel = buttonCancel;
    }

    public ButtonFlat getButtonNeutral() {
        buttonNeutral.setVisibility(View.VISIBLE);
        return buttonNeutral;
    }

    public void setButtonNeutral(ButtonFlat buttonCancel) {
        this.buttonNeutral = buttonCancel;
    }

    public void setOnAcceptButtonClickListener(
            View.OnClickListener onAcceptButtonClickListener) {
        this.onAcceptButtonClickListener = onAcceptButtonClickListener;
//        if (buttonAccept != null)
//            buttonAccept.setOnClickListener(this.onAcceptButtonClickListener);
    }

    public void setOnCancelButtonClickListener(
            View.OnClickListener onCancelButtonClickListener) {
        this.onCancelButtonClickListener = onCancelButtonClickListener;
//        if (buttonCancel != null)
//            buttonCancel.setOnClickListener(this.onCancelButtonClickListener);
    }

    public void setOnNeutralButtonClickListener(
            View.OnClickListener onNeutralButtonClickListener) {
        this.onNeutralButtonClickListener = onNeutralButtonClickListener;
//        if (buttonNeutral != null)
//            buttonNeutral.setOnClickListener(this.onNeutralButtonClickListener);
    }

}
