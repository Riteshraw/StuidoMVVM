package com.rr.credstudio;

import com.rr.credstudio.Model.Studio;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface StudioAPI {
    @GET("studio")
    Call<List <Studio>> getSongList();
}
