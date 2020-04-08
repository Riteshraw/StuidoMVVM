package com.rr.credstudio.viewModel;

import android.app.Application;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rr.credstudio.DownloadService;
import com.rr.credstudio.model.Studio;
import com.rr.credstudio.repo.StudioRepo;

import java.util.List;

import javax.inject.Inject;

public class ShareViewModel extends AndroidViewModel {
    private final Application context;
    private MutableLiveData<List<Studio>> studioLiveData;
    private final MutableLiveData<Studio> selected = new MutableLiveData<Studio>();
    private StudioRepo studioRepo;
    private int position;

    @Inject
    public ShareViewModel(@NonNull Application application) {
        super(application);
        context = application;
    }

    public void init() {
        if (studioLiveData != null) {
            return;
        }
        studioRepo = StudioRepo.getInstance();
        studioLiveData = studioRepo.getSongsList();
    }

    public MutableLiveData<List<Studio>> getStudioRepository() {
        return studioLiveData;
    }

    /*public void onStudioItemClick(Studio studio) {
        Toast.makeText(context, "" + studio.getSong(), Toast.LENGTH_SHORT).show();
        //FragmentManager fm = context.get
    }*/

    public void select(int position) {
        selected.setValue(studioLiveData.getValue().get(position));
        this.position = position;
    }

    public void onNextClick(Studio studio) {
        select(position == studioLiveData.getValue().size() - 1 ? 0 : position + 1);
    }

    public void onPrevClick(Studio studio) {
        select(position == 0 ? 0 : position - 1);
    }

    public void onDownloadClick(Studio studio, int position) {
        Intent intent = new Intent(context,DownloadService.class);
        intent.putExtra("FILENAME", studioLiveData.getValue().get(position).getSong());
        intent.putExtra("URL", studioLiveData.getValue().get(position).getUrl());
        context.startService(intent);
    }

    public LiveData<Studio> getSelected() {
        return selected;
    }
}
