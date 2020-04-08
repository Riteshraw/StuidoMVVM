package com.rr.credstudio;

import com.rr.credstudio.model.Studio;

import javax.inject.Inject;

public interface ActivityPresenter {
    public void onItemClick(Studio studio, int position);
}
