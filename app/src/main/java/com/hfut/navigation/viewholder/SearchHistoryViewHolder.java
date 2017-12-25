package com.hfut.navigation.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.hfut.navigation.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchHistoryViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.tv_HistoryContent)
    public TextView tv_HistoryContent;

    public SearchHistoryViewHolder(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }
}