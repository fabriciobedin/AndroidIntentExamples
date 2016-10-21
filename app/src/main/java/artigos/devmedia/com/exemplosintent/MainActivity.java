package artigos.devmedia.com.exemplosintent;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private ImageView imgResult;

    private final int REQUEST_CODE_PHOTO = 9;
    private final int REQUEST_DATA_CONTACT = 10;
    private final int REQUEST_QR_CODE = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgResult = (ImageView) findViewById(R.id.result);
    }

    public void webBrowser(View view) {

        Intent intent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://www.globo.com"));
        startActivity(intent);


    }

    public void share(View view) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Usando a Intent de Share");

        startActivity(sharingIntent);

    }

    public void photo(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_PHOTO);
    }

    public void call(View view) {
        Intent intent = new Intent(Intent.ACTION_CALL,
                Uri.parse("tel:(+49)12345789"));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }

    public void dial(View view) {
        Intent intent = new Intent(Intent.ACTION_DIAL,
                Uri.parse("tel:(+49)12345789"));
        startActivity(intent);
    }

    public void viewMap(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("geo:50.123,7.1434?z=19"));
        startActivity(intent);
    }

    public void searchMap(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q=query"));
        startActivity(intent);
    }

    public void showPeople(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("content://contacts/people/"));
        startActivity(intent);
    }

    public void showContact(View view) {
        Intent intent = new Intent(Intent.ACTION_EDIT,
                Uri.parse("content://contacts/people/1"));
        startActivity(intent);
    }

    public void calendario(View view) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("title", "Some title");
        intent.putExtra("description", "Some description");
        intent.putExtra("beginTime", System.currentTimeMillis());
        intent.putExtra("endTime", System.currentTimeMillis() + 100000);
        startActivity(intent);
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    public void abreCamera(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void dadoContato(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, REQUEST_DATA_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        InputStream stream = null;
        if (requestCode == REQUEST_CODE_PHOTO && resultCode == RESULT_OK) {
            try {
                //if (bitmap != null) {
                //    bitmap.recycle();
                // }
                Uri uriOrigin = data.getData();
                Log.e("PATH", "uri: " + uriOrigin.getPath());
                stream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(stream);

                imgResult.setImageBitmap(bitmap);

                String type = "image/*";
                String filename = "/myPhoto.jpg";
                String mediaPath = Environment.getExternalStorageDirectory() + filename;

                // Create the new Intent using the 'Send' action.
                Intent share = new Intent(Intent.ACTION_SEND);

                // Set the MIME type
                share.setType(type);

                // Create the URI from the media
                File media = new File(mediaPath);
                Uri uri = Uri.fromFile(media);

                // Add the URI to the Intent.
                share.putExtra(Intent.EXTRA_STREAM, uri);

                // Broadcast the Intent.
                startActivity(Intent.createChooser(share, "Share to"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (stream != null)
                    try {
                        stream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        } else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imgResult.setImageBitmap(imageBitmap);
        } else if (requestCode == REQUEST_DATA_CONTACT && resultCode == RESULT_OK) {
            Uri contactData = data.getData();
            Cursor c =  managedQuery(contactData, null, null, null, null);
            if (c.moveToFirst()) {
                String name = c.getString(c.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
                Log.e("INTENTS", "Name: " + name);
            }
        } else if (requestCode == REQUEST_QR_CODE && resultCode == RESULT_OK) {
            String contents = data.getStringExtra("SCAN_RESULT");
        }
    }

    public void qrcode(View view) {
        try {

            Intent intent = new Intent("com.google.zxing.client.android.SCAN");
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE"); // "PRODUCT_MODE for bar codes

            startActivityForResult(intent, REQUEST_QR_CODE);

        } catch (Exception e) {

            //Uri marketUri = Uri.parse("market://details?id=com.google.zxing.client.android");
            Uri marketUri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.zxing.client.android");
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
            startActivity(marketIntent);

        }
    }
}

