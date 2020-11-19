package com.example.thuthuchichi;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ShowIncomeCatalog extends AppCompatActivity {
    TableLayout CatalogTableLayout;
    Database dbHelper;
    SQLiteDatabase db;
    ArrayList<IncomeCatalog> listCatalog;
    Cursor data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_income_catalog);

        dbHelper = new Database(getApplicationContext());
        db = dbHelper.getdb();

        listCatalog = new ArrayList<IncomeCatalog>();
        data = db.rawQuery("Select * from IncomeCatalog;",null);
        HandleReturnData();

        CatalogTableLayout = (TableLayout) findViewById(R.id.tableIncomeCatalog);

        fillData(listCatalog);
    }

    void HandleReturnData(){
        if(data!=null){
            listCatalog.clear();
            while (data.moveToNext()){
                listCatalog.add(new IncomeCatalog(data.getInt(0),data.getString(1),data.getString(2)));
            }
        }

    }

    private void fillData(ArrayList<IncomeCatalog> list){
        for (final IncomeCatalog item:list){
            TableRow tableRow = new TableRow(this);
            tableRow.setLayoutParams(new TableRow.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));

            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    EditDialog(item);
                }
            });

            // Id Column
            TextView textViewId = new TextView(this);
            textViewId.setText(item.getId());
            textViewId.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewId.setPadding(5, 5, 5, 0);
            tableRow.addView(textViewId);

            // Name Column
            TextView textViewName = new TextView(this);
            textViewName.setText(item.getName());
            textViewName.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewName.setPadding(5, 5, 5, 0);
            tableRow.addView(textViewName);

            // Type Column
            TextView textViewType = new TextView(this);
            textViewType.setText(item.getType());
            textViewType.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            textViewType.setPadding(5, 5, 5, 0);
            tableRow.addView(textViewType);

            CatalogTableLayout.addView(tableRow, new TableLayout.LayoutParams(
                    TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT));
        }
    }

    private void EditDialog(IncomeCatalog item){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_income_catalog_dialog);
        dialog.setCanceledOnTouchOutside(false);

        ImageButton closeBtn = dialog.findViewById(R.id.CloseEditBtn);
        TextView Title = dialog.findViewById(R.id.DialogTitle);
        TextView CatalogNameEditDialog = dialog.findViewById(R.id.CatalogNameEditDialog);
        Spinner CatalogTypeSpinnerDialog = dialog.findViewById(R.id.CatalogTypeSpinnerDialog);
        Button SaveChangesCatalogBtn = dialog.findViewById(R.id.SaveChangeCatalogBtn);
        Button DeleteCatalogBtn = dialog.findViewById(R.id.DeleteCatalogBtn);

        Title.setText("Sửa dữ liệu "+item.getId());
        CatalogNameEditDialog.setText(item.getName());
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.catalog_type_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        CatalogTypeSpinnerDialog.setAdapter(adapter);

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
