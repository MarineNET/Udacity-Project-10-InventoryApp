package com.viktorkhon.udacity_project_10_inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.app.Activity;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
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

    // A number given to startActivityForResult(). can be any number
    private static final int READ_REQUEST_CODE = 42;

    // Constant to use with getLoaderManager()
    private static final int INV_LOADER = 1;

    private static final String LOG_TAG = EditActivity.class.getSimpleName();

    // Give global names to each Button and a View in this layout
    Button addImage;
    EditText nameEditText;
    EditText priceEditText;
    TextView quantityTextView;
    Button decrease;
    Button increase;
    Button order;
    ImageView mImageView;

    // Uri received from CatalogActivity
    Uri currentItemUri;

    // Image Uri received from user
    Uri imageUri;

    private boolean itemHasChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        // Initialize each view
        addImage = (Button) findViewById(R.id.add_image);
        nameEditText = (EditText) findViewById(R.id.et_item_name);
        priceEditText = (EditText) findViewById(R.id.et_price);
        quantityTextView = (TextView) findViewById(R.id.et_quantity);
        decrease = (Button) findViewById(R.id.bn_decrease_by_1);
        increase = (Button) findViewById(R.id.bn_increase_by_1);
        order = (Button) findViewById(R.id.bn_order);
        mImageView = (ImageView) findViewById(R.id.imageView);

        // Attach listeners to each View that we want to monitor for change to display a message
        nameEditText.setOnTouchListener(mOnTouchListener);
        priceEditText.setOnTouchListener(mOnTouchListener);
        mImageView.setOnTouchListener(mOnTouchListener);
        quantityTextView.setOnTouchListener(mOnTouchListener);

        // Get intent and Uri for an item that was clicked on ListView from CatalogActivity
        Intent intent = getIntent();
        currentItemUri = intent.getData();

        // If there is no Uri being passed, then set a name for this Activity as "Add new item"
        // Otherwise it is an existent item, so change the name of the Activity to "Edit item"
        if (currentItemUri == null) {
            setTitle("Add new item");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit item");
            // Start Loader Manager
            getLoaderManager().initLoader(INV_LOADER, null, this);
        }

        // Add a click listener to the Button. Upon click, start an intent and open a document
        // That only contains images
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

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Order more product");
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });
    }

    // Displays text to the Qty TextView
    private void displayQty(int qty) {
        quantityTextView.setText(String.valueOf(qty));
    }

    // Code provided by Forum Mentor at Udacity sudhirkhanger
    // Create a new method that takes in a Uri and returns a bitmap
    // This is done in order to help upload images, that are larger than allowed by ImageView
    private Bitmap getBitmapFromUri(Uri uri) {

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

    // Once a user inputs an image, use onActivityResult method to tell the app what to do with it
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
                // Set the image to ImageView as Bitmap using getBitmapFromUri() method
                mImageView.setImageBitmap(getBitmapFromUri(imageUri));
            }
        }
    }

    // Check if the priceEditText field is empty. If so, return 0 as default
    // Otherwise use this number
    private int confirmPrice() {
        if (priceEditText.getText().toString().length() > 0) {
            return Integer.parseInt(priceEditText.getText().toString().trim());
        }
        return 0;
    }

    // Method that defines how an item will be saved, based on whether its a new or existed item
    private void saveItem() {

        // Get appropriate values from UI and store them in a variable
        String nameString = nameEditText.getText().toString().trim();
        int priceInt = confirmPrice();
        int qtyInt = Integer.parseInt(quantityTextView.getText().toString().trim());

        // Get Uri of an image that a user chooses and store it as a String
        String imageString = null;
        if (imageUri != null) {
            imageString = imageUri.toString();
        }

        // Use ContentValues class and store each variable in appropriate table. Image is stored
        // as a String
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_NAME, nameString);
        values.put(InventoryEntry.COLUMN_PRICE, priceInt);
        values.put(InventoryEntry.COLUMN_QTY, qtyInt);
        values.put(InventoryEntry.COLUMN_IMAGE, imageString);

        // If a new item is being entered use this method
        if (currentItemUri == null) {

            // Sanity check: If no entries, return to the Catalog screen
            if (nameString.equals("") && priceInt == 0 && imageUri == null && qtyInt == 0) {
            Toast.makeText(this, "Entry is required", Toast.LENGTH_SHORT).show();
            finish();}

            // Sanity check: All entries are required. If missing, show a Toast message
            if (nameString.equals("") || priceInt == 0 || imageUri == null){
                Toast.makeText(this, "All entries are required", Toast.LENGTH_SHORT).show();
                return;}

            // New Uri ID for an item that is being saved
            Uri newId = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            if (newId == null) {
                Toast.makeText(this, "Error with saving item", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item saved", Toast.LENGTH_SHORT).show();
                finish();
            }
            // If an existing item being saved, use this method
        } else {
            // Sanity check: All entries are required. If missing, show a Toast message
            if (nameString.equals("") || priceInt == 0 || TextUtils.isEmpty(imageString)) {
                Toast.makeText(this, "All entries are required", Toast.LENGTH_SHORT).show();
                return;}

            // After update is successful, get the number of rows as int that have been updated
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveItem();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the item hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!itemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
            default:
                return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_edit.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new item, hide the "Delete" menu item.
        if (currentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    // Create Loader method that performs Async functions to query database
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                InventoryEntry.COLUMN_ID,
                InventoryEntry.COLUMN_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QTY,
                InventoryEntry.COLUMN_IMAGE
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                currentItemUri,
                projection,
                null,
                null,
                null);
    }

    // Used to populate Views once a user clicks on an item from the ListView
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToNext()) {
            String name = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_NAME));
            int priceInt = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_PRICE));
            int quantityInt = cursor.getInt(cursor.getColumnIndex(InventoryEntry.COLUMN_QTY));

            // Get a String of an image from the database and convert it back to Uri
            // Assign Uri value to the global value 'imageUri' that other methods can now use
            String imageString = cursor.getString(cursor.getColumnIndex(InventoryEntry.COLUMN_IMAGE));
            imageUri = Uri.parse(imageString);

            // Set appropriate values to each view and display them back
            nameEditText.setText(name);
            priceEditText.setText(String.valueOf(priceInt));
            quantityTextView.setText(String.valueOf(quantityInt));
            mImageView.setImageBitmap(getBitmapFromUri(imageUri));
         }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
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

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        // If the item hasn't changed, continue with handling back button press
        if (!itemHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                deleteItem();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the item.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the item in the database.
     */
    private void deleteItem() {
        if (currentItemUri != null) {
            int rowsDeleted = getContentResolver().delete(currentItemUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_item_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_item_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Close the activity
        finish();
    }
}