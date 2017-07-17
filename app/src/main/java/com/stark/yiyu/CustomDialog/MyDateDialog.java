package com.stark.yiyu.CustomDialog;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;

import com.stark.yiyu.NetWork.MD5;
import com.stark.yiyu.R;

import java.util.Calendar;

/**
 * Created by asus on 2017/7/8.
 */
public class MyDateDialog extends AlertDialog implements DatePicker.OnDateChangedListener {

    private DatePicker mDatePicker;
    private OnDateSetListener mDateSetListener;
    private Calendar mCalendar;
    private boolean mTitleNeedUpdate = true;

    private View view;

    public interface OnDateSetListener{
        void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth);
    }

    public MyDateDialog(Context context, OnDateSetListener callBack, int year, int monthOfYear, int dayOfMonth) {
        this(context, 0, callBack, year, monthOfYear, dayOfMonth);
    }

    public MyDateDialog(Context context, int theme, OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
        super(context, theme);
        mDateSetListener = listener;
        mCalendar = Calendar.getInstance();

        Context themeContext = getContext();
        LayoutInflater inflater = LayoutInflater.from(themeContext);
        view = inflater.inflate(R.layout.date_picker_dialog, null);
//        view.setBackgroundColor(Color.WHITE);

        mDatePicker = (DatePicker) view.findViewById(R.id.datePicker);

        mDatePicker.setMaxDate(System.currentTimeMillis());
        mDatePicker.init(year, monthOfYear, dayOfMonth, this);
        setButton();
    }

    private void setButton() {
        view.findViewById(R.id.date_picker_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDatePicker != null) {
                    mDatePicker.clearFocus();
                    mDateSetListener.onDateSet(mDatePicker, mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
                    dismiss();
                }
            }
        });

        view.findViewById(R.id.date_picker_cancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });
    }

    public void myShow() {
        show();
        setContentView(view);
    }

    @Override
    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mDatePicker.init(year,monthOfYear,dayOfMonth,this);
    }

    public DatePicker getDatePicker() {
        return mDatePicker;
    }

    public void updateDate(int year, int monthOfYear, int dayOfMonth) {
        mDatePicker.updateDate(year, monthOfYear, dayOfMonth);
    }


    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt("year", mDatePicker.getYear());
        state.putInt("month", mDatePicker.getMonth());
        state.putInt("day", mDatePicker.getDayOfMonth());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int year = savedInstanceState.getInt("year");
        int month = savedInstanceState.getInt("month");
        int day = savedInstanceState.getInt("day");
        mDatePicker.init(year, month, day, this);
    }
}
