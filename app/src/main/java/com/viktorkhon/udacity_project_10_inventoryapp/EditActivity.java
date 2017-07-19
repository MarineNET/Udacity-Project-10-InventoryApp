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

import com.viktorkhon.udacity_project_10_inventoryapp.Data.InventoryContract.InventoryEntry;

import com.viktorkhon.udacity_project_10_inventoryapp.Data.InventoryContract;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Viktor Khon on 7/17/2017.
 */

public class EditActivity extends AppCompatActivity{

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

        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.parseInt(quantity.getText().toString());
                qty++;
                displayQty(qty);
            }
        });
    }

    public void displayQty(int qty) {
        quantity.setText(String.valueOf(qty));
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
                // do nothing
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
