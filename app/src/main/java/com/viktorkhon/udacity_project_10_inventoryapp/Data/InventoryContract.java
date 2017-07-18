package com.viktorkhon.udacity_project_10_inventoryapp.Data;

import android.provider.BaseColumns;

/**
 * Created by Viktor Khon on 7/18/2017.
 */

public final class InventoryContract {


    public class InventoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "inventory";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_IMAGE = "image";
    }
}
