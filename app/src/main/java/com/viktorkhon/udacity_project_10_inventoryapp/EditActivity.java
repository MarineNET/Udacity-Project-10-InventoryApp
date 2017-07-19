package com.viktorkhon.udacity_project_10_inventoryapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.viktorkhon.udacity_project_10_inventoryapp.Data.InventoryContract.InventoryEntry;

import com.viktorkhon.udacity_project_10_inventoryapp.Data.InventoryContract;
import com.viktorkhon.udacity_project_10_inventoryapp.Data.InventoryDbHelper;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Viktor Khon on 7/17/2017.
 */

public class EditActivity extends AppCompatActivity{

    private InventoryDbHelper mDbHelper;

    private static final int READ_REQUEST_CODE = 42;

    TextView addImage;
    EditText itemName;
    EditText price;
    TextView quantity;
    Button decrease;
    Button increase;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        addImage = (TextView) findViewById(R.id.add_image);
        itemName = (EditText) findViewById(R.id.et_item_name);
        price = (EditText) findViewById(R.id.et_price);
        quantity = (TextView) findViewById(R.id.et_quantity);
        decrease = (Button) findViewById(R.id.bn_decrease_by_1);
        increase = (Button) findViewById(R.id.bn_increase_by_1);
        mImageView = (ImageView) findViewById(R.id.imageView);
        mDbHelper = new InventoryDbHelper(this);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
                    // browser.
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

                    // Filter to only show results that can be "opened", such as a
                    // file (as opposed to a list of contacts or timezones)
                    intent.addCategory(Intent.CATEGORY_OPENABLE);

                    // Filter to show only images, using the image MIME data type.
                    intent.setType("image/*");

                    startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });

        // Get the value of 'quantity' and convert it to Integer
        // Decrease this value by 1 each time a button is pressed
        // Make sure that the quantity doesn't go below '0'
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.parseInt(quantity.getText().toString());
                qty--;
                displayQty(qty);
                if (qty < 0) {
                    displayQty(0);
                }
            }
        });

        // Get the value of 'quantity' and convert it to Integer
        // Increase this value by 1 each time a button is pressed
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.parseInt(quantity.getText().toString());
                qty++;
                displayQty(qty);
            }
        });
    }

    // Displays text to the Qty TextView
    public void displayQty(int qty) {
        quantity.setText(String.valueOf(qty));
    }

    public void insertItem() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String name = itemName.getText().toString().trim();
        int priceInt = Integer.parseInt(price.getText().toString().trim());
        int qtyInt = Integer.parseInt(quantity.getText().toString().trim());

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_NAME, name);
        values.put(InventoryEntry.COLUMN_PRICE, priceInt);
        values.put(InventoryEntry.COLUMN_QTY, qtyInt);

        long newId = db.insert(InventoryEntry.TABLE_NAME, null, values);

        if (newId == -1) {
            Toast.makeText(this, "Error with saving pet", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Pet saved with id: " + newId, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri;
            InputStream image = null;
            if (resultData != null) {
                uri = resultData.getData();
                try {
                    image = getContentResolver().openInputStream(uri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                Bitmap selectedImage = BitmapFactory.decodeStream(image);
                mImageView.setImageBitmap(selectedImage);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                insertItem();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // do nothing
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(EditActivity.this);
                return true;
            default:
                return true;
        }
    }
}
