package com.example.thuthuchichi;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Database extends SQLiteOpenHelper {

    private static int DB_VERSION = 2;
    private  static String DB_PATH = "";
    private  static String DB_NAME = "thuchiDB.db";
    private SQLiteDatabase db;
    private Context mContext=null;

    private static String DB_CREATE_TABLE_INCOMECATALOG = "CREATE TABLE \"IncomeCatalog\" (\n" +
            "\t\"ID\"\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
            "\t\"Name\"\tTEXT NOT NULL,\n" +
            "\t\"Type\"\tTEXT NOT NULL\n" +
            ");";

    public Database(Context c){
        super(c,DB_NAME,null,DB_VERSION);
        if(Build.VERSION.SDK_INT>=17)
            DB_PATH=c.getApplicationInfo().dataDir+"/";
        else
            DB_PATH="data/data/"+c.getPackageName()+"/";

        mContext = c;

       if(CheckDB()){
           OpenDB();
       }
       else{
           CreateDB();
       }
    }

    public void CreateDB(){
        this.getWritableDatabase();

        try {
            CopyDB();
        }

        catch (IOException e) {e.printStackTrace();}
    }

    public void CopyDB() throws IOException {
        AssetManager dirPATH = mContext.getAssets();
        InputStream myinput = dirPATH.open(DB_NAME);
        String outFileName = DB_PATH+DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);


        byte[] buffer = new byte[1024];
        int length;

        while ((length = myinput.read(buffer))>0)
        {
            myOutput.write(buffer,0,length);
        }

        myOutput.flush();
        myOutput.close();
        myinput.close();
    }

    @Override
    public synchronized void close() {
        if(db!=null)
            db.close();
        super.close();
    }

    public boolean CheckDB(){
        File dbFile = new File(DB_PATH + DB_NAME);
        return dbFile.exists();
    }

    public SQLiteDatabase getdb() {return db;}

    public void OpenDB(){
        String Mypath=DB_PATH+DB_NAME;
        db=SQLiteDatabase.openDatabase(Mypath,null,SQLiteDatabase.OPEN_READWRITE);
    }

    //create db for the first using (in case did not add db file to source)
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    //update db when update app and db version change.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion<2)
            db.execSQL(DB_CREATE_TABLE_INCOMECATALOG);
    }
}
