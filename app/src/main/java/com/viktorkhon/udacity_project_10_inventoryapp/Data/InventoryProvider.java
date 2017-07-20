package com.viktorkhon.udacity_project_10_inventoryapp.Data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.viktorkhon.udacity_project_10_inventoryapp.Data.InventoryContract;
import com.viktorkhon.udacity_project_10_inventoryapp.Data.InventoryContract.InventoryEntry;

import java.io.IOException;

/**
 * Created by Viktor Khon on 7/19/2017.
 */

public class InventoryProvider extends ContentProvider {

    private InventoryDbHelper mDbHelper;

    public static final int INVENTORY = 1;

    public static final int INVENTORY_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                InventoryContract.PATH_INVENTORY, INVENTORY);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,
                InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case INVENTORY_ID:
                // This translates to SQLlite as "WHERE _ID = ?", in this case '?' will be
                // substituted by an appropriate ID number
                selection = InventoryEntry.COLUMN_ID + " =?";
                // Convert the last part of Uri segment to a 'long' and then convert it to a String
                // and store it as a String[]
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertItem(Uri uri, ContentValues contentValues) {

        String name = contentValues.getAsString(InventoryEntry.COLUMN_NAME);
        if (name.equals("")) {
            throw new IllegalArgumentException("Please enter an item name");
        }

        Integer price = contentValues.getAsInteger(InventoryEntry.COLUMN_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Please enter a price");
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        long id = db.insert(InventoryEntry.TABLE_NAME, null, contentValues);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateInventory(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateInventory(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateInventory(Uri uri, ContentValues contentValues,
                                String selection, String[] selectionArgs) {

        if (contentValues.containsKey(InventoryEntry.COLUMN_NAME)) {
            String name = contentValues.getAsString(InventoryEntry.COLUMN_NAME);
            if (name.equals("")) {
                throw new IllegalArgumentException("Please enter an item name");
            }
        }

        if (contentValues.containsKey(InventoryEntry.COLUMN_PRICE)) {
            Integer price = contentValues.getAsInteger(InventoryEntry.COLUMN_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Please enter a price");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (contentValues.size() == 0) {
            return 0;
        }

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        int newID = db.update(InventoryEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        return newID;
    }
}
