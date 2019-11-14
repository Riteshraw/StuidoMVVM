package com.rr.credstudio;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.rr.credstudio.Adapter.StudioRecyclerAdapter;
import com.rr.credstudio.Model.Studio;
import com.rr.credstudio.ViewModel.ShareViewModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<Studio> articleArrayList = new ArrayList<>();
    StudioRecyclerAdapter studioAdapter;
    RecyclerView rvHeadline;
    ShareViewModel shareViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shareViewModel = ViewModelProviders.of(this).get(ShareViewModel.class);
        shareViewModel.init();
        shareViewModel.getStudioRepository().observe(this, (Observer<List <Studio>>) studioList -> {
            articleArrayList.addAll(studioList);
            //studioAdapter.notifyDataSetChanged();
        });

        //setupRecyclerView();

    }
}
