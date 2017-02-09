package com.bwie.moonlighttea.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bwie.moonlighttea.R;

/**
 * 类描述:
 * 作者：Administrator
 * 时间:2017/2/9 20:02
 */

public class FragmentTab extends Fragment {
    TextView mTvText;

    private View mViewContent; // 缓存视图内容


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getContentView(inflater,container);
    }

    private View getContentView(LayoutInflater inflater, @Nullable ViewGroup container){
        if (mViewContent == null) {//判断一下视图是否被创建，无则创建一下
            mViewContent = inflater.inflate(R.layout.fragment_tab, container, false);
        }

        // 缓存View判断是否含有parent, 如果有需要从parent删除, 否则发生已有parent的错误.
        ViewGroup parent = (ViewGroup) mViewContent.getParent();
        if (parent != null) {
            parent.removeView(mViewContent);
        }

        if (mTvText==null){
            mTvText=(TextView)mViewContent.findViewById(R.id.tab_tv_text);
        }

        return mViewContent;
    }
    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 显示Fragment的Tag信息
        mTvText.setText(String.valueOf("Page: " + getTag()));
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
    }
}
