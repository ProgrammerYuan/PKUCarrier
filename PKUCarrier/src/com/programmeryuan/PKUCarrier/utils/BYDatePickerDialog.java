package com.programmeryuan.PKUCarrier.utils;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.programmeryuan.PKUCarrier.R;
import com.programmeryuan.PKUCarrier.materialdesign.widgets.ButtonFlat;
import com.programmeryuan.PKUCarrier.materialdesign.widgets.Dialog;
import net.simonvt.numberpicker.NumberPicker;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;


public class BYDatePickerDialog extends DialogFragment {
	NumberPicker np_year, np_month, np_day, np_hour, np_minute;
	int min_year, min_month, min_date;

	OnDatePickedListener l1;
	public final static int MODE_DATE = 0;
	public final static int MODE_TIME = 1;
	public final static int MODE_FULL_VERSION = 2;
	int res_theme, mode;
	TextView tv_title;


	public static BYDatePickerDialog newInstance(Context context, String title, int year, int month, int day, int res_divider, int res_theme, int mode, OnDatePickedListener listener) {

		Bundle args = new Bundle();
		args.putString("title", title);
		args.putInt("res_divider", res_divider);
		args.putInt("year", year);
		args.putInt("month", month);
		args.putInt("day", day);
		args.putInt("mode", mode);

		BYDatePickerDialog d = (BYDatePickerDialog) Fragment.instantiate(context, BYDatePickerDialog.class.getName(), args);
		d.setStyle(DialogFragment.STYLE_NORMAL, res_theme);
		d.res_theme = res_theme;
		d.setOnDatePickedListener(listener);
		return d;
	}

	NumberPicker.Formatter formatter = new NumberPicker.Formatter() {
		@Override
		public String format(int value) {
			if (value < 10) {
				return "0" + value;
			} else {
				return String.valueOf(value);
			}
		}
	};

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {


//        AlertDialog.Builder b = new AlertDialog.Builder(getActivity());
		String title = getArguments().getString("title");
		int res_divider = getArguments().getInt("res_divider");

		int year = getArguments().getInt("year", 0);
		int month = getArguments().getInt("month", 0);
		int day = getArguments().getInt("day", 0);

		min_year = year;
		min_month = month;
		min_date = day;

		mode = getArguments().getInt("mode", MODE_DATE);

		View custom = getActivity().getLayoutInflater().inflate(R.layout.dialog_bydatepicker, null);

		switch (mode) {
			case MODE_DATE:
			case MODE_TIME:
			default:
				custom.findViewById(R.id.dialog_datepicker_tmp2).setVisibility(View.GONE);
				break;
			case MODE_FULL_VERSION:
				custom.findViewById(R.id.dialog_datepicker_tmp2).setVisibility(View.VISIBLE);
				break;
		}

		np_year = (NumberPicker) custom.findViewById(R.id.dialog_datepicker_year);
		np_month = (NumberPicker) custom.findViewById(R.id.dialog_datepicker_month);
		np_day = (NumberPicker) custom.findViewById(R.id.dialog_datepicker_day);
		np_hour = (NumberPicker) custom.findViewById(R.id.dialog_datepicker_hour);
		np_minute = (NumberPicker) custom.findViewById(R.id.dialog_datepicker_minute);

		tv_title = (TextView) custom.findViewById(R.id.dialog_datepicker_title);
		tv_title.setText(title);
		np_year.setMinValue(mode == MODE_FULL_VERSION ? year : 1900);
		np_year.setMaxValue(mode == MODE_FULL_VERSION ? year + 2 : DateProvider.getThisYear());
		np_year.setFocusable(true);
		np_year.setFocusableInTouchMode(true);
		np_year.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		np_year.setDivider(getResources().getDrawable(res_divider));
		if (year > 0) {
			np_year.setValue(year);
		} else {
			np_year.setValue(1994);
		}
		if (year <= DateProvider.getThisYear()) {
			np_year.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
					if (mode != MODE_FULL_VERSION) {
						if (np_year.getMaxValue() == newVal) {
							np_month.setMaxValue(DateProvider.getThisMonth());
							np_month.invalidate();
						} else {
							np_month.setMaxValue(12);
							np_month.invalidate();
						}
						Calendar mycal = new GregorianCalendar(np_year.getValue(), np_month.getValue() - 1, 1);
						int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
						np_day.setMinValue(1);
						np_day.setMaxValue(daysInMonth);
					} else {
						if (min_year == newVal) {
							np_month.setMinValue(min_month);
							np_month.invalidate();
						} else {
							np_month.setMinValue(1);
							np_month.invalidate();
						}
						Calendar mycal = new GregorianCalendar(np_year.getValue(), np_month.getValue() - 1, 1);
						int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
						if (np_month.getValue() == min_month)
							np_day.setMinValue(min_date);
						else
							np_day.setMinValue(1);
						np_day.setMaxValue(daysInMonth);
					}
				}
			});
		}
		np_month.setMinValue(mode == MODE_FULL_VERSION ? min_month : 1);
		np_month.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
				Calendar mycal = new GregorianCalendar(np_year.getValue(), np_month.getValue() - 1, 1);
				if (mode == MODE_FULL_VERSION && np_year.getValue() == min_year && newVal == min_month)
					np_day.setMinValue(min_date);
				else np_day.setMinValue(1);
				int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);
				np_day.setMaxValue(daysInMonth);
				np_day.invalidate();
			}
		});
		np_month.setMaxValue(12);
		np_month.setValue(DateProvider.getThisMonth());
		np_month.setFocusable(true);
		np_month.setFocusableInTouchMode(true);
		np_month.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		np_month.setDivider(getResources().getDrawable(res_divider));
		if (month > 0) {
			np_month.setValue(month);
		} else {
			np_month.setValue(8);
		}

		Calendar mycal = new GregorianCalendar(np_year.getValue(), np_month.getValue() - 1, 1);
		int daysInMonth = mycal.getActualMaximum(Calendar.DAY_OF_MONTH);

		np_day.setMinValue(mode == MODE_FULL_VERSION ? min_date : 1);
		np_day.setMaxValue(daysInMonth);
		if (day > 0) {
			np_day.setValue(day);
		} else {
			np_day.setValue(4);
		}
		np_day.setFocusable(true);
		np_day.setFocusableInTouchMode(true);
		np_day.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		np_day.setDivider(getResources().getDrawable(res_divider));

		np_hour.setMinValue(0);
		np_hour.setMaxValue(23);
		np_hour.setValue(12);
		np_hour.setDivider(getResources().getDrawable(res_divider));

		np_minute.setMinValue(0);
		np_minute.setMaxValue(59);
		np_minute.setValue(0);
		np_minute.setDivider(getResources().getDrawable(res_divider));

		np_month.setFormatter(formatter);
		np_day.setFormatter(formatter);
		np_hour.setFormatter(formatter);
		np_minute.setFormatter(formatter);
