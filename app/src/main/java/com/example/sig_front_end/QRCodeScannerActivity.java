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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QRCodeScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    private String list;
    private ArrayList<String> nomsSalles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getListQRCode();
        chargerNomsSalleCsv();
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
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
        mScannerView.startCamera();
    }

    private void getListQRCode(){
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

    private void chargerNomsSalleCsv(){
        nomsSalles = new ArrayList<>();
        InputStream is = getResources().openRawResource(R.raw.rdc);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String readLine = null;
        try {
            br.readLine(); //la ligne 1 contient le nom des colonnes
            while ((readLine = br.readLine()) != null) {
                List<String> tokens = new ArrayList<>();
                StringTokenizer tokenizer = new StringTokenizer(readLine, ",");
                while (tokenizer.hasMoreElements()) {
                    tokens.add(tokenizer.nextToken());
                }
                String nameSalle = tokens.get(3);
                if(!nomsSalles.contains(nameSalle)){
                    nomsSalles.add(nameSalle);
                }
            }
            is.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        is = getResources().openRawResource(R.raw.etage);
        br = new BufferedReader(new InputStreamReader(is));
        readLine = null;
        try {
            br.readLine(); //la ligne 1 contient le nom des colonnes
            while ((readLine = br.readLine()) != null) {
                List<String> tokens = new ArrayList<>();
                StringTokenizer tokenizer = new StringTokenizer(readLine, ",");
                while (tokenizer.hasMoreElements()) {
                    tokens.add(tokenizer.nextToken());
                }
                String nameSalle = tokens.get(3);
                if(!nomsSalles.contains(nameSalle)){
                    nomsSalles.add(nameSalle);
                }
            }
            is.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void exist(String text){
        if(nomsSalles.contains(text)){
            makeText(text);
            Intent i = new Intent(QRCodeScannerActivity.this,MapActivity.class);
            i.putExtra("position",text);
            startActivity(i);
        }else{
            makeText(text + " : Bad QRCode format !");
        }
    }

    private void makeText(String text){
        Toast.makeText(this,text,Toast.LENGTH_LONG).show();
    }
}