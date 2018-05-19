package com.example.student.weatherapi;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

import static java.net.Proxy.Type.HTTP;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyActivity";


    Handler handler;


    Thread currentThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView tv = findViewById(R.id.status);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                int n = (int)Math.round(100.0/(msg.arg1*1.0 /(int)(msg.obj)));
                ProgressBar pb = findViewById(R.id.progressBar);
                pb.setProgress(n);
                tv.setText("" + (int)msg.obj/1048576 +"Mb\n" + n +"%");
            }
        };
    }

    public void onClick(View view) {
        findViewById(R.id.status).setVisibility(View.VISIBLE);
        currentThread = new ThreadDownloader();
        TextView sb = (TextView) findViewById(R.id.stop);
        sb.setText("Stop");
        currentThread.start();
    }

    public void ClickStop(View view) {
        currentThread.interrupt();
        TextView sb = (TextView) findViewById(R.id.stop);
        sb.setText("Downloading stopped");

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
    public class ThreadDownloader extends Thread {
        @Override
        public void run() {
            super.run();
            Log.d(TAG, "Starting thread...");
            try {
                URL url = new URL("https://speed.hetzner.de/100MB.bin");
                HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();

                InputStream inputStream = urlConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

                File SDCardRoot = Environment.getExternalStorageDirectory();
                Log.e("SD", SDCardRoot.getAbsolutePath());

                File sdPath = new File(SDCardRoot.getAbsolutePath() + "/" + "Downloaded");
                sdPath.mkdirs();
                File file = new File(sdPath, System.currentTimeMillis() + "file");
                Log.d("PATH", file.getAbsolutePath());

                FileOutputStream fileOutput = new FileOutputStream(file);

                urlConnection.setRequestMethod("GET");

                if (urlConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    Log.d("CON", "Connection set. Code:" + HttpsURLConnection.HTTP_OK);
                } else {
                    Log.e("CON", "Connection error");
                }

                byte[] buffer = new byte[1024];
                int bufferLength;
                int totalSize = urlConnection.getContentLength();
                int downloadedSize = 0;
                Log.d("FILE", "totalSize: " + totalSize);

                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                    downloadedSize += bufferLength;

                    Message msg = new Message();
                    msg.obj = downloadedSize;
                    msg.arg1 = totalSize;
                    handler.sendMessage(msg);

                    if (isInterrupted()) {
                        file.delete();
                        break;
                    }

                }
                Log.d("FILE", "DownloadedSize: " + downloadedSize);
                Log.d("TAG", "File downloaded");
                fileOutput.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("BLET", "Neponyatnaya hernia brat");
            }
        }
    }
}

