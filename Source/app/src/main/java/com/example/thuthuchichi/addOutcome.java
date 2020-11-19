package com.example.thuthuchichi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class addOutcome extends AppCompatActivity {

    ListView lvNewOutcome;
    ArrayList<Outcome> listOutcome;
    OutcomeAdapter Adapter;
    FloatingActionButton addBtn;
    CoordinatorLayout mainLay;

    Database dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_outcome);

        dbHelper = new Database(getApplicationContext());
        db = dbHelper.getdb();

        mainLay = findViewById(R.id.addOutcomeMainLay);

        addBtn = (FloatingActionButton) findViewById(R.id.addBtn);

        lvNewOutcome = findViewById(R.id.lvAdd);
        listOutcome = new ArrayList<>();

        listOutcome.add(new Outcome("",0,""));

        Adapter = new OutcomeAdapter(this,R.layout.add_new_row,listOutcome);

        lvNewOutcome.setAdapter(Adapter);


        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(mainLay.getWindowToken(), 0);
                listOutcome.add(new Outcome("",0,""));
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
                        db.execSQL("Insert into Outgoing values "+sb.toString());
                        moveToPre();
                    }
                    else
                        Toast.makeText(this,"Vui lòng nhập đủ thông tin",Toast.LENGTH_SHORT).show();
                }catch (Exception e){
                    Toast.makeText(addOutcome.this,e.getMessage(),Toast.LENGTH_LONG);
                }
            }
        }
        return super.onOptionsItemSelected(item);
    }

    int Setquery(StringBuilder sb){
        int result;
        int l = listOutcome.size()-1;
        int dem =0;
        for (Outcome o:listOutcome) {
            result = o.prepareQuery(sb);
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
        Intent intent = new Intent(addOutcome.this,ShowListOutCome.class);
        this.finish();
        startActivity(intent);
    }
}
