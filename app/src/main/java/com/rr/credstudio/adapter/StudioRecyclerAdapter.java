package com.rr.credstudio.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.rr.credstudio.ActivityPresenter;
import com.rr.credstudio.model.Studio;
import com.rr.credstudio.R;
import com.rr.credstudio.viewModel.ShareViewModel;
import com.rr.credstudio.databinding.StudioListItemBinding;

import java.util.List;

import javax.inject.Inject;

public class StudioRecyclerAdapter extends RecyclerView.Adapter<StudioRecyclerAdapter.StudioViewHolder> {
    private final LayoutInflater mInflater;
    private final Context context;
    private final ShareViewModel viewModel;
    private final ActivityPresenter activityPresenter;
    private List<Studio> studioList;

    public StudioRecyclerAdapter(Context context, ShareViewModel viewModel, ActivityPresenter activityPresenter) {
        this.context = context;
        this.viewModel = viewModel;
        this.activityPresenter = activityPresenter;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public StudioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        StudioListItemBinding binding = DataBindingUtil.inflate(mInflater, R.layout.studio_list_item, parent, false);
        return new StudioViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(StudioViewHolder holder, int position) {
        if (studioList != null) {
            holder.bind(viewModel, position);
        } else {
            // Covers the case of data not being ready yet
            Toast.makeText(context, "No Songs", Toast.LENGTH_SHORT).show();
        }
    }

    public void setStudioList(List<Studio> studioList) {
        this.studioList = studioList;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // superTabs has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        return studioList == null ? 0 : studioList.size();
    }

    class StudioViewHolder extends RecyclerView.ViewHolder {
        final private StudioListItemBinding binding;

        private StudioViewHolder(StudioListItemBinding recyclerviewItemBinding) {
            super(recyclerviewItemBinding.getRoot());
            binding = recyclerviewItemBinding;
        }

        private void bind(ShareViewModel viewModel, Integer position) {
//            binding.setVariable(BR., studioList.get(position));
//            binding.setVariable(BR.position, position);
//            binding.setVariable(BR.superTabVM, viewModel);
            //OR like below, both produce same result
            binding.setPosition(position);
            binding.setStudio(studioList.get(position));
            binding.setStudioVM(viewModel);
            binding.setPresenter(activityPresenter);
            binding.executePendingBindings();
        }
    }
}
