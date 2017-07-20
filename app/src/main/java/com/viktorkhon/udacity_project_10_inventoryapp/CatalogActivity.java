package com.viktorkhon.udacity_project_10_inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.viktorkhon.udacity_project_10_inventoryapp.Data.InventoryContract.InventoryEntry;
import com.viktorkhon.udacity_project_10_inventoryapp.Data.InventoryDbHelper;

public class CatalogActivity extends AppCompatActivity {

    public static InventoryAdapter inventoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        Button addProduct = (Button)findViewById(R.id.addProduct);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });
        displayDb();
    }

    @Override
    protected void onStart() {
    super.onStart();
    displayDb();
    }

    private void displayDb() {

        String[] projection = {
                InventoryEntry.COLUMN_ID,
                InventoryEntry.COLUMN_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QTY
        };

        Cursor cursor = getContentResolver().query(InventoryEntry.CONTENT_URI,
                projection, null, null, null);

        ListView invListView = (ListView) findViewById(R.id.list_view);
        inventoryAdapter = new InventoryAdapter(this, cursor);
        invListView.setAdapter(inventoryAdapter);
    }
}

