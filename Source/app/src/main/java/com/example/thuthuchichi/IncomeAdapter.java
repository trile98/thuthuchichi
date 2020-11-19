package com.example.thuthuchichi;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.RecyclerView.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class IncomeAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Income> listIncome;
    private String regexStr = "^[0-9]*$";

    public IncomeAdapter(Context c, int l, ArrayList<Income> li){
        context=c;
        layout=l;
        listIncome=li;
    }

    @Override
    public int getCount() {
        return listIncome.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        TextView mDateEdit;
        EditText mMoneyEdit;
        EditText mNoteEdit;
        ImageButton mCancelBtn;
        TextView mTitle;
    }

    //if notifyDataChange run, list view will load again and this function is called from beginning
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(layout, null);

            ViewHolder viewHolder = new ViewHolder();

            viewHolder.mDateEdit = rowView.findViewById(R.id.DateEdit);
            viewHolder.mMoneyEdit=rowView.findViewById(R.id.MoneyEdit);
            viewHolder.mNoteEdit = rowView.findViewById(R.id.NoteEdit);
            viewHolder.mCancelBtn = rowView.findViewById(R.id.CancelBtn);
            viewHolder.mTitle = rowView.findViewById(R.id.RowTitle);

            rowView.setTag(viewHolder);
        }



//        fill data
        final ViewHolder holder = (ViewHolder) rowView.getTag();
        Income inc = listIncome.get(position);

        holder.mDateEdit.setInputType(InputType.TYPE_NULL);

        int displayPos = position+1;

        holder.mDateEdit.setText(inc.getDate());
        holder.mMoneyEdit.setText(inc.getMoney()+"");
        holder.mNoteEdit.setText(inc.getNote());

        holder.mTitle.setText("Dữ Liệu "+displayPos);

        holder.mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listIncome.size()>1){
                    listIncome.remove(position);
                    notifyDataSetChanged();
                }
                else{
                    Toast.makeText(context,"Giữ lại ít nhất một khung",Toast.LENGTH_LONG).show();
                }
            }
        });

        holder.mDateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickADate(holder.mDateEdit);

            }
        });

        UpdateValueAfterTextChange(holder,position);

        return rowView;
    }

    void UpdateValueAfterTextChange(final ViewHolder h, final int pos){

        h.mDateEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String c = h.mDateEdit.getText().toString();
                if(!c.equals(""))
                    listIncome.get(pos).setDate(h.mDateEdit.getText().toString());
                else
                    h.mDateEdit.setError("Chọn ngày");
            }
        });

        h.mMoneyEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    EditText content = (EditText) v;
                    String c = content.getText().toString();
                    if(c.matches(regexStr) && !c.equals(""))
                        listIncome.get(pos).setMoney(Integer.valueOf(content.getText().toString()));
                    else
                        h.mMoneyEdit.setError("Nhập số tiền");
                }
            }
        });

        h.mNoteEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText content = (EditText) v;
                String c = content.getText().toString();
                if(!c.equals(""))
                    listIncome.get(pos).setNote(content.getText().toString());
                else
                    h.mNoteEdit.setError("Nhập ghi chú về số tiền");
            }
        });

    }

    void pickADate(final TextView DateEdit){
        final Calendar cal = Calendar.getInstance();
        int D = cal.get(Calendar.DATE);
        int M = cal.get(Calendar.MONTH);
        int Y = cal.get(Calendar.YEAR);

        DatePickerDialog datepicker = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(year,month,dayOfMonth);
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                DateEdit.setText(format.format(cal.getTime()));
            }
        },Y,M,D);
        datepicker.show();


    }

}
