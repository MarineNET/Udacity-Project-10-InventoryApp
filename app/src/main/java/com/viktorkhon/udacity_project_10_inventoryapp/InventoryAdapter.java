package com.viktorkhon.udacity_project_10_inventoryapp;

import android.content.Context;
import android.database.Cursor;
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

    int quantity;
    TextView qtyView;

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameView = (TextView)view.findViewById(R.id.lv_name);
        qtyView = (TextView)view.findViewById(R.id.lv_quantity);
        TextView priceView = (TextView)view.findViewById(R.id.lv_price);
        Button saleButton = (Button)view.findViewById(R.id.lv_button);

        String name = cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_NAME));
        quantity = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_QTY));
        int price = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRICE));

        // At this time only the last row is being changed, no matter where you press this button
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity--;
                qtyView.setText(String.valueOf(quantity));
                if(quantity < 0) {
                    quantity = 5;
                }
            }
        });

        nameView.setText(name);
        qtyView.setText(String.valueOf(quantity));
        priceView.setText(String.valueOf(price));
    }
}
