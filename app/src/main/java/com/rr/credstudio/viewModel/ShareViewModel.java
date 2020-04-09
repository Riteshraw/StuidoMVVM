package com.rr.credstudio.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.rr.credstudio.model.Studio;
import com.rr.credstudio.repo.StudioRepo;
import java.util.List;

public class ShareViewModel extends AndroidViewModel {
    private final Application context;
    private MutableLiveData<List<Studio>> studioLiveData;
    private MutableLiveData<List<Studio>> studioLiveData2;
    private List<Studio> studioList;
    private final MutableLiveData<Studio> selected = new MutableLiveData<Studio>();
    private StudioRepo studioRepo;
    private int position;


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
        studioList = studioRepo.getNormalSongsList();
    }

    public MutableLiveData<List<Studio>> getStudioRepository() {
        return studioLiveData;
    }


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

    public void onDownloadClick(int position) {
        studioRepo.getSongDwn(context, studioList, position);
    }

    public void dwnAll() {
        for (int i = 0; i < studioList.size(); i++) {
            onDownloadClick(i);
        }
    }

    public LiveData<Studio> getSelected() {
        return selected;
    }

}
