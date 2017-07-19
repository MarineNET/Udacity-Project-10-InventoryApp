package com.viktorkhon.udacity_project_10_inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.viktorkhon.udacity_project_10_inventoryapp.Data.InventoryContract;
import com.viktorkhon.udacity_project_10_inventoryapp.Data.InventoryContract.InventoryEntry;
import com.viktorkhon.udacity_project_10_inventoryapp.Data.InventoryDbHelper;

public class CatalogActivity extends AppCompatActivity {

    InventoryDbHelper mDbHelper;

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

        mDbHelper = new InventoryDbHelper(this);

        displayDb();
    }

    @Override
    protected void onStart() {
    super.onStart();
    displayDb();
    }

    private void displayDb() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = db.query(InventoryEntry.TABLE_NAME,
                null, null, null, null, null, null);

        TextView displayView = (TextView) findViewById(R.id.textView4);

        try {
            displayView.setText("Currently there are " + cursor.getCount() + " objects in db\n");
            displayView.append(InventoryEntry.COLUMN_ID + " - " +
                    InventoryEntry.COLUMN_NAME + " - " +
                    InventoryEntry.COLUMN_QTY + " - " +
                    InventoryEntry.COLUMN_PRICE);

            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_NAME));
                int price = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRICE));
                int qty = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_QTY));

                displayView.append("\n" + id + " - " + name + " - " + price + " - " + qty);
            }
        }
        finally {
                cursor.close();
        }
    }
}

