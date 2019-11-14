package com.rr.credstudio.ViewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.rr.credstudio.Model.Studio;
import com.rr.credstudio.Repo.StudioRepo;

import java.util.List;

public class ShareViewModel extends ViewModel {
    private MutableLiveData<List<Studio>> studioLiveData;
    private StudioRepo studioRepo;

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
}
