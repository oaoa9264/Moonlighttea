package com.bwie.moonlighttea;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import com.bwie.moonlighttea.fragment.FragmentTab;

public class MainActivity extends AppCompatActivity {
    NewsFragmentTabHost mTabHost;
    TextView title;

    //图片
    private int mImages[] = {
            R.drawable.tab_home,
            R.drawable.tab_tuan,
            R.drawable.tab_cart,
            R.drawable.tab_my
    };

    //标题
    private String mFragmentTags[] = {
            "商品",
            "天天",
            "购物车",
            "我的"
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title=(TextView)findViewById(R.id.title);
        mTabHost = (NewsFragmentTabHost) findViewById(R.id.tabhost);

        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);


        mTabHost.getTabWidget().setDividerDrawable(null); // 去掉分割线

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                Log.d(getLocalClassName(),tabId);
                title.setText(tabId);


            }
        });

        //初始化TAB
        Bundle bundle = null;
        for (int i = 0; i < mImages.length; i++) {
            // Tab按钮添加文字和图片
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec(mFragmentTags[i]).setIndicator(getImageView(i,mFragmentTags[i]));
            // 添加Fragment
            bundle = new Bundle();
            mTabHost.addTab(tabSpec, FragmentTab.class, bundle);
            // 设置Tab按钮的背景
            mTabHost.getTabWidget().getChildAt(i).setBackgroundResource(R.color.pedo_actionbar_bkg);
        }
    }

    // 获得图片资源 设置初始化指示器
    private View getImageView(int index, String str) {
        @SuppressLint("InflateParams")
        View view = getLayoutInflater().inflate(R.layout.view_tab_indicator, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.tab_iv_image);
        TextView label=(TextView)view.findViewById(R.id.tab_label);
        label.setTextSize(10);
        label.setText(str);

        imageView.setImageResource(mImages[index]);
        return view;
    }
}
