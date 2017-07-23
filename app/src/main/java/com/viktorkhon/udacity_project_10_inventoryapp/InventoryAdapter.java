package com.viktorkhon.udacity_project_10_inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.viktorkhon.udacity_project_10_inventoryapp.Data.InventoryContract;
import com.viktorkhon.udacity_project_10_inventoryapp.Data.InventoryContract.InventoryEntry;


/**
 * Created by Viktor Khon on 7/20/2017.
 */

public class InventoryAdapter extends CursorAdapter {

    public InventoryAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.lits_item, viewGroup, false);
    }

    /**
     * Bind an existing view to the data pointed to by cursor
     * @param view - Individual View in a ListView
     * @param context - CatalogActivity
     * @param cursor - The cursor from which to get the data.
     *               The cursor is already moved to the correct position.
     */
    @Override
    public void bindView(View view, Context context, final Cursor cursor) {

        // Give variables to each of the views in list_items.xml
        TextView nameView = (TextView) view.findViewById(R.id.lv_name);
        final TextView qtyView = (TextView) view.findViewById(R.id.lv_quantity);
        TextView priceView = (TextView) view.findViewById(R.id.lv_price);
        Button saleButton = (Button) view.findViewById(R.id.lv_button);

        // Take out the value of each column and store it in a variable
        String name = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_NAME));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_QTY));
        double price = cursor.getDouble(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRICE));

        // Assign a value extracted from the table to corresponding View in the ListView
        nameView.setText(name);
        qtyView.setText(String.valueOf(quantity));
        priceView.setText(String.valueOf(price));

        // Gets position of a cursor in the list
        final int position = cursor.getPosition();

        // Credits to Udacity forum and mentors with figuring out the whole code
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();

                Uri uri = null;
                // Upon click, get cursor of the specified position
                cursor.moveToPosition(position);
                // get _id number of _ID column
                long id = cursor.getLong(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_ID));

                // uri gets the id number of a clicked row
                Uri idToChange = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);

                    // Extract an int value from the view that holds quantities
                    int quantity = Integer.parseInt(qtyView.getText().toString());
                    quantity --;
                    if (quantity <= 0) {
                        quantity = 0;
                    }

                // create a new value for the quatity column and add the updated number to the column
                values.put(InventoryEntry.COLUMN_QTY, quantity);
                // Update the table with the new quantity value
                view.getContext().getContentResolver().update(idToChange, values, null, null);
            }
        });
    }
}
