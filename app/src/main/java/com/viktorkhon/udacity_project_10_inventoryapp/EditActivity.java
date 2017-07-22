package com.viktorkhon.udacity_project_10_inventoryapp;

import android.app.LoaderManager;
import android.app.Activity;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.viktorkhon.udacity_project_10_inventoryapp.Data.InventoryContract.InventoryEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Viktor Khon on 7/17/2017.
 */

public class EditActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int READ_REQUEST_CODE = 42;

    private static final int INV_LOADER = 1;

    public static final String LOG_TAG = EditActivity.class.getSimpleName();

    TextView addImage;
    EditText nameEditText;
    EditText priceEditText;
    TextView quantityTextView;
    Button decrease;
    Button increase;
    ImageView mImageView;

    Uri currentItemUri;

    Uri imageUri;

    private boolean itemHasChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        addImage = (TextView) findViewById(R.id.add_image);
        nameEditText = (EditText) findViewById(R.id.et_item_name);
        priceEditText = (EditText) findViewById(R.id.et_price);
        quantityTextView = (TextView) findViewById(R.id.et_quantity);
        decrease = (Button) findViewById(R.id.bn_decrease_by_1);
        increase = (Button) findViewById(R.id.bn_increase_by_1);
        mImageView = (ImageView) findViewById(R.id.imageView);

        nameEditText.setOnTouchListener(mOnTouchListener);
        priceEditText.setOnTouchListener(mOnTouchListener);

        Intent intent = getIntent();
        currentItemUri = intent.getData();

        if (currentItemUri == null) {
            setTitle("Add new item");
        } else {
            setTitle("Edit item");
            getLoaderManager().initLoader(INV_LOADER, null, this);
        }

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
                    // browser.
                    Intent intent;

                    if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    }

                    // Filter to show only images, using the image MIME data type.
                    intent.setType("image/*");

                    startActivityForResult(intent, READ_REQUEST_CODE);
            }
        });

        // Get the value of 'quantityTextView' and convert it to Integer
        // Decrease this value by 1 each time a button is pressed
        // Make sure that the quantityTextView doesn't go below '0'
        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.parseInt(quantityTextView.getText().toString());
                qty--;
                displayQty(qty);
                if (qty < 0) {
                    displayQty(0);
                }
            }
        });

        // Get the value of 'quantityTextView' and convert it to Integer
        // Increase this value by 1 each time a button is pressed
        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.parseInt(quantityTextView.getText().toString());
                qty++;
                displayQty(qty);
            }
        });
    }

    // Displays text to the Qty TextView
    public void displayQty(int qty) {
        quantityTextView.setText(String.valueOf(qty));
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
            if (resultData != null) {
                imageUri = resultData.getData();
                mImageView.setImageBitmap(getBitmapFromUri(imageUri));
            }
        }
    }

    private int confirmPrice() {
        if (priceEditText.getText().toString().length() > 0) {
            return Integer.parseInt(priceEditText.getText().toString().trim());
        }
        return 0;
    }

    public void insertItem() {

        String nameString = nameEditText.getText().toString().trim();
        int priceInt = confirmPrice();
        int qtyInt = Integer.parseInt(quantityTextView.getText().toString().trim());

        String imageString = null;
        if (imageUri != null) {
            imageString = imageUri.toString();
        }

        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_NAME, nameString);
        values.put(InventoryEntry.COLUMN_PRICE, priceInt);
        values.put(InventoryEntry.COLUMN_QTY, qtyInt);
        values.put(InventoryEntry.COLUMN_IMAGE, imageString);

        if (currentItemUri == null) {

            // Sanity check: If no entries, return to the Catalog screen
            if (nameString.equals("") && priceInt == 0 && imageUri == null && qtyInt == 0) {
            Toast.makeText(this, "Entry is required", Toast.LENGTH_SHORT).show();
            finish();}

            // Sanity check: All entries are required. If missing, show a Toast message
            if (nameString.equals("") || priceInt == 0 || imageUri == null){
                Toast.makeText(this, "All entries are required", Toast.LENGTH_SHORT).show();
                return;}

            Uri newId = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            if (newId == null) {
                Toast.makeText(this, "Error with saving item", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            // Sanity check: All entries are required. If missing, show a Toast message
            if (nameString.equals("") || priceInt == 0 || TextUtils.isEmpty(imageString)) {
                Toast.makeText(this, "All entries are required", Toast.LENGTH_SHORT).show();
                return;}

            int updatedId = getContentResolver().update(currentItemUri, values, null, null);

            if (updatedId == 0) {
                Toast.makeText(this, "Error with saving item", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
                finish();
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

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryEntry.COLUMN_ID,
                InventoryEntry.COLUMN_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QTY,
                InventoryEntry.COLUMN_IMAGE
        };

        return new CursorLoader(this,
                currentItemUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        if (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_NAME));
            int priceInt = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE));
            int quantityInt = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_QTY));

            String imageString = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_IMAGE));
            imageUri = Uri.parse(imageString);

            nameEditText.setText(name);
            priceEditText.setText(String.valueOf(priceInt));
            quantityTextView.setText(String.valueOf(quantityInt));
            mImageView.setImageBitmap(getBitmapFromUri(imageUri));
         }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        priceEditText.setText("");
        quantityTextView.setText("");
        mImageView.setImageBitmap(null);
    }

    private View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            itemHasChanged = true;
            return false;
        }
    };

    // Code provided by Forum Mentor at Udacity sudhirkhanger
    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = mImageView.getWidth();
        int targetH = mImageView.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            // Get the dimensions of the View
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {
            }
        }
    }
}