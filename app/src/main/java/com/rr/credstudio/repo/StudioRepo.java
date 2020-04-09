package com.rr.credstudio.repo;

import android.app.Application;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.rr.credstudio.DownloadService;
import com.rr.credstudio.model.Studio;
import com.rr.credstudio.RetrofitService;
import com.rr.credstudio.StudioAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class StudioRepo {
    private static StudioRepo studioRepo;
    private static StudioAPI studioAPI;
    private static MutableLiveData<Studio> studioList2;
    private static MutableLiveData<List<Studio>> studioList3;
    private static List<Studio> studioList;
    private static MutableLiveData<List<Studio>> studioListMain;

    public static StudioRepo getInstance() {
        if (studioRepo == null) {
            studioRepo = new StudioRepo();
        }
        return studioRepo;
    }

    public StudioRepo() {
        studioAPI = RetrofitService.createService(StudioAPI.class);
    }

    public MutableLiveData<List<Studio>> getSongsList() {
        studioListMain = new MutableLiveData<>();

        studioAPI.getSongList().enqueue(new Callback<List<Studio>>() {
            @Override
            public void onResponse(Call<List<Studio>> call, Response<List<Studio>> response) {
                if (response.isSuccessful()) {
                    studioListMain.postValue(response.body());
                    //    studio.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Studio>> call, Throwable t) {
                studioListMain.setValue(null);
            }
        });

        return studioListMain;
    }

    public List<Studio> getNormalSongsList() {
        List<Studio> studioList = new ArrayList<>();

        studioAPI.getSongList().enqueue(new Callback<List<Studio>>() {
            @Override
            public void onResponse(Call<List<Studio>> call, Response<List<Studio>> response) {
                if (response.isSuccessful()) {
                    studioList.addAll(response.body());
                    //    studio.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Studio>> call, Throwable t) {
                studioList.addAll(null);
            }
        });

        return studioList;
    }

    public MutableLiveData<Studio> getSongDwn(Application context, Studio studio) {
        studioList2 = new MutableLiveData<>();

        Intent intent = new Intent(context, StudioRepo.DownloadRService.class);
        intent.putExtra("STUDIO", (Serializable) studio);
        context.startService(intent);

        studioList2.setValue(studio);

        return studioList2;
    }

    public MutableLiveData<List<Studio>> getSongDwn(Application context, List<Studio> studioList, int position) {
        studioList2 = new MutableLiveData<>();
        studioList3 = new MutableLiveData<>();

        this.studioList = studioList;

        Intent intent = new Intent(context, StudioRepo.DownloadRService.class);
        intent.putExtra("POSITION", position);
        context.startService(intent);

        studioList3.setValue(studioList);

        return studioList3;
    }

    public static class DownloadRService extends Service {

        public DownloadRService() {
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
//                Log.v("URL", "handler message " + msg.getData().getString("FILENAME"));
//                Log.v("URL", "handler message " + msg.getData().getString("URL"));
                initDownload((msg.getData().getInt("POSITION")), msg.arg1);
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
//            List<Studio> studioList = (List<Studio>) intent.getSerializableExtra("STUDIO");
            int position = intent.getIntExtra("POSITION", 0);
//            bundle.putSerializable("STUDIO", (Serializable) studioList);
            bundle.putSerializable("POSITION", position);

//            bundle.putString("URL", studio.getUrl());
//            bundle.putString("FILENAME", studio.getUrl());
//            Log.v("URL", "onStartCommand" + studioList.get(position).getUrl());
//            Log.v("URL", "onStartCommand" + studioList.get(position).getSong());

            msg.setData(bundle);
            mServiceHandler.sendMessage(msg);

            // If we get killed, after returning from here, restart
            return START_STICKY;
        }

        private int totalFileSize;

        private void initDownload(int position, int id) {
            Studio studio = studioList.get(position);
            studioAPI.getSongDwn(studio.getUrl()).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "server contacted and has file");

                        boolean writtenToDisk = writeResponseBodyToDisk(response.body(), studio, position);

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

        private boolean writeResponseBodyToDisk(ResponseBody body, Studio studio, int position) {
            try {
                // todo change the file location/name according to your needs
                File futureStudioIconFile = new File(getExternalFilesDir(null) + File.separator + studio.getSong() + ".mp3");
                Log.v("URL", getExternalFilesDir(null) + File.separator + studio.getSong() + ".mp3");

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
                        int progress = (int) ((fileSizeDownloaded * 100) / fileSize);

                        if (progress % 5 == 0) {
                            studio.setProgress(progress);
                            studioList.set(position, studio);
                            studioListMain.setValue(studioList);
                        }

                        Log.v("URL", "" + progress);

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
    }

}
