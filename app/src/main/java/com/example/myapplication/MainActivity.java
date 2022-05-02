package com.example.myapplication;

import static java.lang.Integer.parseInt;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.hardware.camera2.CameraCaptureSession;

import com.example.myapplication.Enumerators.PermissionCode;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private Button captureButton;
    private PermissionCode permissions;
    private boolean cameraPermission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.cameraPermission = false;
        this.imageView = findViewById(R.id.imageView);
        this.captureButton = findViewById(R.id.capture_button);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //permission has not been granted
                if (!checkCameraPermission()) {
                    requestCameraPermission();
                } else {
                    openCamera();
                }
            }
        });
    }

    private ActivityResultLauncher<Intent> requestActivityLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        setImageToView(data);
                    }
                }
            });


    private void requestCameraPermission() {
        Log.println(Log.INFO, "Check", "Permission Enum " + permissions.CAMERA.ordinal());
        System.out.println(permissions.CAMERA.ordinal());
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, permissions.CAMERA.ordinal());

    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private void openCamera() {

        //open camera to get photo intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            //deprecated way
            //startActivityForResult(takePictureIntent, permissions.CAMERA.ordinal());
            //new way to open camera
            requestActivityLauncher.launch(takePictureIntent);
        } catch (ActivityNotFoundException e) {
            // display error state to the user
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //if it camera request
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //check if has granted camera permission
            if (checkCameraPermission()) {
                Log.println(Log.INFO, "Check", "Permission Granted: " + checkCameraPermission());
                openCamera();
            }
        }
    }
    //part of the deprecated way of opening the camera
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == permissions.CAMERA.ordinal()) {
            setImageToView(data);
        }
    }

    private void setImageToView(Intent data) {
        //get Captured Image
        Bitmap captureImage = (Bitmap) data.getExtras().get("data");
        //set Captured Image to ImageView
        this.imageView.setImageBitmap(captureImage);
    }
}