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
import java.util.List;

public class ShowListOutCome extends AppCompatActivity implements android.widget.CompoundButton.OnCheckedChangeListener{
    TextView monthPick,Total;
    Database dbHelper;
    SQLiteDatabase db;
    Button AllBtn,TotalBtn;
    Cursor data;
    ArrayList<Outcome> ListOutcome;
    ListView lvListOutcome;
    OutcomeListAdapter adapter;
    EditText filterEdit;
    ImageButton filterBtn;
    ArrayList<Integer> listId;

    ConstraintLayout mainLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list_out_come);

        dbHelper = new Database(getApplicationContext());
        db = dbHelper.getdb();

        listId = new ArrayList<Integer>();

        mainLay=findViewById(R.id.outcomeMainLay);

        monthPick = findViewById(R.id.MonthpickerEdit);
        AllBtn = findViewById(R.id.AllBtn);
        lvListOutcome = findViewById(R.id.lvOutcome);
        Total = findViewById(R.id.TotalOutcome);

        filterBtn = findViewById(R.id.filterBtn);
        filterEdit = findViewById(R.id.filterEdit);
        TotalBtn =findViewById(R.id.TotalBtn);

        filterEdit.clearFocus();

        ListOutcome=new ArrayList<Outcome>();

        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),1);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM");
        String currentdate= format.format(cal.getTime()).toString();

        try{
            String Query = "Select * from Outgoing where Date LIKE '"+currentdate+"%'";
            monthPick.setText(currentdate);
            data = db.rawQuery(Query,null);
        }catch (Exception e){
            Toast.makeText(ShowListOutCome.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }

        GetSum();
        HandleReturnData();
        adapter = new OutcomeListAdapter(this,R.layout.list_data_row,ListOutcome);
        lvListOutcome.setAdapter(adapter);


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
                    if(!month.equals("")){
                        String Query = "Select * from Outgoing where Date LIKE '"+month+"%'";
                        data = db.rawQuery(Query,null);
                        GetSum();
                        HandleReturnData();

                        adapter.notifyDataSetChanged();
                    }
                }catch (Exception e){
                    Toast.makeText(ShowListOutCome.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });

        AllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               try{
                   String Query = "Select * from Outgoing";
                   data = db.rawQuery(Query,null);
                   monthPick.setText("");
                   filterEdit.setText("");
                   GetSum();
                   HandleReturnData();

                   adapter.notifyDataSetChanged();
               }catch (Exception e){
                   Toast.makeText(ShowListOutCome.this,e.getMessage(),Toast.LENGTH_LONG).show();
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
                            Query = "Select * from Outgoing where Note LIKE '%"+filterContent+"%'";

                        }
                        else{
                            Query = "Select * from Outgoing where Date LIKE '"+temp+"%' AND Note LIKE '%"+filterContent+"%'";

                        }
                        data = db.rawQuery(Query,null);
                        GetSum();
                        HandleReturnData();

                        adapter.notifyDataSetChanged();
                    }
                }catch (Exception e){
                    Toast.makeText(ShowListOutCome.this,e.getMessage(),Toast.LENGTH_LONG).show();
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
                    Cursor CusSum = db.rawQuery("Select Sum(Quantity) as Total from Outgoing where Id IN ("+sb.toString()+")",null);
                    if(CusSum.moveToFirst()){
                        int sum = CusSum.getInt(CusSum.getColumnIndex("Total"));
                        NumberFormat formatter = new DecimalFormat("###,###.#");

                        ShowTotalDialog(formatter.format(sum));
                    }
                }catch (Exception e){
                    Toast.makeText(ShowListOutCome.this,e.getMessage(),Toast.LENGTH_LONG).show();
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
            ListOutcome.clear();

            while (data.moveToNext()){
                ListOutcome.add(new Outcome(data.getInt(0),data.getString(1),data.getInt(2),data.getString(3)));
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
                Intent intent = new Intent(ShowListOutCome.this,addOutcome.class);
                this.finish();
                startActivity(intent);
            }

        }
        return super.onOptionsItemSelected(item);
    }


    void GetSum(){
        Cursor sumCursor;
        String time = ""+monthPick.getText();
        String filterContent = String.valueOf(filterEdit.getText());

        try{
            if(!time.equals("")){
                if(filterContent.equals(""))
                    sumCursor = db.rawQuery("Select sum(Quantity) As Total From Outgoing where Date LIKE '"+time+"%'",null);
                else
                    sumCursor = db.rawQuery("Select sum(Quantity) As Total From Outgoing where Date LIKE '"+time+"%' AND Note LIKE '%"+filterContent+"%'",null);

            }
            else{
                if(filterContent.equals("")){
                    sumCursor = db.rawQuery("Select sum(Quantity) As Total From Outgoing",null);

                }
                else {
                    sumCursor = db.rawQuery("Select sum(Quantity) As Total From Outgoing where Note LIKE '%"+filterContent+"%'",null);

                }
            }
            if(sumCursor!=null) {
                if (sumCursor.moveToFirst()) {
                    int sum = sumCursor.getInt(sumCursor.getColumnIndex("Total"));
                    NumberFormat formatter = new DecimalFormat("###,###.#");

                    Total.setText(formatter.format(sum));
                }
            }
            else {
                Total.setText("0");
            }
        }catch (Exception e){
            Toast.makeText(ShowListOutCome.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pos = lvListOutcome.getPositionForView(buttonView);
        if(pos!=ListView.INVALID_POSITION){
            Outcome out = ListOutcome.get(pos);
            out.setSelected(isChecked);
            if(isChecked)
                listId.add(out.getId());
            else
                listId.remove((Integer) out.getId());
        }

        if(listId.size()>1){
            TotalBtn.setEnabled(true);
        }
        else{
            TotalBtn.setEnabled(false);
        }
    }
}