//        np_year.setValue(1994, true);

		Dialog dialog = new Dialog(getActivity(), null, "");


		dialog.setCustomView(custom);

		dialog.show();
//        dialog.hide();
		dialog.setOnAcceptButtonClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
				if (l1 != null) {
					l1.onDatePicked(getValue());
				}
			}
		});
		ButtonFlat ab = dialog.getButtonAccept();
		TypedArray a = getActivity().getTheme().obtainStyledAttributes(res_theme, new int[]{R.attr.selectionDivider});
		int attributeResourceId = a.getResourceId(0, 0);
		a.recycle();


		ab.getTextView().setTextColor(getResources().getColor(attributeResourceId));
		ab.setText("确定");
		ButtonFlat cb = dialog.getButtonCancel();
		cb.setText("取消");

		return dialog;
	}

	String getValue() {
		switch (mode) {
			case MODE_DATE:
			case MODE_TIME:
			default:
				return np_year.getValue() + "-" + formatter.format(np_month.getValue()) + "-" + formatter.format(np_day.getValue());
			case MODE_FULL_VERSION:
				JSONObject jo = new JSONObject();
				try {
					jo.put("date", np_year.getValue() + "-" + formatter.format(np_month.getValue()) + "-" + formatter.format(np_day.getValue()));
					jo.put("time", formatter.format(np_hour.getValue()) + ":" + formatter.format(np_minute.getValue()));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				return jo.toString();
		}
	}


	public void setOnDatePickedListener(OnDatePickedListener lis) {
		l1 = lis;
	}

	public interface OnDatePickedListener {

		public void onDatePicked(String result);
	}
}
