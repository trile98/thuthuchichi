package com.example.thuthuchichi;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.Image;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import java.util.ArrayList;
import com.google.android.material.floatingactionbutton.*;

public class add_income extends AppCompatActivity {

    ListView lvNewIncome;
    ArrayList<Income> listIncome;
    IncomeAdapter Adapter;
    FloatingActionButton addBtn;

    Database dbHelper;
    SQLiteDatabase db;

    CoordinatorLayout mainLay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        dbHelper = new Database(getApplicationContext());
        db = dbHelper.getdb();

        mainLay = findViewById(R.id.addIncomeMainLay);

        addBtn = (FloatingActionButton) findViewById(R.id.addBtn);

        lvNewIncome = findViewById(R.id.lvAdd);
        listIncome = new ArrayList<>();

        listIncome.add(new Income("",0,""));

        Adapter = new IncomeAdapter(this,R.layout.add_new_row,listIncome);

        lvNewIncome.setAdapter(Adapter);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainLay.getWindowToken(), 0);
                listIncome.add(new Income("",0,""));
                Adapter.notifyDataSetChanged();

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.saveBtn:{
                try{
                    StringBuilder sb = new StringBuilder("");
                    int QueryResult = Setquery(sb);
                    if(QueryResult==1){
                        db.execSQL("insert into Incoming values "+sb.toString());
                        moveToPre();
                    }
                    else
                        Toast.makeText(this,"Vui lòng nhập đủ thông tin",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(add_income.this,e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        }
        return super.onOptionsItemSelected(item);
    }

    int Setquery(StringBuilder sb){
        int result;
        int l = listIncome.size()-1;
        int dem =0;
        for (Income i:listIncome) {
            result = i.prepareQuery(sb);
            if(dem==l){
                sb.append(";");
            }
            else {
                sb.append(",");
            }
            dem++;
            if(result==0)
                return 0;
        }
        return 1;
    }

    void moveToPre(){
        Intent intent = new Intent(add_income.this,show_list_income.class);
        this.finish();
        startActivity(intent);
    }
}
