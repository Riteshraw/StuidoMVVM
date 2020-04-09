package com.rr.credstudio;


import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class DownloadService extends Service {
    public DownloadService() {
        super();
    }

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Context x = getApplicationContext();
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            /*notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setContentTitle("Downloading")
                    .setContentText("Please wait...")
                    .setAutoCancel(true);
            notificationManager.notify(x.ID, notificationBuilder.build());
            Log.i("Paras", "onHandleIntent: " + x.filename + x.url + "  " + x.ID);*/
//            initDownload(x.filename, x.url, x.ID);
            Log.v("URL", "handler message " + msg.getData().getString("FILENAME"));
            Log.v("URL", "handler message " + msg.getData().getString("URL"));

            initDownload(msg.getData().getString("FILENAME"), msg.getData().getString("URL"), msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Get the HandlerThread's Looper and use it for our Handler
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        HandlerThread thread = new HandlerThread("ServiceStartArguments"/*, Process.THREAD_PRIORITY_BACKGROUND*/);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        Context x = getApplicationContext();
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        Bundle bundle = new Bundle();
        bundle.putString("FILENAME", String.valueOf(intent.getExtras().get("FILENAME")));
        bundle.putString("URL", String.valueOf(intent.getExtras().get("URL")));
        Log.v("URL", "onStartCommand" + String.valueOf(intent.getExtras().get("FILENAME")));
        Log.v("URL", "onStartCommand" + String.valueOf(intent.getExtras().get("URL")));

        msg.setData(bundle);
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    private int totalFileSize;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;

    private void initDownload(String filename, String fileUrl, int id) {

        StudioAPI downloadService = RetrofitService.createService(StudioAPI.class);
        Call<ResponseBody> call = downloadService.getSongDwn(fileUrl);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "server contacted and has file");

                    boolean writtenToDisk = writeResponseBodyToDisk(response.body(), filename);

                    Log.d(TAG, "file download was a success? " + writtenToDisk);
                } else {
                    Log.d(TAG, "server contact failed");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "error");
            }
        });


    }

    private boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + fileName + ".mp3");
            Log.v("URL", getExternalFilesDir(null) + File.separator + fileName + ".mp3");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {

    }
}
