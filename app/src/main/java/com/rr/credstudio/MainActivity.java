package com.rr.credstudio;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rr.credstudio.Utils.NetworkUtil;
import com.rr.credstudio.adapter.StudioRecyclerAdapter;
import com.rr.credstudio.fragment.StudioFragment;
import com.rr.credstudio.model.Studio;
import com.rr.credstudio.viewModel.ShareViewModel;
import com.rr.credstudio.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ArrayList<Studio> studioArrayList = new ArrayList<>();
    StudioRecyclerAdapter studioAdapter;
    ShareViewModel shareViewModel;
    private Context context;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkUtil.netAlert(context)) {
            if (shareViewModel != null)
                shareViewModel.init();
            else{
                initialise();
            }
        }
    }

    private void initialise() {
        shareViewModel = ViewModelProviders.of(this).get(ShareViewModel.class);
        shareViewModel.init();
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        recyclerView = binding.recyclerView;
        shareViewModel.getStudioRepository().observe(this, (Observer<List<Studio>>) studioList -> {
            studioAdapter.setStudioList(studioList);
        });
        binding.setStudioVM(shareViewModel);
        setupRecyclerView();
    }

    private void setupRecyclerView() {
        ActivityPresenter ap = new ActivityPresenter() {
            @Override
            public void onItemClick(Studio studio, int position) {
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.container, new StudioFragment());
                ft.addToBackStack(studio.getSong());
                ft.commit();
                shareViewModel.select(position);
            }
        };

        if (studioAdapter == null) {
            studioAdapter = new StudioRecyclerAdapter(MainActivity.this, shareViewModel, ap);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(studioAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setNestedScrollingEnabled(true);

        }

    }

}
