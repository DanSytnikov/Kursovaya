package com.example.student.weatherapi;

import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static java.net.Proxy.Type.HTTP;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void onClick(View view){
        ThreadWeather threadWeather = new ThreadWeather();
        threadWeather.start();


    }

    public class ThreadWeather extends Thread {
        @Override
        public void run() {
            Log.e(TAG, "I'm alive");
            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?q=Odessa&appid=a7c76f80c9580699351007ff55f2e86d");
                HttpURLConnection huc = (HttpURLConnection)url.openConnection();
                BufferedReader br = new BufferedReader(new InputStreamReader(huc.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine())!=null){
                    sb.append(line);
                    Log.e(TAG, sb.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "I WANT2DIE");
            }
        }
    }
}
