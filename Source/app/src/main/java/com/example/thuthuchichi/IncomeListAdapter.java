package com.example.thuthuchichi;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class IncomeListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Income> listIncome;
    private String regexStr = "^[0-9]*$";

    Database dbHelper;
    SQLiteDatabase db;

    public IncomeListAdapter(Context c, int l, ArrayList<Income> li){
        context=c;
        layout=l;
        listIncome=li;

        dbHelper = new Database(context);
        db = dbHelper.getdb();
    }

    static class ViewHolder {
        TextView mID,mDate,mMoney,mNote;
        ImageButton del,edit;
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
            viewHolder.del=rowView.findViewById(R.id.DelBtn);
            viewHolder.edit = rowView.findViewById(R.id.EditBtn);
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

        holder.del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteDialog(inc.getId(),position);
            }
        });

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditDialog(inc,position);
            }
        });

        return rowView;
    }

    void EditDialog(final Income income, final int pos){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_dialog);
        dialog.setCanceledOnTouchOutside(false);

        TextView Title = dialog.findViewById(R.id.DialogTitle);
        ImageButton closeBtn = dialog.findViewById(R.id.CloseEditBtn);
        final TextView DateEditDialog = dialog.findViewById(R.id.DateEditDialog);
        final EditText MoneyEditDialog = dialog.findViewById(R.id.MoneyEditDialog);
        final EditText NoteEditDialog = dialog.findViewById(R.id.NoteEditDialog);
        Button SaveChangesBtn = dialog.findViewById(R.id.SaveChangeBtn);

        Title.setText("Sửa dữ liệu "+income.getId());
        DateEditDialog.setText(income.getDate());
        MoneyEditDialog.setText(""+income.getMoney());
        NoteEditDialog.setText(income.getNote());

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        DateEditDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickADate(DateEditDialog);
            }
        });

        checkWhenTextChange(MoneyEditDialog,NoteEditDialog);

        SaveChangesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String DateTemp = (String) DateEditDialog.getText();
                int moneyTemp =Integer.valueOf(String.valueOf(MoneyEditDialog.getText()));
                String noteTemp = String.valueOf(NoteEditDialog.getText());

               try{
                   if(!DateTemp.equals("") && moneyTemp>0 && !noteTemp.equals("")){
                       //query to delete from db
                       String q = "Update Incoming Set Date ='"+DateTemp+"', Quantity ="+moneyTemp+", Note = '"+noteTemp+"' Where ID ="+income.getId();
                       db.execSQL(q);

                       //delete from list
                       listIncome.get(pos).setDate(DateTemp);
                       listIncome.get(pos).setMoney(moneyTemp);
                       listIncome.get(pos).setNote(noteTemp);
                       notifyDataSetChanged();
                       Toast.makeText(context,"Cập nhật thành công",Toast.LENGTH_SHORT).show();
                       dialog.dismiss();
                   }
                   else {
                       Toast.makeText(context,"Vui lòng nhập đủ thông tin",Toast.LENGTH_SHORT).show();

                   }
               }catch (Exception e){
                   dialog.dismiss();
                   Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
               }
            }
        });

        dialog.show();
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        dialog.getWindow().setLayout((6 * width)/7, (4 * height)/5);
    }


    void checkWhenTextChange(final EditText money, final EditText note){
        money.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    EditText content = (EditText) v;
                    String c = content.getText().toString();
                    if(!c.matches(regexStr) || c.equals("")) {
                        money.setError("Nhập số tiền");
                    }
                }
            }
        });

        note.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    EditText content = (EditText) v;
                    String c = content.getText().toString();
                    if( c.equals(""))
                        note.setError("Nhập ghi chú");
                }
            }
        });
    }

    void DeleteDialog(final int id, final int pos){
        final AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Cảnh Báo!!");
        alert.setIcon(R.drawable.warning);
        alert.setMessage("Bạn có chắc muốn xóa dòng dữ liệu thu nhập này không? Bạn sẽ không thể khôi phục lại dòng dũ liệu này đâu nha!");

        alert.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    //query to delete from db
                    String q = "Delete From Incoming Where ID ="+id;
                    db.execSQL(q);

                    //delete from list
                    listIncome.remove(pos);
                    notifyDataSetChanged();
                }
                catch (Exception e){
                    Toast.makeText(context,e.getMessage(),Toast.LENGTH_LONG).show();
                }


            }
        });
        alert.show();
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
