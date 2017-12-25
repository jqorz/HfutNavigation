package com.hfut.navigation.frame.main;

import android.content.Intent;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hfut.navigation.R;
import com.hfut.navigation.adapter.SearchHistoryAdapter;
import com.hfut.navigation.base.BaseActivity;
import com.hfut.navigation.bean.SearchHistoryBean;
import com.hfut.navigation.util.ConstantUtil;
import com.hfut.navigation.util.ToastUtil;
import com.hfut.navigation.util.UserDataUtil;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class App_Search extends BaseActivity {

    @BindView(R.id.edtTxt_Search)
    EditText edtTxt_Search;
    @BindView(R.id.mRecyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_History_Tip)
    TextView tv_History_Tip;
    SearchHistoryAdapter mAdapter;
    @BindView(R.id.iv_Search)
    ImageView iv_Search;
    @BindView(R.id.iv_Delete)
    ImageView iv_Delete;

    @Override
    protected void init() {
        initRecyclerView();
        initEditText();
        if (UserDataUtil.loadHistoryData(this).equals("")) {
            tv_History_Tip.setVisibility(View.GONE);
        } else {
            tv_History_Tip.setVisibility(View.VISIBLE);
        }
    }


    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mAdapter = new SearchHistoryAdapter(this);
        mAdapter.setOnItemClickListener(new SearchHistoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SearchHistoryBean data = mAdapter.getItem(position);
                String d = data.getContent();
                edtTxt_Search.setText(d);
                edtTxt_Search.setSelection(d.length());
            }
        });
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initEditText() {
        edtTxt_Search.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                /**
                 * 如果用户点击了手机键盘的enter键，响应搜索按钮的点击事件
                 */
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    iv_Search.performClick();
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * 将搜索记录保存到本地
     */
    private void saveSearchHistory(String text) {
        if (text.length() < 1) {
            return;
        }

        String longHistory = UserDataUtil.loadHistoryData(this);
        String[] tmpHistory = longHistory.split(",");
        ArrayList<String> history = new ArrayList<>(
                Arrays.asList(tmpHistory));
        if (history.size() > 0) {
            int i;
            for (i = 0; i < history.size(); i++) {
                if (text.equals(history.get(i)) || history.get(i).equals("")) {
                    history.remove(i);
                    break;
                }
            }
            history.add(0, text);//将最近搜索的(即使为重复的)置为最前端
        }
        if (history.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < history.size(); i++) {
                sb.append(history.get(i)).append(",");
            }
            UserDataUtil.updateHistoryData(this, ConstantUtil.sp_SearchHistory, sb.toString());
        } else {
            UserDataUtil.updateHistoryData(this, ConstantUtil.sp_SearchHistory, text + ",");
        }
    }

    /**
     * 检查是否包含非法字符
     *
     * @param input 输入
     * @return 是否包含
     */
    private boolean checkInput(String input) {
        char[] c = ConstantUtil.IllegalChar.toCharArray();
        for (char cc : c) {
            if (input.indexOf(cc) != -1)//返回-1表示不包含此字符
                return false;
        }
        return true;
    }

    private void goSearch(String string) {
        hideSoftKeyboard();
        if (string.length() > 0) {
            edtTxt_Search.setText(string);
        }
        this.setResult(0, new Intent().putExtra(ConstantUtil.CODE_SearchContent, string));
        this.finish();
    }


    /**
     * 显示键盘
     */
    public void showSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.RESULT_SHOWN);
    }

    /**
     * 隐藏键盘
     */
    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @OnTextChanged(R.id.edtTxt_Search)
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mAdapter.performFiltering(s);
    }

    @OnTextChanged(R.id.edtTxt_Search)
    public void afterTextChanged(Editable s) {
        /**
         * 根据文本款的内容是否为空，隐藏和显示删除按钮
         */
        if (s.length() == 0) {
            iv_Delete.setVisibility(View.GONE);
        } else {
            iv_Delete.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.iv_Search, R.id.iv_Delete, R.id.iv_Back, R.id.tv_History_Tip})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_Back:
                this.finish();
                break;
            case R.id.iv_Search:
                String input = edtTxt_Search.getText().toString().trim();
                if (checkInput(input)) {
                    saveSearchHistory(input);//保存输入记录
                    goSearch(input);
                } else {
                    ToastUtil.showToast(this, "非法字符");
                }
                break;
            case R.id.iv_Delete:
                edtTxt_Search.setText("");
                showSoftKeyboard();
                break;
            case R.id.tv_History_Tip:
                UserDataUtil.updateHistoryData(this, ConstantUtil.sp_SearchHistory, "");
                tv_History_Tip.setVisibility(View.GONE);
                initRecyclerView();
                break;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.app_search;
    }
}
