package com.viktorkhon.udacity_project_10_inventoryapp.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Viktor Khon on 7/18/2017.
 */

public final class InventoryContract {

    public static final String CONTENT_AUTHORITY = "com.viktorkhon.udacity_project_10_inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_INVENTORY = "inventory";

    public static class InventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        public static final String TABLE_NAME = "inventory";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PRICE = "price";
        public static final String COLUMN_QTY = "quantity";
        public static final String COLUMN_IMAGE = "image";
    }
}
