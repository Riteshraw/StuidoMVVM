package com.rr.credstudio.Repo;

import androidx.lifecycle.MutableLiveData;

import com.rr.credstudio.Model.Studio;
import com.rr.credstudio.RetrofitService;
import com.rr.credstudio.StudioAPI;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudioRepo {
    private static StudioRepo studioRepo;
    private final StudioAPI studioAPI;

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
//        MutableLiveData<Studio> studio = new MutableLiveData<>();
        MutableLiveData<List <Studio>> studioList = new MutableLiveData<>();

        studioAPI.getSongList().enqueue(new Callback<List<Studio>>() {
            @Override
            public void onResponse(Call<List<Studio>> call, Response<List<Studio>> response) {
                if (response.isSuccessful()) {
                    studioList.postValue(response.body());
                    int x =1;
                //    studio.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<Studio>> call, Throwable t) {
                studioList.setValue(null);
            }
        });

        /*studioAPI.getSongList().enqueue(new Callback<Studio>() {
            @Override
            public void onResponse(Call<Studio> call, Response<Studio> response) {
                if (response.isSuccessful()) {
                    studio.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Studio> call, Throwable t) {
                studio.setValue(null);
            }
        });*/

        return studioList;
    }

}
