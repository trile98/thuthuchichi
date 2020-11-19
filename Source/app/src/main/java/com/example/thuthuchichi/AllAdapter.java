package com.example.thuthuchichi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class AllAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private ArrayList<Income> listIncome;
    private String regexStr = "^[0-9]*$";

    Database dbHelper;
    SQLiteDatabase db;

    public AllAdapter(Context c, int l, ArrayList<Income> li){
        context=c;
        layout=l;
        listIncome=li;

        dbHelper = new Database(context);
        db = dbHelper.getdb();
    }

    static class ViewHolder {
        TextView mID,mDate,mMoney,mNote;
        CheckBox checkBox;
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        NumberFormat formatter = new DecimalFormat("###,###.#");
        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(layout, null);

            ViewHolder viewHolder = new ViewHolder();

            viewHolder.mID = rowView.findViewById(R.id.RowId);
            viewHolder.mDate = rowView.findViewById(R.id.RowDate);
            viewHolder.mMoney = rowView.findViewById(R.id.RowMoney);
            viewHolder.mNote = rowView.findViewById(R.id.RowNote);
            viewHolder.checkBox = rowView.findViewById(R.id.dataRowCheck);

            viewHolder.checkBox.setOnCheckedChangeListener((CompoundButton.OnCheckedChangeListener) context);
            rowView.setTag(viewHolder);
        }



        final ViewHolder holder = (ViewHolder) rowView.getTag();
        final Income inc = listIncome.get(position);

        holder.mID.setText(String.valueOf(inc.getId()));
        holder.mDate.setText(inc.getDate());
        holder.mMoney.setText(formatter.format(inc.getMoney()));
        holder.mNote.setText(inc.getNote());
        holder.checkBox.setChecked(inc.isSelected());
        holder.checkBox.setTag(inc);

        return rowView;
    }
}
