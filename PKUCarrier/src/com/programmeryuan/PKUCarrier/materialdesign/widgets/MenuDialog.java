package com.programmeryuan.PKUCarrier.materialdesign.widgets;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.programmeryuan.PKUCarrier.R;
import com.programmeryuan.PKUCarrier.materialdesign.utils.Utils;

public class MenuDialog extends android.app.Dialog {

    String message;
    TextView messageTextView;
    String title;
    TextView titleTextView;
    ListView itemList;
    LinearLayout header;
    ImageView divider;
    ImageView bg;
    AdapterView.OnItemClickListener OnItemClickListener;
    int main_color, res_list_item_layout, res_list_item_layout_text;
    String[] items;

    public MenuDialog(Context context, String title, String message) {
//		super(context, android.R.style.Theme_Translucent);
        super(context, R.style.AnimDialog);
        this.message = message;
        this.title = title;
    }

    public MenuDialog(Context context, String title, String message, int color) {
        this(context, title, message);
        main_color = context.getResources().getColor(color);

    }

    public MenuDialog(Context context, String title, String[] items, int res_list_item_layout, int res_list_item_layout_text, AdapterView.OnItemClickListener listener) {
//		super(context, android.R.style.Theme_Translucent);
        super(context, R.style.AnimDialog);
        this.title = title;
        this.items = items;
        this.res_list_item_layout = res_list_item_layout;
        this.res_list_item_layout_text = res_list_item_layout_text;
        OnItemClickListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        this.titleTextView = (TextView) findViewById(R.id.dialog_title);
        header = (LinearLayout)findViewById(R.id.header);
        divider = (ImageView)findViewById(R.id.header_divider);
        bg = (ImageView)findViewById(R.id.dialog_bg);
        bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuDialog.this.dismiss();
            }
        });
        setTitle(title);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        this.messageTextView = (TextView) findViewById(R.id.dialog_message);
        setMessage(message);
        itemList = (ListView) findViewById(R.id.items);

        if (main_color != 0) {
//            buttonAccept.getTextView().setTextColor(main_color);
//            buttonCancel.getTextView().setTextColor(main_color);
        }
        if (items != null) {
            String[] added_items = new String[items.length + 1];
            System.arraycopy(items, 0, added_items, 0, items.length);
            added_items[items.length] = "取消";
            itemList.setAdapter(new ItemAdapter(this, getContext(), res_list_item_layout, res_list_item_layout_text, added_items, OnItemClickListener));
//            itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    dismiss();
//                    OnItemClickListener.onItemClick(parent, view, position, id);
//                }
//            });
            itemList.setVisibility(View.VISIBLE);
            messageTextView.setVisibility(View.GONE);

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

    public void setTitle(String title) {
        this.title = title;
        if (title == null || title.isEmpty()) {
            header.setVisibility(View.GONE);
//            titleTextView.setVisibility(View.GONE);
            divider.setVisibility(View.GONE);
        } else {
//            titleTextView.setVisibility(View.VISIBLE);
            header.setVisibility(View.VISIBLE);
            divider.setVisibility(View.VISIBLE);
            titleTextView.setText(title);
        }
    }

    public TextView getTitleTextView() {
        return titleTextView;
    }

    public void setTitleTextView(TextView titleTextView) {
        this.titleTextView = titleTextView;
    }

    class ItemAdapter extends ArrayAdapter<String> {
        int res_layout, res_button;
        LayoutInflater inflater;
        AdapterView.OnItemClickListener listener;
        MenuDialog owner;
        String[] items;

        public ItemAdapter(MenuDialog dialog, Context context, int resource, int textViewResourceId, String[] objects, AdapterView.OnItemClickListener onItemClickListener) {
            super(context, resource, textViewResourceId, objects);
            items = objects;
            res_layout = resource;
            res_button = textViewResourceId;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listener = onItemClickListener;
            owner = dialog;
        }

        //    public ItemAdapter(Context context, int resource) {
//        super(context, resource);
//    }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return createViewFromResource(position, convertView, parent, res_layout);
        }

        private View createViewFromResource(final int position, View convertView, ViewGroup parent,
                                            int resource) {
            View view;
            ButtonFlat text;

            if (convertView == null) {
                view = inflater.inflate(resource, parent, false);
            } else {
                view = convertView;
            }

            try {
//                if (res_button == 0) {
//                    //  If no custom field is assigned, assume the whole resource is a TextView
//                    text = (TextView) view;
//                } else {
                //  Otherwise, find the TextView field within the layout
                text = (ButtonFlat) view.findViewById(res_button);
//                }
            } catch (ClassCastException e) {
                Log.e("ArrayAdapter", "You must supply a resource ID for a TextView");
                throw new IllegalStateException(
                        "ArrayAdapter requires the resource ID to be a TextView", e);
            }

            String item = getItem(position);
//            if (item instanceof CharSequence) {
//                text.setText((CharSequence) item);
//            } else {
            text.setText(item.toString());
            RelativeLayout.LayoutParams params =new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
            params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            text.getTextView().setLayoutParams(params);
            text.getTextView().setPadding(Utils.dpToPx(24, getContext().getResources()), 0, 0, 0);
//            text.getTextView().setTypeface(text.getTextView().getTypeface(), Typeface.NORMAL);
//            text.setRippleSpeedFactor(2f);
            text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    owner.dismiss();
//                    Log.i("onClick",position+" "+items.length);
                    if (position != items.length - 1) {
                        listener.onItemClick(null, null, position, 0);
                    }

                }
            });
//            }

            return view;
        }
    }

}
