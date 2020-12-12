package com.example.sig_front_end;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class MapActivity extends AppCompatActivity {
    private final int ZOOM_MIN = 150000;

    private int width;
    private int height;

    private float xCenter;
    private float yCenter;

    private float translationX = 0;
    private float translationY = 0;

    private int zoom = ZOOM_MIN;

    private TextView textViewEtage;
    private ImageView imageView;
    private Button buttonListeSalles;
    private Button buttonChangerEtage;

    private SeekBar zoomBar;
    private Button buttonUp;
    private Button buttonDown;
    private Button buttonRight;
    private Button buttonLeft;

    private Map map;
    private Etage currentEtage;
    private Salle currentSalle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        textViewEtage = (TextView) findViewById(R.id.textview_etage);
        imageView = (ImageView) findViewById(R.id.image_view_map);
        buttonListeSalles = (Button) findViewById(R.id.button_liste_salles);
        buttonChangerEtage = (Button) findViewById(R.id.button_changer_etage);

        zoomBar = (SeekBar) findViewById(R.id.zoom_bar);
        buttonDown = (Button) findViewById(R.id.button_down);
        buttonUp = (Button) findViewById(R.id.button_up);
        buttonRight = (Button) findViewById(R.id.button_right);
        buttonLeft = (Button) findViewById(R.id.button_left);

        //Calcul des positions des elements en fonction de la taille de l'ecran
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        width = metrics.widthPixels;
        height = 3 * metrics.heightPixels / 5;

        //Calcul du centre de la view d'affichage de la map
        xCenter = metrics.heightPixels / 2F;
        yCenter = metrics.widthPixels / 2F;

        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.RGB_565);

        chargerMap();
        currentEtage = map.etages.get(0);

        //test
        currentSalle = currentEtage.salles.get(0);

        zoomBar.setMax(300000);
        zoomBar.setProgress(500);

        zoomBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            // When Progress value changed.
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
                zoom = ZOOM_MIN + progress;
                afficherEtage(currentEtage,bitmap);
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        buttonUp.setOnTouchListener(new View.OnTouchListener() {
            long lastPressUp;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    lastPressUp = System.currentTimeMillis();
                }
                translationY += (System.currentTimeMillis() - lastPressUp) / 60;
                afficherEtage(currentEtage,bitmap);
                return true;
            }
        });

        buttonDown.setOnTouchListener(new View.OnTouchListener() {
            long lastPressUp;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    lastPressUp = System.currentTimeMillis();
                }
                translationY -= (System.currentTimeMillis() - lastPressUp) / 60;
                afficherEtage(currentEtage,bitmap);
                return true;
            }
        });

        buttonRight.setOnTouchListener(new View.OnTouchListener() {
            long lastPressUp;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    lastPressUp = System.currentTimeMillis();
                }
                translationX -= (System.currentTimeMillis() - lastPressUp) / 60;
                afficherEtage(currentEtage,bitmap);
                return true;
            }
        });

        buttonLeft.setOnTouchListener(new View.OnTouchListener() {
            long lastPressUp;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    lastPressUp = System.currentTimeMillis();
                }
                translationX += (System.currentTimeMillis() - lastPressUp) / 60;
                afficherEtage(currentEtage,bitmap);
                return true;
            }
        });

        buttonChangerEtage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentEtage = map.etages.get((map.etages.indexOf(currentEtage)+1) % map.etages.size());
                afficherEtage(currentEtage,bitmap);
            }
        });

        buttonListeSalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
            }
        });

        afficherEtage(currentEtage,bitmap);
    }

    private void chargerMap() {
        //inititalisation de la map
        map = new Map();
        map.etages.add(new Etage(R.raw.rdc,"RDC"));
        map.etages.add(new Etage(R.raw.etage,"etage"));
        map.chargerQrCoord(R.raw.qrcodes);
    }

    private void afficherEtage(Etage etage, Bitmap bitmap){
        Paint paintRed = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintRed.setColor(Color.RED);
        Paint paintGray = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintGray.setColor(Color.GRAY);
        paintGray.setTextSize(28);
        paintGray.setStyle(Paint.Style.FILL);
        Paint paintBlue = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintBlue.setColor(Color.BLUE);
        paintBlue.setTextSize(28);
        textViewEtage.setText(etage.name);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.WHITE);
        for (Salle salle : etage.salles){
            if(salle.qrCode.idPoint != Point.NO_QR_FOUND){
                canvas.drawCircle(salle.qrCode.getNormPoint().x+xCenter/2+translationX, salle.qrCode.getNormPoint().y+yCenter/2+translationY, 5F, paintGray);
            }
            Point centre = salle.getCentre();
            if(!centre.equals(null)) {
                if (currentSalle.equals(salle)) {
                    canvas.drawText(salle.name, centre.getNormPoint().x + xCenter / 2 + translationX, centre.getNormPoint().y + yCenter / 2 + translationY, paintBlue);
                } else {
                    canvas.drawText(salle.name, centre.getNormPoint().x + xCenter / 2 + translationX, centre.getNormPoint().y + yCenter / 2 + translationY, paintGray);
                }
            }
            for(int i = 0 ; i < salle.points.size() - 1 ; i++){
                if(currentSalle.equals(salle)){
                    canvas.drawLine(salle.points.get(i).getNormPoint().x+xCenter/2+translationX, salle.points.get(i).getNormPoint().y+yCenter/2+translationY, salle.points.get(i+1).getNormPoint().x+xCenter/2+translationX, salle.points.get(i+1).getNormPoint().y+yCenter/2+translationY, paintBlue);
                } else {
                    canvas.drawLine(salle.points.get(i).getNormPoint().x+xCenter/2+translationX, salle.points.get(i).getNormPoint().y+yCenter/2+translationY, salle.points.get(i+1).getNormPoint().x+xCenter/2+translationX, salle.points.get(i+1).getNormPoint().y+yCenter/2+translationY, paintRed);
                }
            }
            if(currentSalle.equals(salle)){
                canvas.drawLine(salle.points.get(salle.points.size()-1).getNormPoint().x+xCenter/2+translationX, salle.points.get(salle.points.size()-1).getNormPoint().y+yCenter/2+translationY, salle.points.get(0).getNormPoint().x+xCenter/2+translationX, salle.points.get(0).getNormPoint().y+yCenter/2+translationY, paintBlue);
            } else {
                canvas.drawLine(salle.points.get(salle.points.size()-1).getNormPoint().x+xCenter/2+translationX, salle.points.get(salle.points.size()-1).getNormPoint().y+yCenter/2+translationY, salle.points.get(0).getNormPoint().x+xCenter/2+translationX, salle.points.get(0).getNormPoint().y+yCenter/2+translationY, paintRed);
            }
        }
        imageView.setImageBitmap(bitmap);
    }

    private class Point {
        float x;
        float y;
        int idPoint;
        static final int NO_QR_FOUND = 45;

        public Point(float x, float y){
            this.x = x;
            this.y = y;
        }

        public Point(float x, float y, String idStr){
            this(x,y);
            StringTokenizer tokenizer = new StringTokenizer(idStr, "\"");
            this.idPoint = Integer.parseInt(tokenizer.nextToken());
        }

        public Point(float x, float y, int id){
            this(x,y);
            this.idPoint = id;
        }

        public Point getNormPoint(){
            float xNorm = (x + 77.0364895F) * zoom;
            float yNorm = (-y + 38.89837575F) * zoom;
            return new Point(xNorm,yNorm,idPoint);
        }
    }

    private class Salle {
        String name;
        int idSalle;
        ArrayList<Point> points;
        Point qrCode;
        public Salle(String name, String idStr){
            this.name = name;
            StringTokenizer tokenizer = new StringTokenizer(idStr, "\"");
            this.idSalle = Integer.parseInt(tokenizer.nextToken());
            this.points = new ArrayList<>();
        }

        //On utilise ici la moyenne des coordonnées des points d'une salle pour estimer son centre.
        Point getCentre(){
            if (points.size() < 2)
                return null;
            float xCentre = 0;
            float yCentre = 0;
            for(Point p : points){
                xCentre += p.x;
                yCentre += p.y;
            }
            xCentre /= points.size();
            yCentre /= points.size();
            return new Point(xCentre,yCentre);
        }
    }

    private class Etage {
        String name;
        ArrayList<Salle> salles;

        public Etage(String name){
            this.name = name;
            salles = new ArrayList<>();
        }

        public Etage(int csvId, String name){
            this(name);

            InputStream is = getResources().openRawResource(csvId);
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
                    float x = Float.parseFloat(tokens.get(0).substring(0,10));
                    float y = Float.parseFloat(tokens.get(1).substring(0,9));
                    String strIdSalle = tokens.get(2);
                    StringTokenizer tokenizer2 = new StringTokenizer(strIdSalle, "\"");
                    int idSalle = Integer.parseInt(tokenizer2.nextToken());
                    String nameSalle = tokens.get(3);
                    String strIdPoint = tokens.get(4);
                    Point p = new Point(x,y,strIdPoint);
                    boolean salleExistante = false;
                    for(Salle s : salles){
                        if(s.idSalle == idSalle){
                            salleExistante = true;
                            break;
                        }
                    }
                    if(!salleExistante){
                        salles.add(new Salle(nameSalle,strIdSalle));
                    }
                    for(Salle s : salles){
                        if(s.idSalle == idSalle && !s.points.contains(p)) s.points.add(p);
                    }
                }
                is.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (Salle s : salles){
                s.qrCode = new Point(0,0,Point.NO_QR_FOUND);
            }
        }
    }

    private class Map {
        ArrayList<Etage> etages;
        public Map(){
            etages = new ArrayList<>();
        }

        public void chargerQrCoord(int csvId){

            InputStream is = getResources().openRawResource(csvId);
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
                    float x = Float.parseFloat(tokens.get(0).substring(0,10));
                    float y = Float.parseFloat(tokens.get(1).substring(0,9));
                    String idSommetStr = tokens.get(2);
                    String nomSalle = tokens.get(3);
                    StringTokenizer tokenizer2 = new StringTokenizer(idSommetStr, "\"");
                    int idSommet = Integer.parseInt(tokenizer2.nextToken());
                    Point p = new Point(x,y,idSommet);
                    for(Etage e : etages){
                        if(!e.salles.equals(null))
                            for(Salle s : e.salles){
                                if(s.name.equals(nomSalle)){
                                    s.qrCode = p;
                                }
                            }
                    }
                }
                is.close();
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}