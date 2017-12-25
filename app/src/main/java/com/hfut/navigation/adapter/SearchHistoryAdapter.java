package com.hfut.navigation.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hfut.navigation.R;
import com.hfut.navigation.bean.SearchHistoryBean;
import com.hfut.navigation.util.ConstantUtil;
import com.hfut.navigation.util.UserDataUtil;
import com.hfut.navigation.viewholder.SearchHistoryViewHolder;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryViewHolder> {

    private final Object mLock = new Object();
    private LayoutInflater mInflater;
    private SearchHistoryAdapter.OnItemClickListener mOnItemClickListener = null;
    private Context mContext;
    private ArrayList<SearchHistoryBean> mOriginalValues;// 所有的Item
    private List<SearchHistoryBean> mObjects;// 过滤后的Item
    private int mMaxMatch = -1;// 最多显示多少个选项,负数表示全部

    public SearchHistoryAdapter(Context context) {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        initSearchHistory();
        mObjects = mOriginalValues;
    }

    public void setOnItemClickListener(SearchHistoryAdapter.OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public SearchHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.app_search_history_item, parent, false);
        return new SearchHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SearchHistoryViewHolder holder, final int position) {
        SearchHistoryBean data = mObjects.get(position);
        holder.tv_HistoryContent.setText(data.getContent());

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    /**
     * 读取历史搜索记录
     */
    private void initSearchHistory() {
        String longHistory = UserDataUtil.loadHistoryData(mContext);
        mOriginalValues = new ArrayList<>();
        if (longHistory == null || longHistory.equals("")) {
            return;
        }
        String[] hisArrays = longHistory.split(",");
        for (String hisArray : hisArrays) {
            SearchHistoryBean bean = new SearchHistoryBean();
            bean.setContent(hisArray);
            mOriginalValues.add(bean);
        }
    }



    public SearchHistoryBean getItem(int position) {
        return mObjects.get(position);
    }

    /**
     * 匹配过滤搜索内容
     *
     * @param prefix 输入框中输入的内容
     */
    public void performFiltering(CharSequence prefix) {
        if (prefix == null || prefix.length() == 0) {//搜索框内容为空的时候显示所有历史记录
            synchronized (mLock) {
                mObjects = mOriginalValues;
            }
        } else {
            String prefixString = prefix.toString().toLowerCase();
            int count = mOriginalValues.size();
            ArrayList<SearchHistoryBean> newValues = new ArrayList<>(
                    count);
            for (int i = 0; i < count; i++) {
                final String value = mOriginalValues.get(i).getContent();
                final String valueText = value.toLowerCase();
                if (valueText.contains(prefixString)) {

                }
                if (valueText.startsWith(prefixString)) {
                    newValues.add(new SearchHistoryBean().setContent(valueText));
                } else {
                    final String[] words = valueText.split(" ");
                    for (String word : words) {
                        if (word.startsWith(prefixString)) {
                            newValues.add(new SearchHistoryBean()
                                    .setContent(value));
                            break;
                        }
                    }
                }
                if (mMaxMatch > 0) {
                    if (newValues.size() > mMaxMatch - 1) {
                        break;
                    }
                }
            }
            mObjects = newValues;
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return null == mObjects ? 0 : mObjects.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
