package com.example.thuthuchichi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class DeleteYear extends AppCompatActivity {

    TextView YearPicker;
    Button deleteBtn;

    Database dbHelper;
    SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_year);


        dbHelper = new Database(DeleteYear.this);
        db = dbHelper.getdb();


        YearPicker = findViewById(R.id.DeleteYSetY);
        deleteBtn = findViewById(R.id.DeleteYBtn);

        YearPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cal = Calendar.getInstance();
                YearPickerDialog mp = new YearPickerDialog();
                mp.setListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        cal.set(year,month,dayOfMonth);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy");
                        YearPicker.setText(format.format(cal.getTime()));
                    }
                });
                mp.show(getSupportFragmentManager(),"YearPickerDialog");
            }
        });


        YearPicker.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!YearPicker.getText().equals("")){
                    deleteBtn.setEnabled(true);
                    deleteBtn.setVisibility(View.VISIBLE);
                }
                else{
                    deleteBtn.setEnabled(false);
                    deleteBtn.setVisibility(View.INVISIBLE);
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //query to delete from db
                    String q = "Delete From Incoming Where Date LIKE '"+YearPicker.getText()+"-%'";
                    db.execSQL(q);

                    q = "delete from sqlite_sequence where name='Incoming';";
                    db.execSQL(q);

                    q = "Delete From Outgoing Where Date LIKE '"+YearPicker.getText()+"-%';";
                    db.execSQL(q);

                    q = "delete from sqlite_sequence where name='Outgoing';";
                    db.execSQL(q);

                    moveToPre();
                }catch (Exception e){
                    Toast.makeText(DeleteYear.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    void moveToPre(){
        Intent intent = new Intent(DeleteYear.this,MainActivity.class);
        this.finish();
        startActivity(intent);
    }
}
