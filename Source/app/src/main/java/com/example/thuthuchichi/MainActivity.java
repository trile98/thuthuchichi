package com.example.thuthuchichi;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.thuthuchichi.R;

public class MainActivity extends AppCompatActivity {

    Button incomeBtn, outcomeBtn, allBtn,incomeCatalogBtn;
    ImageButton DelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        incomeBtn = (Button) findViewById(R.id.BtnIncome);
        outcomeBtn = (Button) findViewById(R.id.BtnOutcome);
      //  incomeCatalogBtn = (Button) findViewById(R.id.BtnInCatalog);
        allBtn = findViewById(R.id.BtnInOut);
        DelBtn = findViewById(R.id.MainDelBtn);

        incomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,show_list_income.class);
                startActivity(intent);
            }
        });

        outcomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ShowListOutCome.class);
                startActivity(intent);
            }
        });

        /*incomeCatalogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ShowIncomeCatalog.class);
                startActivity(intent);
            }
        });*/

        allBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ShowAll.class);
                startActivity(intent);
            }
        });

        DelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,DeleteYear.class);
                startActivity(intent);
            }
        });
    }


}
