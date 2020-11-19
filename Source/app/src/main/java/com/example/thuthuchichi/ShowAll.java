package com.example.thuthuchichi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ShowAll extends AppCompatActivity implements android.widget.CompoundButton.OnCheckedChangeListener{

    TextView monthPick,Total;
    Database dbHelper;
    SQLiteDatabase db;
    Button AllBtn,TotalBtn;
    Cursor data;
    ArrayList<Income> ListAll;
    ListView lvListIncome;
    AllAdapter adapter;
    EditText filterEdit;
    ImageButton filterBtn;
    ArrayList<Integer> listIncId,listOutId;
    ConstraintLayout mainLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all);

        dbHelper = new Database(getApplicationContext());
        db = dbHelper.getdb();

        listIncId = new ArrayList<Integer>();
        listOutId = new ArrayList<Integer>();

        mainLay = findViewById(R.id.allMainLay);

        monthPick = findViewById(R.id.MonthpickerEdit);
        AllBtn = findViewById(R.id.AllBtn);
        lvListIncome = findViewById(R.id.lvAll);
        Total = findViewById(R.id.TotalAll);

        filterBtn = findViewById(R.id.filterBtn);
        filterEdit = findViewById(R.id.filterEdit);
        TotalBtn =findViewById(R.id.TotalBtn);

        filterEdit.clearFocus();

        ListAll=new ArrayList<Income>();

        String Query = "select * from Incoming UNION SELECT * from Outgoing";
        data = db.rawQuery(Query,null);
        GetSum();
        HandleReturnData();
        adapter = new AllAdapter(this,R.layout.all_data_row,ListAll);
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
                if(month.equals(""))
                    Toast.makeText(ShowAll.this,"Hãy chọn ngày hoặc nhấn nút xem tất cả",Toast.LENGTH_SHORT).show();
                else{
                    try {
                        String Query = "Select * from Incoming where Date LIKE '" + month + "%' Union Select * from Outgoing where Date LIKE '" + month + "%'";
                        data = db.rawQuery(Query, null);
                        GetSum();
                        HandleReturnData();

                        adapter.notifyDataSetChanged();
                    }catch (Exception e){
                        Toast.makeText(ShowAll.this,e.getMessage(),Toast.LENGTH_LONG).show();

                    }
                }
            }
        });

        AllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String Query = "Select * from Incoming union select * from Outgoing";
                    data = db.rawQuery(Query, null);
                    monthPick.setText("");
                    filterEdit.setText("");
                    HandleReturnData();
                    GetSum();

                    adapter.notifyDataSetChanged();
                }catch (Exception e){
                    Toast.makeText(ShowAll.this,e.getMessage(),Toast.LENGTH_LONG).show();

                }

            }
        });

        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String filterContent = String.valueOf(filterEdit.getText());
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainLay.getWindowToken(), 0);
                if(!filterContent.equals("")){
                    //get time on monthPick to check
                    try {
                        String temp = ""+ monthPick.getText();
                        String Query = "";
                        if (temp.equals("")) {
                            Query = "Select * from Incoming where Note LIKE '%" + filterContent + "%' union Select * from Outgoing where Note LIKE '%" + filterContent + "%'";

                        } else {
                            Query = "Select * from Incoming where Date LIKE '" + temp + "%' AND Note LIKE '%" + filterContent + "%' union Select * from Outgoing where Date LIKE '" + temp + "%' AND Note LIKE '%" + filterContent + "%'";

                        }
                        data = db.rawQuery(Query, null);
                        adapter.notifyDataSetChanged();
                        HandleReturnData();
                        GetSum();
                    }catch (Exception e){
                        Toast.makeText(ShowAll.this,e.getMessage(),Toast.LENGTH_LONG).show();

                    }

                }

            }
        });

        TotalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder sbInc = new StringBuilder("");
                StringBuilder sbOut = new StringBuilder("");
                if(listIncId.size()>0){
                    sbInc.append("Select Sum(Quantity) as Total from Incoming where Id IN ("+listIncId.get(0));
                    int size = listIncId.size();
                    for (int i =1;i<size;i++){
                        sbInc.append(","+listIncId.get(i));
                    }
                    sbInc.append(")");
                }

                if(listOutId.size()>0){
                    sbOut.append("Select Sum(Quantity) as Total from Outgoing where Id IN ("+listOutId.get(0));
                    int size = listOutId.size();
                    for (int i =1;i<size;i++){
                        sbOut.append(","+listOutId.get(i));
                    }
                    sbOut.append(")");
                }


                try {
                    Cursor CusSum;
                    int sum;
                    NumberFormat formatter = new DecimalFormat("###,###.#");

                    String incQuery = sbInc.toString();
                    String outQuery = sbOut.toString();

                    if(!incQuery.equals("") && !outQuery.equals("")){
                        CusSum = db.rawQuery("Select Sum(Total) as AllQuantity from ("+sbInc.toString()+" UNION ALL "+sbOut.toString()+")",null);
                        if(CusSum.moveToFirst()){
                            sum = CusSum.getInt(CusSum.getColumnIndex("AllQuantity"));
                            ShowTotalDialog(formatter.format(sum));
                        }
                    }
                    else if(incQuery.equals("") && !outQuery.equals("")){
                        CusSum = db.rawQuery(outQuery,null);
                        if(CusSum.moveToFirst()){
                            sum = CusSum.getInt(CusSum.getColumnIndex("Total"));
                            ShowTotalDialog(formatter.format(sum));
                        }

                    }
                    else{
                        CusSum = db.rawQuery(incQuery,null);
                        if(CusSum.moveToFirst()){
                            sum = CusSum.getInt(CusSum.getColumnIndex("Total"));
                            ShowTotalDialog(formatter.format(sum));
                        }

                    }

                }catch (Exception e){
                    Toast.makeText(ShowAll.this,e.getMessage(),Toast.LENGTH_LONG).show();
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
            ListAll.clear();
            while (data.moveToNext()){
                ListAll.add(new Income(data.getInt(0),data.getString(1),data.getInt(2),data.getString(3)));
            }
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dropmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {


        }
        return super.onOptionsItemSelected(item);
    }

    void GetSum(){
        Cursor sumCursor;
        String time = ""+ monthPick.getText();
        String filterContent = String.valueOf(filterEdit.getText());
        String IncomeSum,OutcomeSum;
        try {
            if (!time.equals("")) {
                if (filterContent.equals("")) {
                    IncomeSum = "Select sum(Quantity) As columnToSum From Incoming where Date LIKE '" + time + "%'";
                    OutcomeSum = "Select sum(Quantity) As columnToSum From Outgoing where Date LIKE '" + time + "%'";
                } else {
                    IncomeSum = "Select sum(Quantity) As columnToSum From Incoming where Date LIKE '" + time + "%' AND Note LIKE '%" + filterContent + "%'";
                    OutcomeSum = "Select sum(Quantity) As columnToSum From Outgoing where Date LIKE '" + time + "%' AND Note LIKE '%" + filterContent + "%'";
                }


            } else {
                if (filterContent.equals("")) {
                    IncomeSum = "Select sum(Quantity) As columnToSum From Incoming";
                    OutcomeSum = "Select sum(Quantity) As columnToSum From Outgoing";
                } else {
                    IncomeSum = "Select sum(Quantity) As columnToSum From Incoming where Note LIKE '%" + filterContent + "%'";
                    OutcomeSum = "Select sum(Quantity) As columnToSum From Outgoing where Note LIKE '%" + filterContent + "%'";
                }
            }

            sumCursor = db.rawQuery("Select sum(columnToSum) As Total From (" + IncomeSum + " union all " + OutcomeSum + ")", null);

            if (sumCursor != null) {
                if (sumCursor.moveToFirst()) {
                    int sum = sumCursor.getInt(sumCursor.getColumnIndex("Total"));
                    NumberFormat formatter = new DecimalFormat("###,###.#");

                    Total.setText(formatter.format(sum));
                }
            } else
                Total.setText("0");
        }catch (Exception e){
            Toast.makeText(ShowAll.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pos = lvListIncome.getPositionForView(buttonView);
        if(pos!=ListView.INVALID_POSITION){
            Income inc = ListAll.get(pos);
            inc.setSelected(isChecked);

            if(inc.getMoney()>0){
                if(isChecked)
                    listIncId.add(inc.getId());
                else
                    listIncId.remove((Integer) inc.getId());
            }
            else{
                if(isChecked)
                    listOutId.add(inc.getId());
                else
                    listOutId.remove((Integer) inc.getId());
            }
        }

        if(listIncId.size() + listOutId.size()>1){
            TotalBtn.setEnabled(true);
        }
        else{
            TotalBtn.setEnabled(false);
        }
    }

}
