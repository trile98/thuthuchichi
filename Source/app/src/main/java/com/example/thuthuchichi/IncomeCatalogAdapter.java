package com.example.thuthuchichi;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class IncomeCatalogAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<IncomeCatalog> listIncomeCatalog;

    @Override
    public int getCount() {
        return listIncomeCatalog.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
