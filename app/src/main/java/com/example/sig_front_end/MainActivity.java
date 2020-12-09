package com.example.sig_front_end;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

//Splash screen et check des permissions
public class MainActivity extends AppCompatActivity {

    private Button buttonScanner;
    private Button buttonPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetListRDC();

        buttonScanner = (Button) findViewById(R.id.button_scanner);
        buttonPermissions = (Button) findViewById(R.id.button_permissions);

        buttonScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA);
                int hasInternetPermission = checkSelfPermission(Manifest.permission.INTERNET);

                if(hasCameraPermission == PackageManager.PERMISSION_GRANTED){
                    if(hasInternetPermission == PackageManager.PERMISSION_GRANTED) {
                        Intent i = new Intent(MainActivity.this, QRCodeScannerActivity.class);
                        startActivity(i);
                    }
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
                int hasInternetPermission = checkSelfPermission(Manifest.permission.INTERNET);
                if(hasCameraPermission == PackageManager.PERMISSION_GRANTED){
                    if(hasInternetPermission == PackageManager.PERMISSION_GRANTED) {
                        //makeText("All required permissions are granted.");
                    }
                } else {
                    askForPermission();
                }
            }
        });
    }

    private void GetListRDC (){
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        String URL="http://192.168.1.44:8080/rdc/rdcList";

        StringRequest stringRequest =new StringRequest(
                Request.Method.GET,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        makeText("Response : "+ response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Err Response",error.toString());
                    }
                }
        );
        requestQueue.add(stringRequest);
    }

    private void Post (){
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        String URL="http://192.168.1.44:8080/request";

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );
        requestQueue.add(stringRequest);
    }

    private void makeText(String text){
        Toast.makeText(this,text,Toast.LENGTH_LONG).show();
    }

    private void askForPermission(){
        final int REQUEST_CODE_ASK_PERMISSIONS = 123;
        //final int REQUEST_CODE_ASK_PERMISSIONS2 = 124;
        requestPermissions(new String[] {Manifest.permission.CAMERA}, REQUEST_CODE_ASK_PERMISSIONS);
        //requestPermissions(new String[] {Manifest.permission.INTERNET}, REQUEST_CODE_ASK_PERMISSIONS2);
    }

}