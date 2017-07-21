package com.viktorkhon.udacity_project_10_inventoryapp.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.viktorkhon.udacity_project_10_inventoryapp.Data.InventoryContract.InventoryEntry;

/**
 * Created by Viktor Khon on 7/18/2017.
 */

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Inventory.db";

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_ENTRIES  =
                "CREATE TABLE " + InventoryEntry.TABLE_NAME + " (" +
                        InventoryEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        InventoryEntry.COLUMN_NAME + " TEXT NOT NULL," +
                        InventoryEntry.COLUMN_PRICE + " INTEGER DEFAULT 0," +
                        InventoryEntry.COLUMN_QTY + " INTEGER," +
                        InventoryEntry.COLUMN_IMAGE + " TEXT)";
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + InventoryEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
