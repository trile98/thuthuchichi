package com.example.thuthuchichi;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class show_list_income extends AppCompatActivity implements android.widget.CompoundButton.OnCheckedChangeListener{
    TextView monthPick,Total;
    Database dbHelper;
    SQLiteDatabase db;
    Button AllBtn,TotalBtn;
    Cursor data;
    ArrayList<Income> ListIncome;
    ListView lvListIncome;
    IncomeListAdapter adapter;
    EditText filterEdit;
    ImageButton filterBtn;
    ArrayList<Integer> listId;

    ConstraintLayout mainLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list_income);

        dbHelper = new Database(getApplicationContext());
        db = dbHelper.getdb();

        listId = new ArrayList<Integer>();

        mainLay = findViewById(R.id.incomeMainLay);

        monthPick = findViewById(R.id.MonthpickerEdit);
        AllBtn = findViewById(R.id.AllBtn);
        lvListIncome = findViewById(R.id.lvIncome);
        Total = findViewById(R.id.TotalIcome);

        filterBtn = findViewById(R.id.filterBtn);
        filterEdit = findViewById(R.id.filterEdit);
        TotalBtn =findViewById(R.id.TotalBtn);

        filterEdit.clearFocus();

        ListIncome=new ArrayList<Income>();

        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String currentdate= format.format(cal.getTime()).toString();

        try{
            String Query = "Select * from Incoming where Date LIKE '"+currentdate+"%'";
            monthPick.setText(currentdate);
            data = db.rawQuery(Query,null);
        }catch (Exception e){
            System.out.println(e.getMessage());
            Toast.makeText(show_list_income.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
        GetSum();
        HandleReturnData();
        adapter = new IncomeListAdapter(this,R.layout.list_data_row,ListIncome);
        lvListIncome.setAdapter(adapter);


        monthPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cal = Calendar.getInstance();
                MonthYearPickerDialog mp = new MonthYearPickerDialog();
                mp.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        cal.set(year,month,dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
                        monthPick.setText(format.format(cal.getTime()));
                        filterEdit.setText("");
                    }
                });
                mp.show(getSupportFragmentManager(),"MonthYearPickerDialog");
            }
        });

        monthPick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String month = String.valueOf(monthPick.getText());
                try{
                    if(month.equals(""))
                        Toast.makeText(show_list_income.this,"Hãy chọn ngày hoặc nhấn nút xem tất cả",Toast.LENGTH_SHORT).show();
                    else{
                        String Query = "Select * from Incoming where Date LIKE '"+month+"%'";
                        data = db.rawQuery(Query,null);
                        GetSum();
                        HandleReturnData();

                        adapter.notifyDataSetChanged();
                    }
                }catch (Exception e){
                    Toast.makeText(show_list_income.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        AllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    String Query = "Select * from Incoming";
                    data = db.rawQuery(Query,null);
                    monthPick.setText("");
                    filterEdit.setText("");
                    GetSum();
                    HandleReturnData();

                    adapter.notifyDataSetChanged();
                }catch (Exception e){
                    Toast.makeText(show_list_income.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }



            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filterContent = String.valueOf(filterEdit.getText());
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainLay.getWindowToken(), 0);
               try{
                   if(!filterContent.equals("")){
                       //get time on monthPick to check
                       String temp = ""+ monthPick.getText();
                       String Query = "";
                       if(temp.equals("")){
                           Query = "Select * from Incoming where Note LIKE '%"+filterContent+"%'";

                       }
                       else{
                           Query = "Select * from Incoming where Date LIKE '"+temp+"%' AND Note LIKE '%"+filterContent+"%'";

                       }
                       data = db.rawQuery(Query,null);
                       GetSum();
                       HandleReturnData();

                       adapter.notifyDataSetChanged();
                   }
               }catch (Exception e){
                   Toast.makeText(show_list_income.this,e.getMessage(),Toast.LENGTH_LONG).show();
               }

            }
        });

        TotalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sb = new StringBuilder(""+listId.get(0));
                int size = listId.size();
                for (int i =1;i<size;i++){
                    sb.append(","+listId.get(i));
                }

                try {
                    Cursor CusSum = db.rawQuery("Select Sum(Quantity) as Total from Incoming where Id IN ("+sb.toString()+")",null);
                    if(CusSum.moveToFirst()){
                        int sum = CusSum.getInt(CusSum.getColumnIndex("Total"));
                        NumberFormat formatter = new DecimalFormat("###,###.#");

                        ShowTotalDialog(formatter.format(sum));
                    }
                }catch (Exception e){
                    Toast.makeText(show_list_income.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    void ShowTotalDialog(String sum){
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Tổng Cộng");
        alert.setMessage(sum);


        alert.setNegativeButton("Đóng", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
    }

    void HandleReturnData(){
       if(data!=null){
           ListIncome.clear();
           while (data.moveToNext()){
               ListIncome.add(new Income(data.getInt(0),data.getString(1),data.getInt(2),data.getString(3)));
           }
       }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_show_list,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.AddNewNav:{
                Intent intent = new Intent(show_list_income.this,add_income.class);
                this.finish();
                startActivity(intent);
            }

        }
        return super.onOptionsItemSelected(item);
    }


    void GetSum(){
        Cursor sumCursor;
        String time = ""+ monthPick.getText();
        String filterContent = String.valueOf(filterEdit.getText());
        try{

            if(!time.equals("")){
                if(filterContent.equals(""))
                    sumCursor = db.rawQuery("Select sum(Quantity) As Total From Incoming where Date LIKE '"+time+"%'",null);
                else
                    sumCursor = db.rawQuery("Select sum(Quantity) As Total From Incoming where Date LIKE '"+time+"%' AND Note LIKE '%"+filterContent+"%'",null);

            }
            else{
                if(filterContent.equals("")){
                    sumCursor = db.rawQuery("Select sum(Quantity) As Total From Incoming",null);

                }
                else {
                    sumCursor = db.rawQuery("Select sum(Quantity) As Total From Incoming where Note LIKE '%"+filterContent+"%'",null);

                }
            }
            if(sumCursor!=null) {
                if (sumCursor.moveToFirst()) {
                    int sum = sumCursor.getInt(sumCursor.getColumnIndex("Total"));
                    NumberFormat formatter = new DecimalFormat("###,###.#");

                    Total.setText(formatter.format(sum));
                }
            }
            else
                Total.setText("0");
        }catch (Exception e){
            Toast.makeText(show_list_income.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }


    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pos = lvListIncome.getPositionForView(buttonView);
        if(pos!=ListView.INVALID_POSITION){
            Income inc = ListIncome.get(pos);
            inc.setSelected(isChecked);
            if(isChecked)
                listId.add(inc.getId());
            else
                listId.remove((Integer) inc.getId());
        }

        if(listId.size()>1){
            TotalBtn.setEnabled(true);
        }
        else{
            TotalBtn.setEnabled(false);
        }
    }
}
