package com.rr.credstudio;

import com.rr.credstudio.model.Studio;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface StudioAPI {
    @GET("studio")
    Call<List <Studio>> getSongList();

    @GET
    Call<ResponseBody> downloadFileWithDynamicUrlSync(@Url String fileUrl);
}
