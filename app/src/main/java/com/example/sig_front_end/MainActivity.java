package com.example.sig_front_end;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

//Splash screen et check des permissions
public class MainActivity extends AppCompatActivity {

    private Button buttonScanner;
    private Button buttonPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonScanner = (Button) findViewById(R.id.button_scanner);
        buttonPermissions = (Button) findViewById(R.id.button_permissions);

        buttonScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
                if(hasCameraPermission == PackageManager.PERMISSION_GRANTED){
                    Intent i = new Intent(MainActivity.this, QRCodeScannerActivity.class);
                    startActivity(i);
                }
                else {
                    makeText("Please enable permissions.");
                }
            }
        });

        buttonPermissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
                if(hasCameraPermission == PackageManager.PERMISSION_GRANTED){
                    makeText("All required permissions are granted.");
                }
                else {
                    askForPermission();
                }
            }
        });
    }

    private void makeText(String text){
        Toast.makeText(this,text,Toast.LENGTH_LONG).show();
    }

    private void askForPermission(){
        final int REQUEST_CODE_ASK_PERMISSIONS = 123;
        requestPermissions(new String[] {Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);
    }

}