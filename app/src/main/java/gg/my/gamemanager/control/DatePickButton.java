package gg.my.gamemanager.control;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.DatePicker;

import java.util.Calendar;

import gg.my.gamemanager.GameDetailActivity;
import gg.my.gamemanager.R;
import gg.my.gamemanager.helpers.Utils;


public class DatePickButton extends android.support.v7.widget.AppCompatButton {
    public interface DatePickCallback{
        void run(Calendar picked);
    }

    private Calendar current;
    private DatePickCallback pickCallback;

    public DatePickButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnClickListener(this::clickDate);
    }

    public void setDate(Calendar date) {
        this.current = date;
        this.setText(Utils.formatDate(this.current));
    }

    public Calendar getDate() {
        return this.current;
    }

    public void setAfterPickCallback(DatePickCallback callback){
        this.pickCallback = callback;
    }

    // when I click on the date button, it shows a date picker dialog
    private void clickDate(View view) {
        if (view.getId() != R.id.detail_date_button) {
            return;
        }

        DatePickerDialog datePicker = new DatePickerDialog(
                this.getContext(),
                this::pickedDate, // called after picking a date
                current.get(Calendar.YEAR),
                current.get(Calendar.MONTH),
                current.get(Calendar.DAY_OF_MONTH));

        datePicker.show();
    }

    private void pickedDate(DatePicker v, int year, int monthOfYear, int dayOfMonth) {
        Calendar picked = Calendar.getInstance();
        picked.set(Calendar.YEAR, year);
        picked.set(Calendar.MONTH, monthOfYear);
        picked.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        this.setDate(picked);
        this.pickCallback.run(picked);
    }


}
