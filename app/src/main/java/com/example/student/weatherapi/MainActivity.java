package com.example.student.weatherapi;

import android.nfc.Tag;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        /*
        ThreadWeather threadWeather = new ThreadWeather();
        threadWeather.start();
*/
        ThreadDownloader threadDownloader = new ThreadDownloader();
        threadDownloader.start();
    }

    /*
    public class ThreadWeather extends Thread {
        @Override
        public void run() {
            Log.e(TAG, "I'm alive");
            try {
                URL url = new URL("https://speed.hetzner.de/100MB.bin");
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
*/
    public class ThreadDownloader extends Thread{
        @Override
        public void run(){
            try{
                URL url = new URL("https://speed.hetzner.de/100MB.bin");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setDoOutput(true);

                urlConnection.connect();

                File SDCardRoot = Environment.getExternalStorageDirectory();
                File file =  new File(SDCardRoot, "100MB.bin");

                FileOutputStream fileOutput = new FileOutputStream(file);
                InputStream inputStream = urlConnection.getInputStream();

                int totalSize = urlConnection.getContentLength();
                int downloadSize = 0;
                byte[] buffer = new byte[1024];
                int bufferLength = 0;

                while ((bufferLength = inputStream.read(buffer)) > 0){
                    fileOutput.write(buffer, 0, bufferLength);
                    downloadSize += bufferLength;
                    //updateProgress(downloadSize, totalSize);

                }
                Log.d("TAG", "File downloaded");
                fileOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
