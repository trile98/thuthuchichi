package com.example.thuthuchichi;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.zip.Inflater;

public class MonthYearPickerDialog extends DialogFragment {
    private static final int MAX_YEAR = 2099;
    private DatePickerDialog.OnDateSetListener listener;

    public void setListener(DatePickerDialog.OnDateSetListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //get layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        Calendar calendar = Calendar.getInstance();

        View dialog = inflater.inflate(R.layout.month_picker,null);
        final NumberPicker monthPicker = dialog.findViewById(R.id.pickerMonth);
        final NumberPicker yearPicker = dialog.findViewById(R.id.pickerYear);

        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(calendar.get(Calendar.MONTH));

        int currentYear = calendar.get(Calendar.YEAR);
        yearPicker.setMinValue(2020);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(currentYear);

        builder.setView(dialog)
                //set action button
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.onDateSet(null,yearPicker.getValue(),monthPicker.getValue(),0);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MonthYearPickerDialog.this.getDialog().cancel();
                    }
                });

        return builder.create();
    }
}
