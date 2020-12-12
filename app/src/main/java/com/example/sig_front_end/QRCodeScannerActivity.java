package com.example.sig_front_end;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private String list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GetListQRCode();
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);

        Intent i = new Intent(QRCodeScannerActivity.this,MapActivity.class);
        startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        String QrScanned = result.getText();
        exist(QrScanned);
    }

    private void GetListQRCode (){
        RequestQueue requestQueue= Volley.newRequestQueue(getApplicationContext());
        String URL="http://192.168.1.44:8080/qrcode/qrcodeList";

        StringRequest stringRequest =new StringRequest(
                Request.Method.GET,
                URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //makeText("Response : "+ response);
                        list = response;
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

    private void exist(String text){
        boolean bool = list.contains(text);
        if(bool){
            makeText("il y est");
        }else{
            makeText("il y est PAS");
        }
    }

    private void makeText(String text){
        Toast.makeText(this,text,Toast.LENGTH_LONG).show();
    }
}