package com.viktorkhon.udacity_project_10_inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.viktorkhon.udacity_project_10_inventoryapp.Data.InventoryContract.InventoryEntry;

public class CatalogActivity extends AppCompatActivity
implements LoaderManager.LoaderCallbacks<Cursor> {

    public static InventoryAdapter inventoryAdapter;

    private static final int INV_LOADER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Make button open new Activity to add product
        Button addProduct = (Button)findViewById(R.id.addProduct);
        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        // Find ListView by ID
        ListView invListView = (ListView) findViewById(R.id.list_view);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        invListView.setEmptyView(emptyView);

        // Initialize adapter and set it to the ListView
        inventoryAdapter = new InventoryAdapter(this, null);
        invListView.setAdapter(inventoryAdapter);

        // Make individual list items to be clicked and then open a new Activity to edit them
        invListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                Intent intent = new Intent(CatalogActivity.this, EditActivity.class);

                // Assign a Uri ID based on the list item that was clicked
                Uri currentUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

                // Assign Uri to the intent
                intent.setData(currentUri);

                // Open an activity with data from the currentUri
                startActivity(intent);
            }
        });

        // Initialize the LoaderManager
        getLoaderManager().initLoader(INV_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Initialize the columns to be appear in the ListView
        String[] projection = {
                InventoryEntry.COLUMN_ID,
                InventoryEntry.COLUMN_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QTY
        };

        // Load CursorLoader with the whole table information
        return new CursorLoader(this,
                InventoryEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    // Swap a cursor with a new data upon reloading
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        inventoryAdapter.swapCursor(data);
    }

    // Make cursor Null upon Reset
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        inventoryAdapter.swapCursor(null);
    }
}

