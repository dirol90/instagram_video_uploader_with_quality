package com.insta.videouploader.web_view;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.webkit.JavascriptInterface;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import com.insta.videouploader.MainActivity;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class WebAppInterface {
    private static final int REQUEST_EXTERNAL_STORAGE = 991;
    private Context mContext;
    String[] PERMISSIONS_ALL = {
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    };
    public WebAppInterface(Context c) {
        mContext = c;
    }

    @JavascriptInterface
    public void runGalery() {
        getPermitions();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Create new file or get from gallery?");
        builder.setMessage("Do you like to create new file (video/photo) or get existing file from gallery?")
                .setPositiveButton("Get From Gallery", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.setType("*/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        ((Activity) mContext).startActivityForResult(Intent.createChooser(intent, "Select File"), 1234);
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Create new Video", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        openCameraIntent();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Create new Photo", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        openPhotoCameraIntent();
                        dialog.dismiss();
                    }
                });

        builder.create().show();

    }

    @JavascriptInterface
    public void downloadResource(String i) {
        getPermitions();

        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("URL", i);
        clipboard.setPrimaryClip(clip);
    }

    @JavascriptInterface
    public void openCamera() {
        getPermitions();

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Create new file or get from gallery?");
        builder.setMessage("Do you like to create new file (video/photo) or get existing file from gallery?")
                .setPositiveButton("Get From Gallery", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent();
                        intent.setType("*/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        ((Activity) mContext).startActivityForResult(Intent.createChooser(intent, "Select File"), 1234);
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("Create new Video", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        openCameraIntent();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Create new Photo", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        openPhotoCameraIntent();
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    private static final int REQUEST_CAPTURE_IMAGE = 100;

    private void openCameraIntent() {
        getPermitions();

        Intent pictureIntent = new Intent(
                MediaStore.ACTION_VIDEO_CAPTURE
        );

        if (pictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mContext, "com.insta.videouploader.fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                ((Activity) mContext).startActivityForResult(pictureIntent,
                        REQUEST_CAPTURE_IMAGE);
            }
        }
    }

    private void openPhotoCameraIntent() {
        getPermitions();

        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE
        );

        if (pictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createPhotoFile();
            } catch (IOException ex) {
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(mContext, "com.insta.videouploader.fileprovider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                ((Activity) mContext).startActivityForResult(pictureIntent,
                        REQUEST_CAPTURE_IMAGE);
            }
        }
    }

    private File createPhotoFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir =
                new  File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/InstaVideoUploader/");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        MainActivity.capturedPhotoFilePath = Uri.parse(image.getAbsolutePath());
        return image;
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "VID_" + timeStamp + "_";
        File storageDir =
               new  File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/InstaVideoUploader/");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".mp4",         /* suffix */
                storageDir      /* directory */
        );

        MainActivity.capturedImageFilePath = Uri.parse(image.getAbsolutePath());
        return image;
    }

    private void getPermitions(){
        int permission =
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    ((Activity) mContext),
                    PERMISSIONS_ALL,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        permission =
                ActivityCompat.checkSelfPermission(mContext, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    ((Activity) mContext),
                    PERMISSIONS_ALL,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        permission = ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    ((Activity) mContext),
                    PERMISSIONS_ALL,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
